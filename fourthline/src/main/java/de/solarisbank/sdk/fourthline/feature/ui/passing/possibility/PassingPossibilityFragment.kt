package de.solarisbank.sdk.fourthline.feature.ui.passing.possibility

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import de.solarisbank.identhub.fourthline.R
import de.solarisbank.identhub.R as CoreR
import de.solarisbank.identhub.session.main.BaseFragment
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.fourthline.FourthlineFlow
import de.solarisbank.sdk.fourthline.domain.dto.PersonDataStateDto
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.kyc.info.KycSharedViewModel
import org.koin.androidx.navigation.koinNavGraphViewModel

class PassingPossibilityFragment : BaseFragment() {

    private var progressBar: ProgressBar? = null
    private val kycSharedViewModel: KycSharedViewModel by koinNavGraphViewModel(FourthlineFlow.navigationId)
    private val activityViewModel: FourthlineViewModel by koinNavGraphViewModel(FourthlineFlow.navigationId)

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.identhub_fragment_passing_possibility, container, false)
                .also {
                    progressBar = it.findViewById(R.id.progressBar)
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityViewModel.navigator = navigator
        kycSharedViewModel.fetchPersonDataAndIp()
        kycSharedViewModel.passingPossibilityLiveData.observe(viewLifecycleOwner) { processState(it) }
    }

    override fun customizeView(view: View) {
        progressBar?.customize(customization)
    }

    private fun processState(personDataStateDto: PersonDataStateDto) {
        when(personDataStateDto) {
            is PersonDataStateDto.UPLOADING -> {
                progressBar!!.visibility = View.VISIBLE
            }
            is PersonDataStateDto.SUCCEEDED -> {
                progressBar!!.visibility = View.INVISIBLE
                activityViewModel.onPassingPossibilityOutcome(PassingPossibilityOutcome.Success)
            }
            is PersonDataStateDto.EMPTY_DOCS_LIST_ERROR -> {
                progressBar!!.visibility = View.INVISIBLE
                showAlertFragment(
                        title = getString(R.string.identhub_empty_id_doc_list_title),
                        message = getString(R.string.identhub_empty_id_doc_list_message),
                        positiveLabel = getString(CoreR.string.identhub_ok_button),
                        positiveAction = {
                            activityViewModel.onPassingPossibilityOutcome(
                                PassingPossibilityOutcome.Failed(
                                    "User closed the SDK because their ID document was not supported"
                                )
                            )
                        }
                )
            }
            is PersonDataStateDto.GENERIC_ERROR -> {
                progressBar!!.visibility = View.INVISIBLE
                showGenericErrorWithRetry(
                    retryAction = { kycSharedViewModel.fetchPersonDataAndIp() },
                    quitAction = {
                        activityViewModel.onPassingPossibilityOutcome(
                            PassingPossibilityOutcome.Failed(
                                "Generic error happened when loading initial info of Fourthline identification"
                            )
                        )
                    }
                )
            }
            else -> { /* Ignore */ }
        }
    }

    override fun onDestroyView() {
        progressBar = null
        super.onDestroyView()
    }
}

sealed class PassingPossibilityOutcome {
    object Success: PassingPossibilityOutcome()
    data class Failed(val message: String): PassingPossibilityOutcome()
}


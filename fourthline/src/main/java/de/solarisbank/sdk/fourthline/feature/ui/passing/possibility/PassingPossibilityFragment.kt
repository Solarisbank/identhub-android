package de.solarisbank.sdk.fourthline.feature.ui.passing.possibility

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import de.solarisbank.identhub.session.IdentHub.SESSION_URL_KEY
import de.solarisbank.sdk.feature.base.BaseActivity
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.fourthline.R
import de.solarisbank.sdk.fourthline.base.FourthlineFragment
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent
import de.solarisbank.sdk.fourthline.domain.dto.PersonDataStateDto
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.kyc.info.KycSharedViewModel

class PassingPossibilityFragment : FourthlineFragment() {

    private var progressBar: ProgressBar? = null

    private val kycSharedViewModel: KycSharedViewModel by lazy {
        ViewModelProvider(requireActivity(), (requireActivity() as FourthlineActivity)
                .viewModelFactory)[KycSharedViewModel::class.java]
    }

    private val activityViewModel: FourthlineViewModel by lazy {
        ViewModelProvider(requireActivity(), (requireActivity() as BaseActivity).viewModelFactory)
                .get(FourthlineViewModel::class.java)
    }

    override fun inject(component: FourthlineFragmentComponent) {
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.identhub_fragment_passing_possibility, container, false)
                .also {
                    progressBar = it.findViewById(R.id.progressBar)
                    customizeUI()
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        kycSharedViewModel.fetchPersonDataAndIp(requireActivity().intent.getStringExtra(SESSION_URL_KEY)!!)
        kycSharedViewModel.passingPossibilityLiveData.observe(viewLifecycleOwner, { processState(it) })
    }

    private fun customizeUI() {
        progressBar?.customize(customization)
    }

    private fun processState(personDataStateDto: PersonDataStateDto) {
        when(personDataStateDto) {
            is PersonDataStateDto.UPLOADING -> {
                progressBar!!.visibility = View.VISIBLE
            }
            is PersonDataStateDto.SUCCEEDED -> {
                progressBar!!.visibility = View.INVISIBLE
                activityViewModel.navigateFromPassingPossibilityToTc()
            }
            is PersonDataStateDto.EMPTY_DOCS_LIST_ERROR -> {
                progressBar!!.visibility = View.INVISIBLE
                showAlertFragment(
                        title = getString(R.string.identhub_empty_id_doc_list_title),
                        message = getString(R.string.identhub_empty_id_doc_list_message),
                        positiveLabel = getString(R.string.identhub_ok_button),
                        positiveAction = {
                            activityViewModel.setFourthlineIdentificationFailure()
                        }
                )
            }
            is PersonDataStateDto.GENERIC_ERROR -> {
                progressBar!!.visibility = View.INVISIBLE
                showAlertFragment(
                        title = getString(R.string.identhub_generic_error_title),
                        message = getString(R.string.identhub_generic_error_message),
                        positiveLabel = getString(R.string.identhub_ok_button),
                        positiveAction = {
                            activityViewModel.setFourthlineIdentificationFailure()
                        }
                )
            }
        }
    }

    override fun onDestroyView() {
        progressBar = null
        super.onDestroyView()
    }
}


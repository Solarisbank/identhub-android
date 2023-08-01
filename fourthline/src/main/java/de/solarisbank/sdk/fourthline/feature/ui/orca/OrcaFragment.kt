package de.solarisbank.sdk.fourthline.feature.ui.orca

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fourthline.kyc.KycInfo
import com.fourthline.orca.Orca
import com.fourthline.orca.core.flavor.OrcaColors
import com.fourthline.orca.core.flavor.OrcaFlavor
import com.fourthline.orca.core.flavor.OrcaPalette
import com.fourthline.orca.kyc.KycConfig
import com.fourthline.orca.kyc.KycCustomizationConfig
import com.fourthline.orca.kyc.KycError
import com.fourthline.orca.kyc.kyc
import de.solarisbank.identhub.session.main.BaseFragment
import de.solarisbank.sdk.fourthline.FourthlineFlow
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.logger.IdLogger
import org.koin.androidx.navigation.koinNavGraphViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrcaFragment: BaseFragment() {

    private val activityViewModel: FourthlineViewModel by koinNavGraphViewModel(FourthlineFlow.navigationId)
    private val viewModel: OrcaViewModel by viewModel()

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = View(inflater.context)
        initView()
        return view
    }

    private fun initView() {
        viewModel.state().observe(viewLifecycleOwner) {
            when (val state = it.orcaState) {
                is OrcaState.Unknown -> { /* Nothing to do */ }
                is OrcaState.StartOrca -> { launchOrca(state.kycConfig, state.kycInfo) }
                is OrcaState.Done -> { activityViewModel.onOrcaOutcome(OrcaOutcome.Success) }
            }
        }
    }

    private fun launchOrca(kycConfig: KycConfig, kycInfo: KycInfo) {
        val lightColors = OrcaColors(OrcaPalette(isLight = true, primary = OrcaColors.OrcaColor.FromInt(customization.colors.primary)))
        val darkColors = OrcaColors(OrcaPalette(isLight = false, primary = OrcaColors.OrcaColor.FromInt(customization.colors.primaryDark)))
        val orcaFlavor = OrcaFlavor(colorsLight = lightColors, colorsDark = darkColors)
        val customizationConfig = KycCustomizationConfig(flavor = orcaFlavor, kycInfo = kycInfo)

        Orca.kyc(requireContext())
            .configure(kycConfig)
            .customize(customizationConfig)
            .present {
                it.onSuccess { result ->
                    IdLogger.info("Orca successful")
                    viewModel.onAction(OrcaAction.OrcaCompleted(result))
                }
                it.onFailure { throwable ->
                    val throwableMessage = if (throwable is KycError) {
                        throwable.javaClass.name
                    } else {
                        throwable.message ?: "Unknown error"
                    }
                    val errorMessage = "Orca Failure: $throwableMessage"
                    IdLogger.error(errorMessage)
                    activityViewModel.onOrcaOutcome(OrcaOutcome.Failure(errorMessage))
                }
            }
    }
}

sealed class OrcaOutcome {
    object Success: OrcaOutcome()
    data class Failure(val message: String): OrcaOutcome()
}
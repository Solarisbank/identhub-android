package de.solarisbank.sdk.fourthline.feature.ui.orca

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourthline.core.CountryNetworkModel
import com.fourthline.kyc.KycInfo
import com.fourthline.orca.kyc.KycConfig
import com.fourthline.orca.kyc.KycDocumentFlowConfig
import com.fourthline.orca.kyc.KycFlow
import com.fourthline.orca.kyc.KycTinFlowConfig
import de.solarisbank.sdk.data.utils.IdenthubDispatchers
import de.solarisbank.sdk.data.utils.update
import de.solarisbank.sdk.fourthline.data.FourthlineStorage
import de.solarisbank.sdk.fourthline.data.kyc.storage.KycInfoDataSource
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrcaViewModel(
    private val fourthlineStorage: FourthlineStorage,
    private val kycInfoDataSource: KycInfoDataSource,
    private val dispatchers: IdenthubDispatchers
): ViewModel() {
    private val viewState = MutableLiveData<OrcaViewState>()

    fun state(): LiveData<OrcaViewState> = viewState

    init {
        viewState.value = OrcaViewState(
            orcaState = OrcaState.Unknown
        )
        prepareOrca()
    }

    fun onAction(action: OrcaAction) {
        when (action) {
            is OrcaAction.OrcaCompleted -> {
                processOrcaResult(action.kycInfo)
            }
        }
    }

    private fun prepareOrca() {
        val supportedCountries = CountryNetworkModel.create(fourthlineStorage.rawDocumentList ?: "")
        val documentFlowConfig = KycDocumentFlowConfig(
            supportedCountries = supportedCountries,
            includeNfcFlow = false
        )
        val tinFlowConfig = KycTinFlowConfig(KycTinFlowConfig.TaxationCountry.ITA)
        val kycConfig = KycConfig(
            flows = setOf(
                KycFlow.Location,
                KycFlow.Document(documentFlowConfig),
                KycFlow.Tin(tinFlowConfig),
                KycFlow.Selfie
            )
        )
        viewModelScope.launch {
            val kycInfo = withContext(dispatchers.IO) { kycInfoDataSource.finalizeAndGetKycInfo() }
            viewState.update {
                copy(orcaState = OrcaState.StartOrca(kycConfig, kycInfo))
            }
        }
    }

    private fun processOrcaResult(kycInfo: KycInfo) {
        viewModelScope.launch {
            withContext(dispatchers.IO) {
                kycInfoDataSource.overrideKycInfo(kycInfo)
            }
            viewState.update {
                copy(orcaState = OrcaState.Done)
            }
        }
    }
}

sealed class OrcaAction {
    data class OrcaCompleted(val kycInfo: KycInfo): OrcaAction()
}

data class OrcaViewState(
    val orcaState: OrcaState
)

sealed class OrcaState {
    // Not determined yet for no action
    object Unknown: OrcaState()
    // Present Orca Flow
    data class StartOrca(val kycConfig: KycConfig, val kycInfo: KycInfo): OrcaState()
    // Doc scan is done, go to KYC upload (After Orca)
    object Done: OrcaState()
}
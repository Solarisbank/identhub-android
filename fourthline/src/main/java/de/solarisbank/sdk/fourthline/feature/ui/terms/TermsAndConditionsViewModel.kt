package de.solarisbank.sdk.fourthline.feature.ui.terms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.solarisbank.identhub.session.module.config.FourthlineIdentificationConfig

class TermsAndConditionsViewModel(
    fourthlineIdentificationConfig: FourthlineIdentificationConfig
): ViewModel() {
    private val viewState = MutableLiveData<TermsAndConditionsState>()

    fun state(): LiveData<TermsAndConditionsState> = viewState

    init {
        viewState.value = TermsAndConditionsState(
            shouldShowNamirialTerms = fourthlineIdentificationConfig.shouldShowNamirialTerms
        )
    }

}

data class TermsAndConditionsState(
    val shouldShowNamirialTerms: Boolean
)
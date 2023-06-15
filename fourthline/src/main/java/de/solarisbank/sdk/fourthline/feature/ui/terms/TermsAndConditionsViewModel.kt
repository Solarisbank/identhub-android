package de.solarisbank.sdk.fourthline.feature.ui.terms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.solarisbank.sdk.data.utils.IdenthubDispatchers
import de.solarisbank.sdk.data.utils.update
import de.solarisbank.sdk.domain.model.ResultState
import de.solarisbank.sdk.fourthline.data.FourthlineStorage
import de.solarisbank.sdk.fourthline.data.dto.TermsAndConditions
import de.solarisbank.sdk.fourthline.data.terms.TermsAndConditionsUseCase
import de.solarisbank.sdk.logger.IdLogger
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TermsAndConditionsViewModel(
    private val fourthlineStorage: FourthlineStorage,
    private val termsAndConditionsUseCase: TermsAndConditionsUseCase,
    private val dispatchers: IdenthubDispatchers
): ViewModel() {
    private val viewState = MutableLiveData<TermsAndConditionsState>()

    fun state(): LiveData<TermsAndConditionsState> = viewState

    init {
        viewState.value = TermsAndConditionsState(
            namirialTerms = fourthlineStorage.namirialTerms,
            acceptState = ResultState.Unknown(),
        )
    }

    fun onAction(action: TermsAndConditionsAction) {
        when (action) {
            is TermsAndConditionsAction.NextTapped -> {
                acceptTermsAndContinue()
            }
        }
    }

    private fun acceptTermsAndContinue() {
        viewModelScope.launch {
            try {
                viewState.update { copy(acceptState = ResultState.Loading()) }
                withContext(dispatchers.IO) {
                    fourthlineStorage.namirialTerms?.documentId?.let {
                        termsAndConditionsUseCase.acceptNamirialTerms(it)
                    }
                }
                viewState.update { copy(acceptState = ResultState.Success(Unit)) }
            } catch (throwable: Throwable) {
                IdLogger.error("Error accepting terms", throwable)
                viewState.update { copy(acceptState = ResultState.Failure(throwable)) }
            }
        }
    }

}

sealed class TermsAndConditionsAction {
    object NextTapped: TermsAndConditionsAction()
}

data class TermsAndConditionsState(
    val namirialTerms: TermsAndConditions?,
    val acceptState: ResultState<Unit>
)
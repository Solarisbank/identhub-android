package de.solarisbank.sdk.domain.model

sealed class StateUiModel<out T> {
    open val data: T? = null
    open val nextStep: String? = null

    data class Success<out T>(override val data: T? = null, override val nextStep: String? = null) : StateUiModel<T>()
    data class Loading<out T>(override val data: T? = null, override val nextStep: String? = null) : StateUiModel<T>()
    data class Error<out T>(override val data: T? = null, override val nextStep: String? = null) : StateUiModel<T>()

}
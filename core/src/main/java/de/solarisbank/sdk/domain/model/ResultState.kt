package de.solarisbank.sdk.domain.model

/**
 * A class to model the state of a result. Most of the results we usually model in our state
 * objects, have pretty standards states. This class encapsulates those as follows:
 * [Unknown] When the state is unknown. Usually used when we haven't set up the resource or the
 * request to fetch/update it has not yet been initiated.
 * [Loading] When the resource is in loading state. For example when the request has been sent and
 * we're awaiting the result.
 * [Success] When the resource fetching has been successful and the result can be accessed from
 * [Success.result]
 * [Failure] If there was an error fetching/updating the resource. In case of there was an
 * exception [Failure.throwable] should be populated.
 */
sealed class ResultState<out T> {
    class Unknown<out T>: ResultState<T>()
    class Loading<out T>: ResultState<T>()
    data class Success<out T>(val result: T): ResultState<T>()
    data class Failure<out T>(val throwable: Throwable? = null): ResultState<T>()
}

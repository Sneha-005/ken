package com.devrachit.ken.utility.NetworkUtility

/**
 * A sealed class that represents the state of a resource operation.
 * Fixed version with proper variance and data class implementations.
 */
sealed class Resource<out T>(val data: T? = null, val message: String? = null) {

    /**
     * Represents a successful state with data
     */
    data class Success<out T>(private val _data: T) : Resource<T>(_data) {
        // Override data to ensure non-null access for Success
        val successData: T get() = _data
    }

    /**
     * Represents an error state
     */
    data class Error<out T>(
        private val _message: String,
        private val _data: T? = null
    ) : Resource<T>(_data, _message) {
        // Override message to ensure non-null access for Error
        val errorMessage: String get() = _message
    }

    /**
     * Represents a loading state
     */
    data class Loading<out T>(private val _data: T? = null) : Resource<T>(_data)
}

/**
 * Extension functions for easier Resource handling
 */

/**
 * Returns true if the resource is in Success state
 */
fun <T> Resource<T>.isSuccess(): Boolean = this is Resource.Success

/**
 * Returns true if the resource is in Error state
 */
fun <T> Resource<T>.isError(): Boolean = this is Resource.Error

/**
 * Returns true if the resource is in Loading state
 */
fun <T> Resource<T>.isLoading(): Boolean = this is Resource.Loading

/**
 * Safely gets data from Success state, null otherwise
 */
fun <T> Resource<T>.getSuccessData(): T? = when (this) {
    is Resource.Success -> successData
    else -> null
}

/**
 * Safely gets error message from Error state, null otherwise
 */
fun <T> Resource<T>.getErrorMessage(): String? = when (this) {
    is Resource.Error -> errorMessage
    else -> null
}

/**
 * Executes the given block if the resource is in Success state
 */
inline fun <T> Resource<T>.onSuccess(crossinline action: (T) -> Unit): Resource<T> {
    if (this is Resource.Success) {
        action(successData)
    }
    return this
}

/**
 * Executes the given block if the resource is in Error state
 */
inline fun <T> Resource<T>.onError(crossinline action: (String) -> Unit): Resource<T> {
    if (this is Resource.Error) {
        action(errorMessage)
    }
    return this
}

/**
 * Executes the given block if the resource is in Loading state
 */
inline fun <T> Resource<T>.onLoading(crossinline action: () -> Unit): Resource<T> {
    if (this is Resource.Loading) {
        action()
    }
    return this
}

/**
 * Maps the data of a successful resource to another type
 */
inline fun <T, R> Resource<T>.map(crossinline transform: (T) -> R): Resource<R> = when (this) {
    is Resource.Success -> Resource.Success(transform(successData))
    is Resource.Error -> Resource.Error(errorMessage, data?.let(transform))
    is Resource.Loading -> Resource.Loading(data?.let(transform))
}
package com.aiuniverse.app.common

/**
 * Universal sealed class to encapsulate state and data transfer across Clean Architecture boundaries.
 */
sealed class Resource<out T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(val isLoading: Boolean = true, data: T? = null) : Resource<T>(data)
}

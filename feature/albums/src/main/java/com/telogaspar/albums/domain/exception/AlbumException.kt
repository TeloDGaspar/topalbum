package com.telogaspar.albums.domain.exception

sealed class AlbumException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NetworkException(cause: Throwable) :
        AlbumException("Failed to fetch Albums from network", cause)
    class EmptyResultException : AlbumException("No Albums returned from the server")
    class NotFoundException(id: String) : AlbumException("Breed $id not found")
    class ApiException(
        val code: Int,
        cause: Throwable,
    ) : AlbumException(
        message = "API error: $code",
        cause = cause,
    )

    class UnknownException(
        cause: Throwable,
    ) : AlbumException(
        message = "Unexpected error",
        cause = cause,
    )
}
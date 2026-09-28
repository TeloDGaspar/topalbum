package com.telogaspar.albums.domain.exception

sealed class AlbumException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NotFoundException(id: String) : AlbumException("Album $id not found")
    class ApiException(
        val code: Int,
        cause: Throwable,
    ) : AlbumException(
        message = "API error: $code",
        cause = cause,
    )

    class NetworkException(
        cause: Throwable,
    ) : AlbumException(
        message = "Network error",
        cause = cause,
    )

    class UnknownException(
        cause: Throwable,
    ) : AlbumException(
        message = "Unexpected error",
        cause = cause,
    )
}
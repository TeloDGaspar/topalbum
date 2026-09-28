package com.telogaspar.albums.presentation

import com.telogaspar.albums.domain.exception.AlbumException

enum class ErrorType {
    Network,
    Server,
    NotFound,
    Unknown,
}

internal fun ErrorType.toMessage(): String =
    when (this) {
        ErrorType.Network -> "Check your internet connection and try again."
        ErrorType.Server -> "The service is temporarily unavailable."
        ErrorType.NotFound -> "This album is no longer in the Top 100."
        ErrorType.Unknown -> "Something went wrong. Please try again."
    }

internal fun Exception.toErrorType(): ErrorType =
    when (this) {
        is AlbumException.NetworkException -> ErrorType.Network
        is AlbumException.ApiException -> ErrorType.Server
        is AlbumException.NotFoundException -> ErrorType.NotFound
        else -> ErrorType.Unknown
    }
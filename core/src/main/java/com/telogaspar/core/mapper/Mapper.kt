package com.telogaspar.core.mapper

interface Mapper<S, T> {
    fun map(source: S): T
}
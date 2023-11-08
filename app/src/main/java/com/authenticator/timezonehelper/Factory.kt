package com.authenticator.timezonehelper

interface Factory<T> {
    fun create(): T
}
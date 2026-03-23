package com.example.wavecast.core.network.utils

import java.security.MessageDigest

fun createSHA1(clearString: String): String? {
    return try {
        val messageDigest = MessageDigest.getInstance("SHA-1")
        messageDigest.update(clearString.toByteArray(charset("UTF-8")))
        byteArrayToString(messageDigest.digest())
    } catch (ignored: Exception) {
        ignored.printStackTrace()
        null
    }
}
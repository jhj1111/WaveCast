package com.example.wavecast.core.network.utils

import java.util.Locale

fun byteArrayToString(bytes: ByteArray): String {
    val buffer = StringBuilder()
    for (b in bytes) buffer.append(String.format(Locale.getDefault(), "%02x", b))
    return buffer.toString()
}
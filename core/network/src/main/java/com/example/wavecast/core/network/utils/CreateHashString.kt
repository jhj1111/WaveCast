package com.example.wavecast.core.network.utils

fun createHashString(apiKey: String, apiSecret: String, apiHeaderTime: String): String? {
    // Hash them to get the Authorization token
    val data4Hash = apiKey + apiSecret + apiHeaderTime
    val hashString: String? = createSHA1(data4Hash)

//    println("hashString=$hashString")
    return hashString
}
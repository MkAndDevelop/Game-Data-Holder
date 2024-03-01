package com.sinyee.babybus.simpleurlbuilderfinalmoderation.utils

import kotlin.random.Random

internal object DomenHolder {
    fun getRandomDome(): String {
        val index = Random.nextInt(Domens.values().size)
        return Domens.values()[index].domen
    }
}

internal enum class Domens(val domen: String) {
    DOMEN1("aHR0cHM6Ly9rZWxpcGFyZWwuY29tPw==".decrypt()),
    DOMEN2("aHR0cHM6Ly9ld2xlcnRhZXIuY29tPw==".decrypt()),
    DOMEN3("aHR0cHM6Ly9sZXBvd2VyYmEuY29tPw==".decrypt()),
    DOMEN4("aHR0cHM6Ly9rYXphbWlsYW4uY29tPw==".decrypt()),
    DOMEN5("aHR0cHM6Ly9sZWtpcGRhZ28uY29tPw==".decrypt()),
    DOMEN6("aHR0cHM6Ly94ZWxvcGlybmVyLmNvbT8=".decrypt()),
    DOMEN7("aHR0cHM6Ly9yZW1hbGlvbmVyLmNvbT8=".decrypt()),
    DOMEN8("aHR0cHM6Ly9mZWxhZGlhcm5vLmNvbT8=".decrypt())
}
package pmm.ignacio.theanimedatabase

import org.apache.commons.lang3.RandomStringUtils

object PkceGenerator {
    private const val CODE_VERIFIER_STRING =
        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

    fun generateVerifier(length: Int): String {
        return RandomStringUtils.random(length, CODE_VERIFIER_STRING)
    }
}
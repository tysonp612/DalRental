package models

import utilities.auth.PasswordEncryptEngine
import kotlin.random.Random

/**
 * Stores the users credentials to that would later be passed into a database
 * @param email Users email to be stored in the database
 * @param hash HashedPassword that would be stored
 * @param encryptionEngine The engine that was used for the encryption process
 */
data class UserCredentials<T : PasswordEncryptEngine>(
    var userName: String,
    var email: String,
    var encryptionEngine: Class<T>
) {

    internal var hash: String = ""

    fun setPassword(password: String) {
        encryptionEngine
            .getDeclaredConstructor()
            .newInstance()
            .encrypt(password,this)
    }

    /*
     Creates a salt string of length 6 that's appended to the beginning
     of a hash for a stronger security system
     */
    internal val salt: String = StringBuilder().apply {
        Array(6) { Random.nextInt(33, 127).toChar() }
            .forEach { generatedChar -> this.append(generatedChar) }
    }.toString()

    /**
     * Returns true if for this user the given password matches
     */
    fun validate(givenPassword: String): Boolean {
        return encryptionEngine
            .getDeclaredConstructor()
            .newInstance()
            .match(
                password = givenPassword,
                this
            )
    }

    override fun toString(): String {
        return """
        {
            "userName": "$userName",
            "email": "$email",
            "password_hash": "$hash",
            "salt": $salt,
            "encryption_engine": "$encryptionEngine"
        },
        """.trimIndent()
    }
}

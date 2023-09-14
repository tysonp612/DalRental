package models

import utilities.auth.EncryptEngine
import kotlin.random.Random

/**
 * Stores the users credentials to that would later be passed into a database
 * @param email Users email to be stored in the database
 * @param hash HashedPassword that would be stored
 * @param engine The engine that was used for the encryption process
 */
data class UserCredentials<T : EncryptEngine>(
    var email: String,
    private var hash: String,
    private var engine: Class<T>
) {
    /*
     Creates a salt string of length 6 that's appended to the beginning
     of a hash for a stronger security system
     */
    private val salt: String = StringBuilder().apply {
        Array(6) { Random.nextInt(33, 127).toChar() }
            .forEach { generatedChar -> this.append(generatedChar) }
    }.toString()

    /**
     * Returns true if for this user the given password matches
     */
    fun validate(givenPassword: String): Boolean {
        return engine
            .getDeclaredConstructor()
            .newInstance()
            .match(
                password = givenPassword,
                hash = hash,
                salt = salt
            )
    }

    override fun toString(): String {
        return "UserCredentials(email=$email, hash=$hash, salt=$salt, engine=$engine)"
    }
}

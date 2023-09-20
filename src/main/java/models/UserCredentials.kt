package models

import utilities.auth.PasswordEncryptEngine
import kotlin.random.Random

/**
 * The [UserCredentials] class represents user credentials that can be stored in a database.
 * It includes information such as the user's email, hashed password, encryption engine used, and more.
 *
 * @param userName Username that would be used in the database
 * @param email Users email to be stored in the database
 * @param encryptionEngine The engine that was used for the encryption process
 */
data class UserCredentials<T : PasswordEncryptEngine>(
    var userName: String,
    var email: String,
    var encryptionEngine: Class<T>
) {

    /**
     * The hashed password that is stored in the user's credentials.
     */
    internal var hash: String = ""

    /**
     * Creates a new instance of UserCredentials with default values used when matching
     * a given password to a sored password
     *
     * @param encryptionEngine The class of the encryption engine to be used.
     */
    private constructor(
        encryptionEngine: Class<T>,
    ) : this("Match", "email", encryptionEngine)


    /**
     * Stores the users password as hash using the specified encryption engine.
     *
     * @param password The plaintext password to be encrypted and stored.
     */
    fun setPassword(password: String) {
        // Create a random salt when ever setting a new password to prevent password tracebacks
        salt = createSalt(saltSize)
        /*
         * Dynamically instantiates an encryption engine, encrypts a plaintext password,
         * and stores the result in the user's credentials.
         *
         * This code snippet is responsible for performing the following steps:
         * 1. Instantiates a new encryption engine object dynamically,
         * based on the class specified by the 'encryptionEngine' property.
         * 2. Invoke the 'encrypt' method of the encryption engine to securely encrypt the provided plaintext password.
         * 3. Stores the resulting encrypted password in the 'UserCredentials' object.
         *
         * This approach allows for flexibility in choosing different encryption engines at runtime, making it possible
         * to adapt to various encryption methods.
         */
        encryptionEngine
            .getDeclaredConstructor()
            .newInstance()
            .encrypt(password, this)
    }


    /**
     * Size of the salt {random letters} you want to generate
     */
    private val saltSize: Int = 6

    /**
     * Random characters used increase password uncertainty
     */
    internal var salt: String = ""

    /**
     * Creates a string of random characters used for password security
     *
     * @param saltSize Length of the string generated
     */
    private fun createSalt(saltSize: Int): String {
        return StringBuilder().apply {
            Array(saltSize) { Random.nextInt(33, 127).toChar() }
                .forEach { generatedChar -> this.append(generatedChar) }
        }.toString()
    }

    /**
     * Returns true if for this user the given password matches the stored password
     *
     * @param givenPassword The password you want to check against
     */
    fun validate(givenPassword: String): Boolean {
        // Used for just testing the given password
        val demoCredentials = UserCredentials(this.encryptionEngine)

        try {
            // Get the class object
            val userCredentialsClass = demoCredentials.javaClass

            // Get the field by name
            val saltField = userCredentialsClass.getDeclaredField("salt")

            // Make the field accessible (since it's private by default)
            saltField.isAccessible = true

            // Set the salt field to the desired value
            saltField.set(demoCredentials, salt)

        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

        /*
         * Dynamically instantiates an encryption engine, encrypts a plaintext password,
         * and stores the result in the user's credentials.
         *
         * This code snippet is responsible for performing the following steps:
         * 1. Instantiates a new encryption engine object dynamically,
         * based on the class specified by the 'encryptionEngine' property.
         * 2. Invoke the 'encrypt' method of the encryption engine to securely encrypt the provided plaintext password.
         * 3. Stores the resulting encrypted password in the 'UserCredentials' object.
         *
         * This approach allows for flexibility in choosing different encryption engines at runtime, making it possible
         * to adapt to various encryption methods.
         */
        encryptionEngine
            .getDeclaredConstructor()
            .newInstance()
            .encrypt(
                password = givenPassword,
                demoCredentials,
            )

        return this.hash == demoCredentials.hash
    }

    /**
     * {
     *
     *      "UserName": "$userName",
     *
     *      "Email": "$email",
     *
     *      "Password_hash": "$hash",
     *
     *      "Salt": $salt,
     *
     *      "Encryption_engine": "$encryptionEngine"
     *
     *  },
     */
    override fun toString(): String {
        return """
        {
            "UserName": "$userName",
            "Email": "$email",
            "Password_hash": "$hash",
            "Salt": $salt,
            "Encryption_engine": "$encryptionEngine"
        },
        """.trimIndent()
    }
}

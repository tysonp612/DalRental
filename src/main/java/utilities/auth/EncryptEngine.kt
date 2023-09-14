package utilities.auth

import models.UserCredentials
import utilities.CustomFileOperations
import java.io.File
import java.io.IOException

interface EncryptEngine {

    // User credentials that would be used to store the
    val userCredentials: UserCredentials<EncryptEngine>

    // Gives the engine access to easy use common file operations
    val customFileOperations: CustomFileOperations
        get() = CustomFileOperations("main\\java\\utilities\\Auth\\FileAuth", true)

    // gives the file access to the file used for storing the data
    val file: File
        get() = customFileOperations.getFile() ?: run {
            if (!customFileOperations.createFile()) throw IOException("Fatal error when creating the password file")
            customFileOperations.getFile()!!
        }

    /**
     *  Encrypts the text into a hash
     *  @param text Text you want to encrypt
     *  @param key Key you need to encrypt the text
     *  @return Returns true if it successful and false
     *  if an error occurred
     */
    fun encrypt(text: String, key: Int = -1): UserCredentials<EncryptEngine>

    /**
     * Checks if a password matches with the actual passwords
     * @param password The password you want to validate
     * @param hash The saved password you want to compare to
     * @param salt The salt value used for the hash
     * @return Returns true if its matches and false if not
     */
    fun match(password: String, hash: String, salt: String): Boolean
}
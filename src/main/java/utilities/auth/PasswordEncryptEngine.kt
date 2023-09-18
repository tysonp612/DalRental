package utilities.auth

import models.UserCredentials
import utilities.CustomFileOperations
import java.io.File
import java.io.IOException

interface PasswordEncryptEngine {


    // Gives the engine access to easy use common file operations
    private val fileOperations: CustomFileOperations
        get() = CustomFileOperations("main\\java\\utilities\\Auth\\FileAuth", true)

    // gives the file access to the file used for storing the data
    val passwordFile: File
        get() = fileOperations.getFile() ?: run {
            if (!fileOperations.createFile()) throw IOException("Fatal error when creating the password file")
            fileOperations.getFile()!!
        }

    /**
     *  Encrypts the text into a hash
     *  @param password Text you want to encrypt
     *  @param userCredentials The userCredentials you want to match it too
     *  @return Returns true if it successful and false
     *  if an error occurred
     */
    fun <T : PasswordEncryptEngine> encrypt(
        password: String,
        userCredentials: UserCredentials<T>
    )

    /**
     * Checks if a password matches with the actual passwords
     * @param password The password you want to validate
     * @param userCredentials The userCredentials you want to match it too
     * @return Returns true if its matches and false if not
     */
    fun <T : PasswordEncryptEngine> match(
        password: String,
        userCredentials: UserCredentials<T>
    ): Boolean
}
package utilities.auth

import models.UserCredentials
import utilities.CustomFileOperations
import java.io.File
import java.io.IOException

/**
 * The [PasswordEncryptEngine] interface defines the contract for encrypting passwords
 * and storing them in UserCredentials objects.
 * Implementing classes must provide implementations for these methods.
 */
interface PasswordEncryptEngine {


    /**
     * Gives the engine access to common file operations.
     *
     * This property provides access to an instance of [CustomFileOperations] that is
     * initialized with a specific file path for storing password data.
     *
     * @return An instance of [CustomFileOperations] for file operations.
     */
    private val fileOperations: CustomFileOperations
        get() = CustomFileOperations("main\\java\\utilities\\Auth\\FileAuth", true)

    /**
     * Gives access to the file used for storing password data.
     *
     * This property retrieves the password file using the [CustomFileOperations] instance.
     * If the file does not exist, it attempts to create it.
     *
     * @return The [File] object representing the password storage file.
     * @throws IOException if there is a fatal error when creating the password file.
     */
    val passwordFile: File
        get() = fileOperations.getFile() ?: run {
            if (!fileOperations.createFile()) throw IOException("Fatal error when creating the password file")
            fileOperations.getFile()!!
        }

    /**
     * Encrypts the password and writes it to the [UserCredentials] passed in
     *  @param password Text you want to encrypt
     *  @param userCredentials The userCredentials you want to Store the hash into
     */
    fun <T : PasswordEncryptEngine> encrypt(
        password: String,
        userCredentials: UserCredentials<T>
    )
}
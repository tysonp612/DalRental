package utilities.auth

import models.UserCredentials


class KeyedCryptographer(
    override val userCredentials: UserCredentials<EncryptEngine>
) : EncryptEngine {
    /**
     *  Encrypts the text into a hash
     *  @param text Text you want to encrypt
     *  @param key Key you need to encrypt the text
     *  @return Returns true if it successful and false
     *  if an error occurred
     */
    override fun encrypt(text: String, key: Int): UserCredentials<EncryptEngine> {
        TODO("Not yet implemented")
    }

    /**
     * Checks if a password matches with the actual passwords
     * @param password The password you want to validate
     * @param hash The saved password you want to compare to
     * @param salt The salt value used for the hash
     * @return Returns true if its matches and false if not
     */
    override fun match(password: String, hash: String, salt: String): Boolean {
        TODO("Not yet implemented")
    }

}
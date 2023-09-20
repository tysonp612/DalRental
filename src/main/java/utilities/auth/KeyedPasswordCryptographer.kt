package utilities.auth

import models.UserCredentials
import kotlin.math.abs

/**
 * The [KeyedPasswordCryptographer] class provides methods for encrypting passwords
 * and storing them in [UserCredentials] objects.
 * It uses a multistep encryption process involving unique keys and Qwerty keyboard-based transformations.
 */
class KeyedPasswordCryptographer : PasswordEncryptEngine {

    /**
     * Encrypts the password and writes it to the [UserCredentials] passed in
     *
     * Steps Used For Encryption
     *
     *  . Create a unique 3-digit number
     *
     *  . Run the first step of encryption on the password
     *
     *  . Convert the result of the first step into a 256-bit long
     *    string and then convert it into a hex format
     *
     *  . Run the hash with the last three words of the salt appended to
     *    the beginning of hex, into the last encryption algorithm
     *  @param password Text you want to encrypt
     *  @param userCredentials The userCredentials you want to Store the hash into
     *  @return Returns true if it successful and false
     *  if an error occurred
     */
    override fun <T : PasswordEncryptEngine> encrypt(
        password: String, userCredentials: UserCredentials<T>
    ) {

        /*
            Generates a key, the height of a keyboard used for swapping the passwords

            qwertyKey = abs (sum(charInPassword | indexOfChar) << abs(
                sum(charInPassword (if even index then do a bit wise or else bit wise and) indexOfChar))
            ) % 9 repeated keyboardLen times

            if number is key is less than the keyboard len then pad Zeros to the start till the len matches
         */
        val keyLen = keyBoard.size
        val qwertyKey: String = run {
            var index = 0
            abs(password.sumOf { it.code or ++index } shl abs(
                run {
                    var sum = 0
                    for (idx in password.indices) {
                        if (idx % 2 == 0) sum += password[idx].code or idx
                        else password[idx].code and idx
                    }
                    sum
                }
            ) % "9".repeat(keyLen).toInt()).let { key ->
                // take the last n words
                "${"0".repeat(keyLen)}$key".takeLast(keyLen)
            }
        }

        val saltedPassword: StringBuilder = StringBuilder(password + userCredentials.salt)
        qwertyEncryption(saltedPassword, qwertyKey)

        val bitLen = 256
        // Create a binary representation for each character of the previous encryption with each char 'bitLen' long
        val binaryStrings = StringBuilder().apply {
            // Append the salt as binary
            for (char in userCredentials.salt) append(Integer.toBinaryString(bitLen or char.code))

            // Append the previous encoding
            for (char in saltedPassword) append(Integer.toBinaryString(bitLen or char.code))
        }

        // The key is the last char value in int format from the encrypted saltedPassword
        val splitKey: Int = saltedPassword[saltedPassword.lastIndex].code
        alternatingSplitEncryption(binaryStrings, splitKey)

        val hexFormat = toHexFormat(binaryStrings)

        // println("HexFormat: $hexFormat\n${"-".repeat(10)}")

        userCredentials.hash = hexFormat
    }


    /**
     * Converts the binary strings into hex format
     * @param binaryStrings The binary string to convert
     * @return The hex format as a string
     */
    private fun toHexFormat(binaryStrings: StringBuilder): String {
        /*
            E.g.: Array that's returned after splitting can be -> [, 1,0,1,1, ] so I remove the first and last space
            using .drop() and .dropLast() respectively
        */
        return binaryStrings
            .split(Regex(""))
            .drop(1)
            .dropLast(1)
            .reversed()
            .asSequence()
            .chunked(4)
            .map(::binaryToHex)
            .joinToString(separator = "")
            .reversed()
    }

    /**
     * Converts a binary list *{THAT'S BACKWARDS} to string
     * @param binaryList Reversed binary string to convert
     * @return Hex in capital letter format
     */
    private fun binaryToHex(binaryList: List<String>): String {
        var binaryToDecimal = 0
        for (idx in binaryList.indices) {
            if (binaryList[idx] == "1")
                binaryToDecimal += if (idx == 0) 1 else 2 shl (idx - 1)
        }
        val hex = Integer.toHexString(binaryToDecimal)
        return if (hex[0].isLetter()) (hex[0].code - 32).toChar().toString() else hex
    }

    /**
     * When given a binaryString B and a key K concatenates all the odd-indexed characters of B,
     * with all the even-indexed characters of B, this process should be repeated K times
     * @param binaryString The bits to be swapped
     * @param splitKey The number of times to be swapped
     */
    private fun alternatingSplitEncryption(binaryString: StringBuilder, splitKey: Int) {
        // Keep a copy of the original list as we would use this as we rebuild the swapped bits
        var finalResult = StringBuilder(binaryString)
        var swapList = StringBuilder(binaryString)
        for (idx in 0..<splitKey) {
            // Set the startPos to the middle of the list to append the even indexes
            var startPos = finalResult.length / 2
            for (even in finalResult.indices step 2)
                swapList[startPos++] = finalResult[even]

            // Set the startPos to the beginning of the list to append the odd indexes
            startPos = 0
            for (odd in 1..<finalResult.length step 2)
                swapList[startPos++] = finalResult[odd]

            // Swap the two lists to redo the swap for the next iteration
            finalResult = swapList.apply { swapList = finalResult }
        }
        // Writes the result to be the new binary string
        for (idx in binaryString.indices) {
            binaryString[idx] = finalResult[idx]
        }
    }

    /**
     * Keyboard used for the password key swap
     */
    private val keyBoard: Array<Array<Char>> = arrayOf(
        arrayOf('!', '@', '#', '$', '%', '^', '&', '*', '(', ')'),
        arrayOf('1', '2', '3', '4', '5', '6', '7', '8', '9', '0'),
        arrayOf('q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p'),
        arrayOf('a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', ':'),
        arrayOf('z', 'x', 'c', 'v', 'b', 'n', 'm', ',', '.', '/')
    )

    /**
     * This is a variant of qwerty encryption found in codewars.
     * It's done by performing a swap on the row the particular key is found on
     * @see <a href="https://www.codewars.com/kata/57f14afa5f2f226d7d0000f4/java">Simple Encryption #4 - Qwerty</a>
     */
    private fun qwertyEncryption(
        passwordSalted: StringBuilder,
        key: String
    ) {
        for (idx in passwordSalted.indices) {
            val currChar = passwordSalted[idx]
            for (row in keyBoard.indices) {
                for (col in keyBoard[row].indices) {
                    if (keyBoard[row][col] == currChar) {
                        // Find the offset wrap on that row to get the new key
                        val offset = (row + key[row].toString().toInt()) % keyBoard[row].size
                        passwordSalted[idx] = keyBoard[row][offset]
                    }
                }
            }
        }
    }

}


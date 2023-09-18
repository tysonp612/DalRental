package utilities.auth

import models.UserCredentials
import kotlin.math.abs

class KeyedPasswordCryptographer : PasswordEncryptEngine {


    /**
     * Encrypts the data and writes the hashed password to the [UserCredentials] passed in
     *
     * Steps Used For Encryption
     *
     *  . Create a unique 3-digit number
     *
     *  . Run the first step of encryption on the password
     *
     *  . Convert the result of the first step into 256 bit long
     *    string and then convert it into a hex format
     *
     *  . Run the hash with the last 3 words of the salt appended to
     *    the beginning of hex, into the last encryption algorithm
     *  @param password Text you want to encrypt
     *  @param userCredentials The userCredentials you want to match it too
     *  @return Returns true if it successful and false
     *  if an error occurred
     */
    override fun <T : PasswordEncryptEngine> encrypt(
        password: String, userCredentials: UserCredentials<T>
    ) {
        // Max len - salt used to make a 256bit number minus
        if (password.length > 28 - userCredentials.salt.length) {
            throw IllegalArgumentException(
                "Password $password with length ${password.length} cannot be of more than length 28"
            )
        }

        /*
             Sum up all char and do a bit wise left  shift while taking the absolute bit wise or between the
             integer value of the first and last char

            threeDigitKey = abs((allCharIntSum << abs(fistCharInt | lastCharInt)) mod 999)
         */
        val keyLen = keyBoard.size
        val qwertyKey: String =
            abs((password.sumOf { it.code } shl abs(password[0].code or password[password.lastIndex].code)) % 99999).let { key ->
                // take the last n words
                "${"0".repeat(keyLen)}$key".takeLast(keyLen)
            }

        println("${"-".repeat(10)}\nPassword: $password")
        println("Salt: ${userCredentials.salt}")
        val saltedPassword: StringBuilder = StringBuilder(password + userCredentials.salt.take(3))
        println("SaltedPassword: $saltedPassword")
        println("QwertyKey: $qwertyKey")
        qwertyEncryption(saltedPassword, qwertyKey)
        println("Qwerty: $saltedPassword")

        // Create a binary representation for each character of the previous encryption
        val binaryBits = StringBuilder().apply {
            // Append the salt as binary
            for (char in userCredentials.salt.takeLast(3)) append(Integer.toBinaryString(256 or char.code))

            // Append the previous encoding
            for (char in saltedPassword) append(Integer.toBinaryString(256 or char.code))

        }

        println("BinaryBits: $binaryBits")

        // The key is the fist char value in int format from the saltedPassword
        val splitKey: Int = saltedPassword[0].code
        alternatingSplitEncryption(binaryBits, splitKey)

        println("SplitKey: $splitKey")
        println("Alternating Splits: $binaryBits")

        val hexFormat = toHexFormat(binaryBits)

        println("HexFormat: $hexFormat\n${"-".repeat(10)}")

        userCredentials.hash = hexFormat
    }

    /**
     * Checks if a password matches with the actual passwords
     * @param password The password you want to validate
     * @param hash The saved password you want to compare to
     * @param salt The salt value used for the hash
     * @return Returns true if its matches and false if not
     */
    override fun <T : PasswordEncryptEngine> match(
        password: String, userCredentials: UserCredentials<T>
    ): Boolean {
        TODO("Not yet implemented")
    }

    private fun toHexFormat(binaryBits: StringBuilder): String {
        return binaryBits
            .split(Regex(""))
            /*
                Eg: Array returned -> [,1,2,3,4,] so I remove the first and last space
                using drop and dropLast
             */
            .drop(1)
            .dropLast(1)
            .reversed()
            .asSequence()
            .chunked(4)
            .map(::binaryToHex)
            .joinToString(separator = "")
            .reversed()
    }

    private fun binaryToHex(binary: List<String>): String {
        var binaryToDecimal = 0
        for (idx in binary.indices) {
            if (binary[idx] == "1")
                binaryToDecimal += if (idx == 0) 1 else 2 shl (idx - 1)
        }
        val hex = Integer.toHexString(binaryToDecimal)
        return if (hex[0].isLetter()) (hex[0].code - 32).toChar().toString() else hex
    }

    /**
     *
     * @param binaryBits The bits to be swapped
     * @param splitKey The amount of times to be swapped
     */
    private fun alternatingSplitEncryption(binaryBits: StringBuilder, splitKey: Int) {
        // Keep a copy of the original list as we would use this as we rebuild the swapped bits
        var finalResult = StringBuilder(binaryBits)
        var swapList = StringBuilder(binaryBits)
        for (idx in 0..<splitKey) {
            // Set the startPos to the middle of the list to append the even indexes
            var startPos = finalResult.length / 2
            for (even in finalResult.indices step 2)
                swapList[startPos++] = finalResult[even]

            // Set the startPos to the beginning of the list to append the odd indexes
            startPos = 0
            for (odd in 1..<finalResult.length step 2)
                swapList[startPos++] = finalResult[odd]

            finalResult = swapList.apply { swapList = finalResult }
        }
        for (idx in binaryBits.indices) {
            binaryBits[idx] = finalResult[idx]
        }
    }

    private val keyBoard: Array<Array<Char>> = arrayOf(
        arrayOf('1', '2', '3', '4', '5', '6', '7', '8', '9', '0'),
        arrayOf('q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p'),
        arrayOf('a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', ':'),
        arrayOf('z', 'x', 'c', 'v', 'b', 'n', 'm', ',', '.', '/')
    )

    private fun qwertyEncryption(
        passwordSalted: StringBuilder, key: String
    ) {
        for (idx in passwordSalted.indices) {
            val currChar = passwordSalted[idx]
            for (row in keyBoard.indices) {
                for (col in keyBoard[row].indices) {
                    if (keyBoard[row][col] == currChar) {
                        // Find the wrap on that row
                        val offset = (row + key[row].toString().toInt()) % keyBoard[row].size
                        passwordSalted[idx] = keyBoard[row][offset]
                    }
                }
            }
            passwordSalted[idx] = (passwordSalted[idx].code xor 32).toChar()
        }
    }

}
package utilities

import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.Path
import java.util.Scanner
import kotlin.io.path.*

class CustomFileOperations (
    private val fileName: String,
    shouldCreateFile: Boolean,
    private var filePath: String? = null,
    private var file:File? = null
) {

    /**
     * Sets up the file to read from what ever file Location
     * given or a default created in the FileWriters path folder
     */
    init {
        // Set up file path if nones provided
        filePath = filePath ?: "${getFilePathStr()}/$fileName"
        try {
            // Create the file
            filePath?.let { file = File(it) }

            if (shouldCreateFile) {
                createFile()
            }
            println(toString())
        }catch (e: IOException) {
            throw IOException("File path $filePath not found")
            e.printStackTrace()
        }
    }

    companion object {
        /**
         * Provides the string path till the /src path and
         * users should provide all path till destined location
         */
        fun getFilePathStr(): String {
            var currentDirectory:String = System.getProperty("user.dir")

            /*
                 Linux provides path till src while widows stops before the src path,
                 so we have to manually input the name.
                 Linux: User/../ProjectName/src
                 Windows: C:\..\ProjectName\
             */
            if (!currentDirectory.endsWith("src"))
                currentDirectory += "/src"

            return "$currentDirectory/"
        }

        /**
         * Provides the path object using the string gotten from [getFilePathStr]
         */
        fun getFilePath(): Path = Path(getFilePathStr())

        /**
         * Provides the file object using the string gotten from [getFilePathStr]
         */
        fun getFile(): File = File(getFilePathStr())

        /**
         * Returns a scanner object that reads input from the specified file
         */
        fun writeResultToFile(result: CharSequence, fileName: String = "Validator\\Input.txt") {
            val filewriter = CustomFileOperations(fileName,true)
            filewriter.writeToFile(result)
        }

        /**
         * Creates a scanner object that reads input from the file specified
         */
        fun fileToScanner(args: Array<String> = arrayOf("Validator\\Input.txt")): Scanner {
            return when {
                // If running with a location containing input
                (args.isNotEmpty()) -> {
                    val fileLocation: String = CustomFileOperations.getFilePathStr()
                    val file = File("$fileLocation${args[0].replace("\\s+","")}")
                    println(file.path)
                    file.createNewFile()
                    if (!file.exists())
                        throw FileNotFoundException("File name ${args[0]} not found in location $fileLocation")
                    Scanner(file)
                }
                // If file is running normally
                else -> Scanner(System.`in`)
            }
        }
    }

    fun getFile(): File? {
        return file
    }

    fun getFilePath(): Path? = filePath?.let { Path(it) }

    fun createDirectory(): Path = filePath?.let { Path(it).createDirectory() }!!

    fun createFile():Boolean {
        // Check if the file does exist and create one if it doesn't
        file?.let {
            if (!it.exists()) it.createNewFile()
            else{
                println("File: \"$fileName\" already exists")
                return false
            }
        }
        return true
    }
    fun writeToFile (content: CharSequence) { file?.printWriter()?.use { it.println(content) } }
    fun <T : Collection<T>> writeToFile (content: T, collectionFunction: (T) -> String = {
        collectionContents -> collectionContents.toString()
    }) {
        file?.printWriter()?.use {writer ->
            content.forEach {
                writer.println(collectionFunction(it))
            }
        }
    }

    fun <T> writeToFile (content: Array<Array<T>>, arrayRowFunction: (Array<T>) -> String = {
        innerArrayContents -> innerArrayContents.contentDeepToString()
    }) {
        file?.printWriter()?.use {writer ->
            content.forEach {
                writer.println(arrayRowFunction(it))
            }
        }
    }

    fun <T> writeToFile (content: Array<T>, arrayFunction: (T) -> String = {
        contents -> contents.toString()
    }) {
        file?.printWriter()?.use {writer ->
            content.forEach {
                writer.println(arrayFunction(it))
            }
        }
    }

    override fun toString(): String {
        return """
            Path name formed was: $filePath
            File path is: ${file?.path}
            File parent is: ${file?.parent}
            File absolutePath is: ${file?.absolutePath}
        """.trimIndent()
    }
}

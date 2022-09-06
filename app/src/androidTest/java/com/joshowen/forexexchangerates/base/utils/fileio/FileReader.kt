package com.joshowen.forexexchangerates.base.utils.fileio

import androidx.test.platform.app.InstrumentationRegistry
import java.io.IOException
import java.io.InputStreamReader

object FileReader {
    fun readStringFromFile(fileName: String): String {
        try {
            val inputStream = (InstrumentationRegistry.getInstrumentation().targetContext
                .applicationContext).assets.open(fileName)
            val builder = StringBuilder()
            val reader = InputStreamReader(inputStream, "UTF-8")
            reader.readLines().forEach {
                builder.append(it)
            }
            return builder.toString()
        } catch (e: IOException) {
            throw e
        }
    }
}


//object FileReader {
//    fun asset(context: Context, assetPath: String): String {
//        try {
//            val inputStream = context.assets.open("network_files/$assetPath")
//            return inputStreamToString(inputStream, "UTF-8")
//        } catch (e: IOException) {
//            throw RuntimeException(e)
//        }
//    }
//
//    private fun inputStreamToString(inputStream: InputStream, charsetName: String): String {
//        val builder = StringBuilder()
//        val reader = InputStreamReader(inputStream, charsetName)
//        reader.readLines().forEach {
//            builder.append(it)
//        }
//        return builder.toString()
//    }
//}
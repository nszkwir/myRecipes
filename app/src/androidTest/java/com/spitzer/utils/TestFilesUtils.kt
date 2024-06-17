package com.spitzer.utils

import android.content.Context
import java.io.BufferedReader

object TestFileUtils {
    fun readFile(context: Context, filename: String): String {
        return try {
            val bufferedReader = context.assets.open(filename).bufferedReader()
            bufferedReader.use(BufferedReader::readText)
        } catch (e: Exception) {
            ""
        }
    }
}

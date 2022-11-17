package com.qhy040404.libraryonetap.utils.extensions

import com.qhy040404.libraryonetap.constant.Constants

@Suppress("unused")
object StringExtension {
    /**
     * Check if a string is equals to ERROR message
     *
     * @return true if the data is OK to use. Not equals to ERROR
     */
    fun String.isValid(): Boolean {
        return this != Constants.GLOBAL_ERROR && this != Constants.NET_TIMEOUT && this != Constants.NET_ERROR && this != Constants.NET_DISCONNECTED
    }

    /**
     * Check if new value is same as previous one
     *
     * @param globalValue Current value
     * @param isCustomTheme If the setting is custom theme
     * @return true if they are same
     */
    fun String.isDuplicateGV(globalValue: String, isCustomTheme: Boolean = false): Boolean {
        val a = this == globalValue || globalValue == Constants.GLOBAL_ERROR && this == ""
        return if (isCustomTheme) {
            a && this != "random"
        } else {
            a
        }
    }

    /**
     * Replace all in a string
     *
     * @param oldVal value to be replaced
     * @param newVal value to replace
     * @param times times to replace. 0 to infinite
     * @return replaced string
     */
    fun String.replaceAll(oldVal: String, newVal: String, times: Int = 0): String {
        var temp = this
        if (times <= 0) {
            temp = temp.split(oldVal).joinToString(separator = newVal)
        } else {
            for (i in 0 until times) {
                temp = temp.replace(oldVal, newVal)
            }
        }
        return temp
    }
}

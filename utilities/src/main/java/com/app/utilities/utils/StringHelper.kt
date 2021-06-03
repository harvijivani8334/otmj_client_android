package com.app.utilities.utils

import android.content.Context

object StringHelper {
    /**
     * Check String is Empty OR Null
     */
    fun isEmpty(str: String?): Boolean {
        return str == null || str.trim { it <= ' ' }.length == 0
    }

    fun isNotEmpty(str: String?): Boolean {
        return !isEmpty(str)
    }

    /**
     * Convert any object to string
     */
    fun convertToString(value: Any?): String {
        return value?.toString() ?: ""
    }

    /**
     * @param inputString
     * @return $returnType$
     */
    fun firstAsUpperCase(inputString: String): String {
        return if (!isEmpty(inputString)) {
            inputString.substring(0, 1).toUpperCase() + inputString.substring(1, inputString.length)
        } else {
            inputString
        }
    }

    /**
     * @param inputString
     * @return $returnType$
     */
    fun firstAsLowerCase(inputString: String): String {
        return if (!isEmpty(inputString)) {
            inputString.substring(0, 1).toLowerCase() + inputString.substring(1, inputString.length)
        } else {
            inputString
        }
    }

    /**
     * @param value1
     * @param value2
     * @return $returnType$
     */
    fun equals(value1: String?, value2: String?): Boolean {
        return value1 != null && value2 != null && value1 == value2 || value1 == null && value2 == null
    }

    /**
     * @param value1
     * @param value2
     * @return $returnType$
     */
    fun equalsTrim(value1: String?, value2: String?): Boolean {
        return value1 != null && value2 != null && value1.trim { it <= ' ' } == value2.trim { it <= ' ' } || value1 == null && value2 == null
    }

    /**
     * @param value1
     * @param value2
     * @return $returnType$
     */
    fun equalsNoCase(value1: String?, value2: String?): Boolean {
        return value1 != null && value2 != null && value1.toLowerCase()
            .trim { it <= ' ' } == value2.toLowerCase()
            .trim { it <= ' ' } || value1 == null && value2 == null
    }

    /**
     * @param str
     * @return
     */
    fun length(str: String?): Int {
        return str?.length ?: 0
    }

    /**
     * @param str
     * @return
     */
    fun nullStrToEmpty(str: Any?): String {
        return if (str == null) "" else (if (str is String) str else str.toString())!!
    }

    /**
     * @param inString
     * @param oldPattern
     * @param newPattern
     * @return
     */
    fun replace(inString: String, oldPattern: String, newPattern: String?): String {
        if (!isEmpty(inString) || !isEmpty(oldPattern) || !isEmpty(newPattern)) {
            return inString
        }
        val sb = StringBuilder()
        var pos = 0 // our position in the old string
        var index = inString.indexOf(oldPattern)
        // the index of an occurrence we've found, or -1
        val patLen = oldPattern.length
        while (index >= 0) {
            sb.append(inString.substring(pos, index))
            sb.append(newPattern)
            pos = index + patLen
            index = inString.indexOf(oldPattern, pos)
        }
        sb.append(inString.substring(pos))
        // remember to append any characters to the right of a match
        return sb.toString()
    }

    /**
     * @param toSplit
     * @param delimiter
     * @return
     */
    fun split(toSplit: String, delimiter: String?): Array<String>? {
        return if (isEmpty(toSplit) || isEmpty(delimiter)) {
            null
        } else toSplit.split(delimiter!!).toTypedArray()
    }

    /**
     * @param value
     * @return
     */
    fun isAlpha(value: String): Boolean {
        if (isEmpty(value)) {
            return false
        }
        for (i in 0 until value.length) {
            val ch = value[i]
            if (!Character.isLetter(ch)) {
                return false
            }
        }
        return true
    }

    /**
     * @param val
     * @return
     */
    fun isAlphaNumeric(`val`: String): Boolean {
        if (isEmpty(`val`)) {
            return false
        }
        for (i in 0 until `val`.length) {
            val ch = `val`[i]
            if (!Character.isLetterOrDigit(ch)) {
                return false
            }
        }
        return true
    }

    /**
     * @param input
     * @return
     */
    fun containsOnlyDigits(input: String): Boolean {
        for (i in 0 until input.length) {
            if (!Character.isDigit(input[i])) {
                return false
            }
        }
        return true
    }

    /**
     * @param mContext
     * @param aString
     * @return
     */
    fun getStringResourceByName(mContext: Context, aString: String?): String {
        val packageName = mContext.packageName
        return mContext.getString(mContext.resources.getIdentifier(aString, "string", packageName))
    }

    fun removeZeroOnFirstLetter(input: String): String {
        return if (!isEmpty(input)) input.replaceFirst("^0*".toRegex(), "") else ""
    }
}
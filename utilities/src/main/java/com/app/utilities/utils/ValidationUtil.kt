package com.app.utilities.utils

import android.util.Patterns
import android.view.View
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*

object ValidationUtil {
    fun isEmptyEditText(s: String?): Boolean {
        return StringHelper.isEmpty(s)
    }

    fun isValidEmail(email: String?): Boolean {
        return if (StringHelper.isEmpty(email)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }

    fun isValidPassword(password: String): Boolean {
        if (StringHelper.isEmpty(password)) {
            return false
        }
        var isHaveDigit = false
        val chars = password.toCharArray()
        val sb = StringBuilder()
        for (c in chars) {
            if (Character.isDigit(c)) {
                sb.append(c)
                isHaveDigit = true
            }
        }
        return (password.length in 6..30
                && password != password.toLowerCase(Locale.getDefault())
                && password != password.toUpperCase(Locale.getDefault())
                && isHaveDigit)
    }

    fun isValidFullName(name: String): Boolean {
        return !StringHelper.isEmpty(name) && name.trim { it <= ' ' }.contains(" ")
    }

    fun isValidConfirmPassword(confirmPassword: String, password: String): Boolean {
        return confirmPassword == password
    }

    fun setErrorIntoEditText(view: View, message: String?) {
        if (view is EditText && !StringHelper.isEmpty(message)) {
            view.error = message
            view.requestFocus()
        }
    }

    fun setErrorIntoInputTextLayout(viewEditText: View, viewInputTextLout: View, message: String?) {
        if (!StringHelper.isEmpty(message)) {
            (viewInputTextLout as TextInputLayout).error = message
            viewEditText.requestFocus()
        }
    }

    fun isValidField(text: String, pattern: String): Boolean {
        return if (StringHelper.isEmpty(text)) {
            false
        } else {
            val regex = Regex(pattern)
            return regex.matches(input = text)
        }
    }

    fun checkMinTextValidation(text: String, textLength: Int): Boolean {
        return if (StringHelper.isEmpty(text)) {
            false
        } else text.trim { it <= ' ' }.length >= textLength
    }

    fun checkMaxTextValidation(text: String, textLength: Int): Boolean {
        return if (StringHelper.isEmpty(text)) {
            false
        } else text.trim { it <= ' ' }.length <= textLength
    }

    fun checkEnglishLanguagesValidation(text: String): Boolean {
        if (StringHelper.isEmpty(text)) {
            return false
        }
        val regex = Regex("[a-zA-Z0-9.? !\"#\$%&'()*+,-./:;<=>?@^_`~[|]]*")
        return regex.matches(input = text)
    }

    fun isValidPhoneNumberRange(text: String): Boolean {
        return text.length == 10
    }

    fun isPhoneNumberStartWithZero(text: String): Boolean {
        return text.startsWith("0")
    }
}

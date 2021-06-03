package com.app.utilities.callback

interface DialogButtonClickListener {
    fun onPositiveButtonClicked(dialogIdentifier: Int)
    fun onNegativeButtonClicked(dialogIdentifier: Int)
}
package com.app.utilities.callback

interface DialogRadioButtonItemSelectListener {
    fun onItemSelect(dialogIdentifier: Int, position: Int)

    fun onPositiveButtonClicked(dialogIdentifier: Int, position: Int)

    fun onNegativeButtonClicked(dialogIdentifier: Int, position: Int)
}
package com.app.utilities.callback

import android.widget.DatePicker

interface OnDateSetListener {
    fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int)
}
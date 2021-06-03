package com.app.utilities.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.app.utilities.callback.OnDateSetListener
import com.app.utilities.utils.Constant
import com.app.utilities.utils.DateHelper
import com.app.utilities.utils.StringHelper
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private var onDateSetCallback: OnDateSetListener? = null

    companion object {
        fun newInstance(
            minDate: Long,
            maxDate: Long,
            date: String?,
            dateFormat: String?,
            identifier: String?
        ): DatePickerFragment {
            val args = Bundle()
            args.putString(Constant.IntentKey.DATE, date)
            args.putLong(Constant.IntentKey.MIN_DATE, minDate)
            args.putLong(Constant.IntentKey.MAX_DATE, maxDate)
            args.putString(Constant.IntentKey.DATE_FORMAT, dateFormat)
            args.putString(Constant.IntentKey.DATE_PICKER_IDENTIFIER, identifier)
            val fragment = DatePickerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            try {
                onDateSetCallback = context as OnDateSetListener
            } catch (e: ClassCastException) {
                throw ClassCastException(
                    context.toString()
                            + " must implement MyInterface "
                )
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bundle = arguments
        if (bundle != null) {
            val date = bundle.getString(Constant.IntentKey.DATE, "")
            val dateFormat = bundle.getString(Constant.IntentKey.DATE_FORMAT, "")
            val minDate = bundle.getLong(Constant.IntentKey.MIN_DATE, 0)
            val maxDate = bundle.getLong(Constant.IntentKey.MAX_DATE, 0)
            val identifier = bundle.getString(Constant.IntentKey.DATE_PICKER_IDENTIFIER, "")
            val c = Calendar.getInstance(Locale.US)
            if (!StringHelper.isEmpty(date) && !StringHelper.isEmpty(dateFormat)) {
                try {
                    c.time = DateHelper.stringToDate(date, dateFormat)
                } catch (e: Exception) {
                    Log.e(
                        this@DatePickerFragment.javaClass.simpleName,
                        "error in onCreateDialog(): " + e.message
                    )
                }
            }
            val year = c[Calendar.YEAR]
            val month = c[Calendar.MONTH]
            val day = c[Calendar.DAY_OF_MONTH]
            val dialog = DatePickerDialog(activity!!, this, year, month, day)
            dialog.datePicker.tag = identifier
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dialog.datePicker.firstDayOfWeek = Calendar.MONDAY
            }

            // if (StringHelper.isEmpty(date)){
            if (minDate != 0L) {
                dialog.datePicker.minDate = minDate
            }
            if (maxDate != 0L) {
                dialog.datePicker.maxDate = maxDate
            }
            //}
            return dialog
        }

        return super.onCreateDialog(savedInstanceState)
    }

    override fun onDateSet(datePicker: DatePicker?, year: Int, month: Int, day: Int) {
        onDateSetCallback!!.onDateSet(datePicker, year, month, day)
    }
}
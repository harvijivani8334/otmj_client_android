package com.app.otmjobs.managejob.ui.fragment

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.app.otmjobs.R
import com.app.otmjobs.common.callback.SelectDateRangeListener
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.databinding.DialogSelectDateRangeBinding
import com.app.utilities.utils.*
import java.text.SimpleDateFormat
import java.util.*

class SelectDateRangeDialog(
    var mContext: Activity,
    var selectDateRangeListener: SelectDateRangeListener?
) : DialogFragment(), DatePickerDialog.OnDateSetListener, View.OnClickListener {

    private lateinit var binding: DialogSelectDateRangeBinding
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MyDialogFragmentStyle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val ad = AlertDialog.Builder(mContext)
        val i = mContext.layoutInflater
        val view = i.inflate(R.layout.dialog_select_date_range, null)
        binding = DataBindingUtil.bind(view)!!
        ad.setView(view)
        dialog = ad.create()
        dialog.setCancelable(false)

        binding.edtFromDate.setOnClickListener(this)
        binding.edtToDate.setOnClickListener(this)
        binding.txtSelect.setOnClickListener(this)
        binding.txtCancel.setOnClickListener(this)

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onClick(v: View?) {
        val c = Calendar.getInstance()
        when (v?.id) {
            R.id.edtFromDate -> {
                showDatePickerDialog(
                    c.time.time,
                    0,
                    binding.edtFromDate.text.toString(),
                    DateFormatsConstants.DD_MMM_YYYY_SPACE,
                    AppConstants.DialogIdentifier.START_DATE
                )
            }
            R.id.edtToDate -> {
                var minTime: Long = 0
                val date: String = binding.edtFromDate.text.toString()
                if (!StringHelper.isEmpty(date)) {
                    val dateFormat =
                        SimpleDateFormat(DateFormatsConstants.DD_MMM_YYYY_SPACE, Locale.US)
                    try {
                        val d = dateFormat.parse(date)
                        c.time = d!!
                        c[c[Calendar.YEAR], c[Calendar.MONTH]] = c[Calendar.DAY_OF_MONTH]
                        minTime = c.time.time
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }

                    showDatePickerDialog(
                        minTime,
                        0,
                        binding.edtFromDate.text.toString(),
                        DateFormatsConstants.DD_MMM_YYYY_SPACE,
                        AppConstants.DialogIdentifier.END_DATE
                    )
                }
            }
            R.id.txtSelect -> {
                if (!ValidationUtil.isEmptyEditText(
                        binding.edtFromDate.text.toString().trim()
                    )
                    && !ValidationUtil.isEmptyEditText(
                        binding.edtToDate.text.toString().trim()
                    )
                ) {
                    selectDateRangeListener!!.onSelectDate(
                        binding.edtFromDate.text.toString(),
                        binding.edtToDate.text.toString()
                    )
                    dismiss()
                } else {
                    ToastHelper.normal(
                        mContext, mContext.getString(R.string.error_empty_date_range),
                        Toast.LENGTH_SHORT,
                        false
                    )
                }
            }
            R.id.txtCancel -> {
                dismiss()
            }
        }
    }

    private fun showDatePickerDialog(
        minDate: Long,
        maxDate: Long,
        date: String?,
        dateFormat: String?,
        identifier: String?
    ) {
        val c = Calendar.getInstance(Locale.US)
        if (!StringHelper.isEmpty(date) && !StringHelper.isEmpty(dateFormat)) {
            try {
                c.time = DateHelper.stringToDate(date, dateFormat)!!
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val year = c[Calendar.YEAR]
        val month = c[Calendar.MONTH]
        val day = c[Calendar.DAY_OF_MONTH]
        val dialog = DatePickerDialog(mContext, this, year, month, day)
        dialog.datePicker.tag = identifier
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.datePicker.firstDayOfWeek = Calendar.MONDAY
        }
        if (minDate != 0L) {
            dialog.datePicker.minDate = minDate
        }
        if (maxDate != 0L) {
            dialog.datePicker.maxDate = maxDate
        }
        dialog.show()
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        if (view.tag.toString() == AppConstants.DialogIdentifier.START_DATE) {
            val dobDate = Calendar.getInstance()
            dobDate.set(year, month, dayOfMonth)
            val dateFormat = SimpleDateFormat(DateFormatsConstants.DD_MMM_YYYY_SPACE, Locale.US)
            binding.edtFromDate.setText(dateFormat.format(dobDate.time))
        } else if (view.tag.toString() == AppConstants.DialogIdentifier.END_DATE) {
            val dobDate = Calendar.getInstance()
            dobDate.set(year, month, dayOfMonth)
            val dateFormat = SimpleDateFormat(DateFormatsConstants.DD_MMM_YYYY_SPACE, Locale.US)
            binding.edtToDate.setText(dateFormat.format(dobDate.time))
        }
    }
}
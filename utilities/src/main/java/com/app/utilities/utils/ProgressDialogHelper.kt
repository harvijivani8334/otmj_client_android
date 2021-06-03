package com.app.utilities.utils

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import com.app.utilities.R

class ProgressDialogHelper(private val context: Context?) {
    private var roundedProgressDialog: Dialog? = null
    private var progressDialog: Dialog? = null
    fun showProgressDialog(message: String) {
        try {
            if (context == null) return
            if (progressDialog == null) {
                progressDialog = getProgressDialog(context, message)
            }
            if (context is Activity) {
                if (!context.isFinishing &&
                    !progressDialog!!.isShowing
                ) {
                    progressDialog!!.show()
                }
            } else if (!progressDialog!!.isShowing) {
                progressDialog!!.show()
            }
        } catch (e: Exception) {
            Log.e("ProgressDialog", "Error in showProgressDialog: " + e.message)
        }
    }

    fun hideProgressDialog() {
        try {
            if (progressDialog == null || !progressDialog!!.isShowing) return
            if (context is Activity) {
                if (!context.isFinishing) {
                    progressDialog!!.dismiss()
                }
            } else {
                progressDialog!!.dismiss()
            }
        } catch (e: Exception) {
            Log.e("ProgressDialog", "Error in hideProgressDialog: " + e.message)
        }
    }

    private fun getProgressDialog(context: Context, message: String): Dialog {
        val progressDialog = ProgressDialog(context, R.style.CustomAlertDialogStyleParentTheme)
        progressDialog.setCancelable(false)
        if (!StringHelper.isEmpty(message)) {
            progressDialog.setMessage(message)
        }
        return progressDialog
    }

    fun showCircularProgressDialog() {
        try {
            if (context == null) return
            roundedProgressDialog = setDialog(context)
            if (context is Activity) {
                if (!context.isFinishing && roundedProgressDialog != null && !roundedProgressDialog!!.isShowing
                ) {
                    roundedProgressDialog!!.show()
                }
            } else {
                if (roundedProgressDialog != null
                    && !roundedProgressDialog!!.isShowing
                ) {
                    roundedProgressDialog!!.show()
                }
            }
        } catch (e: Exception) {
            Log.e("ProgressDialog", "Error in showCircularProgressDialog: " + e.message)
        }
    }

    fun hideCircularProgressDialog() {
        try {
            if (roundedProgressDialog == null || !roundedProgressDialog!!.isShowing) return
            if (roundedProgressDialog != null
                && roundedProgressDialog!!.isShowing
            ) {
                roundedProgressDialog!!.dismiss()
            }
        } catch (e: Exception) {
            Log.e("ProgressDialog", "Error in hideCircularProgressDialog: " + e.message)
        } finally {
            roundedProgressDialog = null
        }
    }

    private fun setDialog(context: Context?): Dialog? {
        try {
            if (context == null) return null
            val dialog = Dialog(context)
            dialog.setCancelable(false)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val mInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view: View = mInflater.inflate(R.layout.dialog_loading, null, false)
            val wheel = view.findViewById<View>(R.id.progress_wheel) as ProgressWheel
            wheel.setBarColor(getThemeColors(context, R.attr.colorAccent))
            dialog.setContentView(view)
            if (dialog.window != null) {
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            return dialog
        } catch (e: Exception) {
            Log.e("ProgressDialog", "Error in setDialog: " + e.message)
        }
        return null
    }

    private fun getThemeColors(context: Context, color: Int): Int {
        val value = TypedValue()
        context.theme.resolveAttribute(color, value, true)
        return value.data
    }
}

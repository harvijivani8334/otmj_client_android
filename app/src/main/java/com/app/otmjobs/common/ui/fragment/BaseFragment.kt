package com.app.otmjobs.common.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.app.otmjobs.common.callback.OnFragmentBackListener
import com.app.utilities.utils.ProgressDialogHelper
import com.app.utilities.utils.StringHelper

open class BaseFragment : Fragment(), OnFragmentBackListener {
    private var progressDialogHelper: ProgressDialogHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onBackPressed(): Boolean {
        return BackPressImpl(this).onBackPressed()
    }


    fun moveActivity(
        context: Context,
        c: Class<*>?,
        finish: Boolean,
        clearStack: Boolean,
        bundle: Bundle?
    ) {
        val intent = Intent(context, c)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        if (clearStack) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
        if (finish) {
            (context as Activity).finish()
        }
    }

    fun moveActivity(
        context: Context,
        intent: Intent,
        finish: Boolean,

        clearStack: Boolean,
        bundle: Bundle?
    ) {
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        if (finish) {
            (context as Activity).finish()
        }
        if (clearStack) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }

    fun moveActivityForResult(
        context: Context,
        c: Class<*>?,
        finish: Boolean,
        clearStack: Boolean,
        requestCode: Int,
        bundle: Bundle?
    ) {
        val intent = Intent(context, c)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        if (clearStack) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        (context as Activity).startActivityForResult(intent, requestCode)
        val activity = context
        //        activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        if (finish) {
            context.finish()
        }
    }

    open fun showProgressDialog(context: Context, message: String?) {
        if (progressDialogHelper == null) {
            progressDialogHelper = ProgressDialogHelper(context)
        }
        if (StringHelper.isEmpty(message)) {
            progressDialogHelper!!.showCircularProgressDialog()
        } else {
            progressDialogHelper!!.showProgressDialog(message!!)
        }
    }

    open fun hideProgressDialog() {
        if (progressDialogHelper != null) {
            progressDialogHelper!!.hideProgressDialog()
            progressDialogHelper!!.hideCircularProgressDialog()
        }
    }

}
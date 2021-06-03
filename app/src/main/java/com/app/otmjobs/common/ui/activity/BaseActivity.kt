package com.app.otmjobs.common.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import com.app.otmjobs.R
import com.app.utilities.utils.ProgressDialogHelper
import com.app.utilities.utils.StringHelper

open class BaseActivity : AppCompatActivity() {
    private var progressDialogHelper: ProgressDialogHelper? = null

    fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (AppCompatDelegate.getDefaultNightMode()
                != AppCompatDelegate.MODE_NIGHT_YES
            ) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.statusBarColor = Color.WHITE
            } else {
                window.statusBarColor = Color.parseColor("#191b24")
            }
        }
    }

    fun setupToolbar(text: String?, isShowBack: Boolean) {
        Log.e("test", "setupToolbar");
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val title = findViewById<TextView>(R.id.toolBarNavigation)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            if (supportActionBar != null) {
                //getSupportActionBar().setTitle(toolbarTitle.toUpperCase());
                supportActionBar!!.setDisplayShowTitleEnabled(false)
                title.text = text
            }
            if (isShowBack) {
                Log.e("test", "isShowBack:" + isShowBack);
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
                toolbar.setNavigationOnClickListener { v: View? -> onBackPressed() }
            }
        }
    }

    fun setFullScreen() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun moveActivity(
        context: Context,
        c: Class<*>?,
        finish: Boolean,
        clearStack: Boolean,
        bundle: Bundle?
    ) {
        val intent = Intent(context, c)
        if (bundle != null)
            intent.putExtras(bundle)
        if (clearStack)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
        //        Activity activity = (Activity) context;
//        activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
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
        if (bundle != null)
            intent.putExtras(bundle)
        if (clearStack)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
        if (finish) {
            (context as Activity).finish()
        }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
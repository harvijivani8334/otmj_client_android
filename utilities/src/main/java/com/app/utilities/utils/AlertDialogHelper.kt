package com.app.utilities.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.Html
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.app.utilities.R
import com.app.utilities.callback.DialogButtonClickListener
import com.app.utilities.callback.DialogRadioButtonItemSelectListener

object AlertDialogHelper {
    private var isIsDialogOpen = false

    fun showDialog(
        context: Context,
        dialogTitle: String?, dialogMessage: String?, textPositiveButton: String?,
        textNegativeButton: String?, isCancelable: Boolean,
        buttonClickListener: DialogButtonClickListener?,
        dialogIdentifier: Int
    ) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        if (!StringHelper.isEmpty(dialogTitle)) {
            alertDialogBuilder.setTitle(dialogTitle)
        }
        alertDialogBuilder.setMessage(dialogMessage)
        if (isCancelable) {
            alertDialogBuilder.setCancelable(true)
        } else {
            alertDialogBuilder.setCancelable(false)
        }
        alertDialogBuilder.setOnDismissListener { isIsDialogOpen = false }
        alertDialogBuilder.setPositiveButton(
            textPositiveButton
        ) { dialog, id1 ->
            isIsDialogOpen = false
            if (buttonClickListener == null) {
                dialog.dismiss()
            } else {
                buttonClickListener.onPositiveButtonClicked(dialogIdentifier)
            }
        }
        if (textNegativeButton != null) {
            alertDialogBuilder.setNegativeButton(
                textNegativeButton
            ) { dialog, id1 ->
                isIsDialogOpen = false
                if (buttonClickListener == null) {
                    dialog.dismiss()
                } else {
                    buttonClickListener.onNegativeButtonClicked(dialogIdentifier)
                }
            }
        }
        val alertDialog = alertDialogBuilder.create()
        //	alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        if (!(context as Activity).isFinishing && !isIsDialogOpen) {
            isIsDialogOpen = true
            alertDialog.show()
        }
    }

    fun showDialog(
        context: Context,
        dialogTitle: String?, dialogMessage: String?, textPositiveButton: String?,
        textNegativeButton: String?, isCancelable: Boolean,
        finish: Boolean
    ) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        if (!StringHelper.isEmpty(dialogTitle)) {
            alertDialogBuilder.setTitle(dialogTitle)
        }
        alertDialogBuilder.setMessage(dialogMessage)
        if (isCancelable) {
            alertDialogBuilder.setCancelable(true)
        } else {
            alertDialogBuilder.setCancelable(false)
        }
        alertDialogBuilder.setOnDismissListener { isIsDialogOpen = false }
        alertDialogBuilder.setPositiveButton(
            textPositiveButton
        ) { dialog, id1 ->
            isIsDialogOpen = false
            if (finish) {
                (context as Activity).finish()
            }
        }
        if (textNegativeButton != null) {
            alertDialogBuilder.setNegativeButton(
                textNegativeButton
            ) { dialog, id1 ->
                isIsDialogOpen = false
                dialog.dismiss()
            }
        }
        val alertDialog = alertDialogBuilder.create()
        //	alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        if (!(context as Activity).isFinishing && !isIsDialogOpen) {
            isIsDialogOpen = true
            alertDialog.show()
        }
    }

    fun showDialogWithHtml(
        context: Context,
        dialogTitle: String?, dialogMessage: String?, textPositiveButton: String?,
        textNegativeButton: String?, isCancelable: Boolean,
        buttonClickListener: DialogButtonClickListener?,
        dialogIdentifier: Int
    ) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        if (!StringHelper.isEmpty(dialogTitle)) {
            alertDialogBuilder.setTitle(dialogTitle)
        }
        //        alertDialogBuilder.setMessage(dialogMessage);
        val htmlTextView = TextView(context)
        htmlTextView.textSize = context.resources.getDimension(R.dimen.dialog_text_size)
        htmlTextView.setTextColor(Color.BLACK)
        htmlTextView.setPadding(
            context.resources.getDimension(R.dimen.size_21dp).toInt(),
            context.resources.getDimension(R.dimen.size_11dp).toInt(),
            context.resources.getDimension(R.dimen.size_21dp).toInt(), 0
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            htmlTextView.text = Html.fromHtml(dialogMessage, Html.FROM_HTML_MODE_COMPACT)
        } else {
            htmlTextView.text = Html.fromHtml(dialogMessage)
        }
        alertDialogBuilder.setView(htmlTextView)
        if (isCancelable) {
            alertDialogBuilder.setCancelable(true)
        } else {
            alertDialogBuilder.setCancelable(false)
        }
        alertDialogBuilder.setPositiveButton(
            textPositiveButton
        ) { dialog, id1 ->
            if (buttonClickListener == null) {
                dialog.dismiss()
            } else {
                buttonClickListener.onPositiveButtonClicked(dialogIdentifier)
            }
        }
        if (textNegativeButton != null) {
            alertDialogBuilder.setNegativeButton(
                textNegativeButton
            ) { dialog, id1 ->
                if (buttonClickListener == null) {
                    dialog.dismiss()
                } else {
                    buttonClickListener.onNegativeButtonClicked(dialogIdentifier)
                }
            }
        }
        val alertDialog = alertDialogBuilder.create()
        //	alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        if (!(context as Activity).isFinishing) {
            alertDialog.show()
        }
    }

    fun showDropDownDialog(
        context: Context,
        dialogTitle: String?,
        dialogMessage: String?,
        listItems: Array<String?>?,
        checkedItem: Int,
        textPositiveButton: String?,
        textNegativeButton: String?,
        isCancelable: Boolean,
        itemSelectListener: DialogRadioButtonItemSelectListener?,
        dialogIdentifier: Int
    ) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        if (!StringHelper.isEmpty(dialogTitle)) {
            alertDialogBuilder.setTitle(dialogTitle)
        }
        alertDialogBuilder.setMessage(dialogMessage)
        if (isCancelable) {
            alertDialogBuilder.setCancelable(true)
        } else {
            alertDialogBuilder.setCancelable(false)
        }
        alertDialogBuilder.setOnDismissListener { isIsDialogOpen = false }
        alertDialogBuilder.setSingleChoiceItems(
            listItems, checkedItem
        ) { dialog, which ->
            isIsDialogOpen = false
            if (itemSelectListener == null) {
                dialog.dismiss()
            } else {
                dialog.dismiss()
                itemSelectListener.onItemSelect(dialogIdentifier, which)
            }
        }
        alertDialogBuilder.setPositiveButton(
            textPositiveButton
        ) { dialog, id1 ->
            isIsDialogOpen = false
            if (itemSelectListener == null) {
                dialog.dismiss()
            } else {
                dialog.dismiss()
                itemSelectListener.onPositiveButtonClicked(dialogIdentifier, 0)
            }
        }
        if (textNegativeButton != null) {
            alertDialogBuilder.setNegativeButton(
                textNegativeButton
            ) { dialog, id1 ->
                isIsDialogOpen = false
                if (itemSelectListener == null) {
                    dialog.dismiss()
                } else {
                    itemSelectListener.onNegativeButtonClicked(dialogIdentifier, 0)
                }
            }
        }
        val alertDialog = alertDialogBuilder.create()
        if (!(context as Activity).isFinishing && !isIsDialogOpen) {
            isIsDialogOpen = true
            alertDialog.show()
        }
    }

    fun showListAlertDialog(
        context: Context,
        dialogTitle: String?,
        dialogMessage: String?,
        listItems: Array<String?>?,
        checkedItem: Int,
        textPositiveButton: String?,
        textNegativeButton: String?,
        isCancelable: Boolean,
        itemSelectListener: DialogRadioButtonItemSelectListener?,
        dialogIdentifier: Int
    ) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        if (!StringHelper.isEmpty(dialogTitle)) {
            alertDialogBuilder.setTitle(dialogTitle)
        }
        alertDialogBuilder.setMessage(dialogMessage)
        if (isCancelable) {
            alertDialogBuilder.setCancelable(true)
        } else {
            alertDialogBuilder.setCancelable(false)
        }
        alertDialogBuilder.setOnDismissListener { isIsDialogOpen = false }
        alertDialogBuilder.setItems(
            listItems
        ) { dialog, which ->
            isIsDialogOpen = false
            if (itemSelectListener == null) {
                dialog.dismiss()
            } else {
                dialog.dismiss()
                itemSelectListener.onItemSelect(dialogIdentifier, which)
            }
        }
        alertDialogBuilder.setPositiveButton(
            textPositiveButton
        ) { dialog, id1 ->
            isIsDialogOpen = false
            if (itemSelectListener == null) {
                dialog.dismiss()
            } else {
                dialog.dismiss()
                itemSelectListener.onPositiveButtonClicked(dialogIdentifier, 0)
            }
        }
        if (textNegativeButton != null) {
            alertDialogBuilder.setNegativeButton(
                textNegativeButton
            ) { dialog, id1 ->
                isIsDialogOpen = false
                if (itemSelectListener == null) {
                    dialog.dismiss()
                } else {
                    itemSelectListener.onNegativeButtonClicked(dialogIdentifier, 0)
                }
            }
        }
        val alertDialog = alertDialogBuilder.create()
        if (!(context as Activity).isFinishing && !isIsDialogOpen) {
            isIsDialogOpen = true
            alertDialog.show()
        }
    }

    fun setIsDialogOpen(isDialogOpen: Boolean) {
        isIsDialogOpen = isDialogOpen
    }
}
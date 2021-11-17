package com.app.blockaat.helper

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Looper
import android.view.View
import android.view.Window
import android.widget.ImageView
import com.app.blockaat.R
import com.bumptech.glide.Glide


class CustomProgressBar(val activity: Activity?) {
    var dialog: Dialog? =
        null //..we need the context else we can not create the dialog so get context in constructor



    fun showDialog() {
        //start a new thread to process job
        Thread(Runnable {
            //heavy job here
            //send message to main thread
            Looper.prepare()
            val v = activity?.layoutInflater?.inflate(R.layout.custom_progress_dialog, null)

            dialog = activity?.let { Dialog(it, R.style.DialogStyle) }
            //...that's the layout i told you will inflate later
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            if (v != null) {
                dialog?.setContentView(v)
            }
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            //...set cancelable false so that it's never get hidden
            dialog?.setCancelable(false)
            dialog?.setCanceledOnTouchOutside(false)

            //...initialize the imageView form infalted layout
            dialog?.show()
            Looper.loop()
        }).start()

    }

    //..also create a method which will hide the dialog when some work is done
    fun hideDialog() {
        activity?.runOnUiThread {
            dialog?.dismiss()
        }

    }
}
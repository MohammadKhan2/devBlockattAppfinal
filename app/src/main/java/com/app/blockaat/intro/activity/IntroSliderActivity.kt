package com.app.blockaat.intro.activity

import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.View.VISIBLE
import android.view.Window
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.app.blockaat.R
import com.app.blockaat.helper.*
import com.app.blockaat.intro.model.IntroResponseModelItem
import com.app.blockaat.navigation.NavigationActivity
import kotlinx.android.synthetic.main.activity_intro_slider.*

class IntroSliderActivity : BaseActivity() {
    private var introModel: IntroResponseModelItem? = null
    private var dialog: CustomProgressBar? = null
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFormat(PixelFormat.TRANSLUCENT)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_intro_slider)
        initializeToolbar()
        initializeFields()
        setFonts()
        onClickListener()
    }


    private fun initializeToolbar() {

    }

    private fun initializeFields() {
        dialog = CustomProgressBar(this)
        val lpImage = RelativeLayout.LayoutParams(
            Global.getDeviceWidth(this),
            Global.getDeviceHeight(this)
        )
//        ivIntro.layoutParams = lpImage
//        showProgressDialog(this)
        if (intent.hasExtra("introModel")) {
            introModel = intent.extras?.get("introModel") as IntroResponseModelItem
//            Global.loadImagesUsingGlide(this, introModel?.image, ivIntro)
            Glide.with(this)
                .load(introModel?.image)
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                }).into(ivIntro)
            linBottomLayout.visibility = VISIBLE
        }
        Global.saveStringInSharedPref(this, Constants.PREFS_INTRO_SHOWN, "yes")
        handler.postDelayed({
            txtSkip.performClick()

        }, 5000)
    }


    private fun setFonts() {
        txtSkip.typeface = Global.fontBtn
        txtEnter.typeface = Global.fontBtn
    }

    private fun onClickListener() {
        txtSkip.setOnClickListener {
            handler.removeCallbacksAndMessages(null)
            val intent = Intent(this, NavigationActivity::class.java)
            startActivity(intent)
            finish()
        }
        txtEnter.setOnClickListener {
            handler.removeCallbacksAndMessages(null)
            if (introModel != null) {
                val intent = Intent(this, NavigationActivity::class.java)
                intent.putExtra("introModel", introModel)
                startActivity(intent)
                finish()

            }
        }
    }

    private fun showProgressDialog(mActivity: AppCompatActivity) {
        dialog?.showDialog()

    }

    private fun hideProgressDialog() {
        dialog?.hideDialog()

    }


}
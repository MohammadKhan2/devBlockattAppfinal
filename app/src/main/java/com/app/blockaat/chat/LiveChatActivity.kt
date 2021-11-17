package com.app.blockaat.chat

import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import com.app.blockaat.R
import com.app.blockaat.helper.AppController
import com.app.blockaat.helper.BaseActivity
import com.app.blockaat.helper.Global
import com.zopim.android.sdk.api.Chat
import com.zopim.android.sdk.api.ZopimChat
import com.zopim.android.sdk.chatlog.ZopimChatLogFragment
import com.zopim.android.sdk.embeddable.ChatActions
import com.zopim.android.sdk.model.VisitorInfo
import com.zopim.android.sdk.prechat.ChatListener
import com.zopim.android.sdk.prechat.PreChatForm
import com.zopim.android.sdk.prechat.ZopimChatFragment
import com.zopim.android.sdk.widget.ChatWidgetService
import kotlinx.android.synthetic.main.activity_live_chat.*

class LiveChatActivity : BaseActivity(), ChatListener {
    private var appController: AppController? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_chat)

        initializeToolbar()
        initializeFields()
        setFonts()

    }


    //initializing toolbar
    private fun initializeToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        val upArrow = resources.getDrawable(R.drawable.abc_ic_ab_back_material)
        upArrow.setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_ATOP)
        upArrow.setVisible(true, true)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    private fun initializeFields() {
        appController = this!!.applicationContext as AppController
        //ZopimChat.init("3a2a5d9c-fd20-4097-872a-cef230763d30")
        //Adding permission for Overlay
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName"))
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE)
            }
        }

        /*
          If starting activity while the chat widget is actively presented the activity will resume the current chat
         */
        val widgetWasActive = stopService(Intent(this, ChatWidgetService::class.java))
        if (widgetWasActive) {
            resumeChat()
            return
        }

        /*
          We've received an intent request to resume the existing chat.
          Resume the chat via {@link com.zopim.android.sdk.api.ZopimChat#resume(android.support.v4.app.FragmentActivity)} and
          start the {@link ZopimChatLogFragment}
         */
        if (intent != null) {
            val action = intent.action
            if (ChatActions.ACTION_RESUME_CHAT == action) {
                resumeChat()
                return
            }
        }

        /*
          Attempt to resume chat. If there is an active chat it will be resumed.
         */
        val chat = ZopimChat.resume(this)
        if (!chat.hasEnded()) {
            resumeChat()
            return
        }

        /*
          Start a new chat
         */
        run {
            // build and set visitor info
            val visitorInfo = VisitorInfo.Builder()
                .phoneNumber("")
                .email("")
                .name("")
                .build()

            // visitor info can be set at any point when that information becomes available
            ZopimChat.setVisitorInfo(visitorInfo)

            // set pre chat fields as mandatory
            val preChatForm = PreChatForm.Builder()
                .name(PreChatForm.Field.NOT_REQUIRED)
                .email(PreChatForm.Field.NOT_REQUIRED)
                .phoneNumber(PreChatForm.Field.NOT_REQUIRED)
                .department(PreChatForm.Field.NOT_REQUIRED)
                .message(PreChatForm.Field.NOT_REQUIRED)
                .build()
            // build chat config
            val config = ZopimChat.SessionConfig()
                .preChatForm(preChatForm)
            // prepare chat fragment
            val fragment = ZopimChatFragment.newInstance(config)
            // show fragment
            val manager = supportFragmentManager
            val transaction = manager.beginTransaction()
            transaction.replace(R.id.chat_fragment_container, fragment, ZopimChatFragment::class.java.name)
            transaction.commit()
        }
    }

    private fun setFonts() {
        txtToolbarHeader.typeface = Global.fontNavBar
    }

    private fun resumeChat() {
        val manager = supportFragmentManager
        // find the retained fragment
        if (manager.findFragmentByTag(ZopimChatLogFragment::class.java.name) == null) {
            val chatLogFragment = ZopimChatLogFragment()
            val transaction = manager.beginTransaction()
            transaction.add(com.zopim.android.sdk.R.id.chat_fragment_container, chatLogFragment, ZopimChatLogFragment::class.java.name)
            transaction.commit()
        }
    }


    override fun onChatLoaded(chat: Chat) {

    }

    override fun onChatInitialized() {

    }

    override fun onChatEnded() {
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                run { finish() }
            }

            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)

    }

    companion object {
        private val OVERLAY_PERMISSION_REQ_CODE = 100
        private val SCREEN_NAME = "Live Support Screen"

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

}


package com.app.blockaat.helper


import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.AdjustEvent
import com.adjust.sdk.LogLevel
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.app.blockaat.R
import com.facebook.FacebookSdk
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.iid.FirebaseInstanceId
import com.instabug.library.Feature
import com.instabug.library.Instabug
import com.instabug.library.invocation.InstabugInvocationEvent
import com.parse.*
import com.parse.fcm.ParseFCM
import com.pushwoosh.Pushwoosh
import io.branch.referral.Branch
import java.util.*


class AppController : Application() {
    private lateinit var markerUpdatesReceiver: CustomPushReceiver
    private var sAnalytics: GoogleAnalytics? = null
    var fAnalytics:FirebaseAnalytics? = null
    private var sTracker: Tracker? = null
    private lateinit var referrerClient: InstallReferrerClient

    companion object {
        lateinit var instance: AppController
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // get analytics instance
        sAnalytics = GoogleAnalytics.getInstance(this);
        //updated by azim
        fAnalytics = FirebaseAnalytics.getInstance(this)

//      FacebookSdk.sdkInitialize(applicationContext)
        FacebookSdk.sdkInitialize(this)
        Branch.getAutoInstance(this)// Branch object initialization
//        Branch.enableTestMode() // Branch logging for debugging
        Branch.disableDebugMode()
        Branch.disableLogging()

        // Adjust config
        val appToken = "2esby2kb8bgg"
        val environment = AdjustConfig.ENVIRONMENT_PRODUCTION
        val config = AdjustConfig(this, appToken, environment)
        config.setLogLevel(LogLevel.VERBOSE);
        Adjust.onCreate(config)


        registerActivityLifecycleCallbacks(AdjustLifecycleCallbacks())

        //it means -if no language set then it will check device language and set device language as default
        if (Global.getStringFromSharedPref(this, Constants.PREFS_LANGUAGE).isNullOrEmpty()) {
            if (Locale.getDefault().displayLanguage.equals("English", true)) {
                //this will set english language
                Global.saveStringInSharedPref(this, Constants.PREFS_LANGUAGE, "ar")
                changeLanguage()
            } else {
                //this will set arabic language
                Global.saveStringInSharedPref(this, Constants.PREFS_LANGUAGE, "en")
                changeLanguage()
            }
        } else {
            setLocale()
            Global.setFont(Global.getStringFromSharedPref(this, Constants.PREFS_LANGUAGE), this)
        }
      //  initInstaBug()
//      initParse()
        CustomEvents.setFirebaseAnalytics(this) // firebase analytics
        Pushwoosh.getInstance().registerForPushNotifications()
        initTracking()
    }

    fun changeLanguage() {
        if (Global.getStringFromSharedPref(this, Constants.PREFS_LANGUAGE) == "en") {
            arabicLanguage()
        } else {
            englishLanguage()
        }
    }

    fun arabicLanguage() {
        Global.saveStringInSharedPref(this, Constants.PREFS_LANGUAGE, "ar")
        setLocale()
       // initInstaBug()
        Global.setFont("ar", this)
    }

    fun englishLanguage() {
        Global.saveStringInSharedPref(this, Constants.PREFS_LANGUAGE, "en")
        setLocale()
       // initInstaBug()
        Global.setFont("en", this)
    }

    fun setLocale() {
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.setLocale(
            Locale(
                Global.getStringFromSharedPref(this, Constants.PREFS_LANGUAGE).toLowerCase()
            )
        )
        res.updateConfiguration(conf, dm)
    }

    private fun initParse() {
       // println("Init parse")
        Parse.enableLocalDatastore(this)
        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId("")//yewywFvWNN8TXBQRghzcfmS9s6VfH3dgPG4XEL8b
                .server("")//http://push.inovantsolutions.com:1337/boutikey
                .build()
        )

        ParseUser.enableAutomaticUser()
        val defaultACL = ParseACL()
        ParseACL.setDefaultACL(defaultACL, true)
        ParseInstallation.getCurrentInstallation().saveInBackground(object : SaveCallback {
            override fun done(e: ParseException?) {
                if (e == null) {
                    val token: String? = ParseInstallation.getCurrentInstallation().deviceToken
                    // println("token string: " + token)
                    if (token == null) {
                        FirebaseInstanceId.getInstance().instanceId
                            .addOnCompleteListener(OnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    Log.w(
                                        "SPLASH",
                                        "getInstanceId failed",
                                        task.exception
                                    )
                                    return@OnCompleteListener
                                }
                                // Get new Instance ID token
                                val token = task.result?.token
                                // println("TOKEN STRING: " + token)
                                ParseInstallation.getCurrentInstallation().deviceToken = token
                                ParseFCM.register(applicationContext)
                                Constants.DEVICE_TOKEN = token.toString()
                            })
                    } else {
                        //  println("register token")
                        ParseInstallation.getCurrentInstallation().deviceToken = token
                        ParseFCM.register(applicationContext)
                        Constants.DEVICE_TOKEN = token.toString()
                    }
                } else {
                    e.printStackTrace()
                }
            }
        })
        ParsePush.subscribeInBackground("") { e ->
            if (e == null) {
            }
        }
    }

    private fun initInstaBug() {
        val locale = Locale(Global.getStringFromSharedPref(this, Constants.PREFS_LANGUAGE))
        Instabug.Builder(this, Global.INSTABUG_KEY_DEBUG)
            .setInvocationEvent(InstabugInvocationEvent.SHAKE)
            .setInvocationEvent(InstabugInvocationEvent.FLOATING_BUTTON)
            .setLocale(locale)
            .setCrashReportingState(Feature.State.ENABLED)
            .setIntroMessageEnabled(false)
            .build()
    }

    /**
     * Gets the default [Tracker] for this [Application].
     * @return tracker
     */
    @Synchronized
    fun getDefaultTracker(): Tracker? {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics?.newTracker(R.xml.app_tracker)
        }
        return sTracker
    }

    fun trackScreenView(screenName: String){
        val tracker:Tracker = getDefaultTracker()!!
        tracker.setScreenName(screenName)
        tracker.send(HitBuilders.ScreenViewBuilder().build())
        sAnalytics?.dispatchLocalHits()
    }

    private fun initTracking() {
        referrerClient = InstallReferrerClient.newBuilder(this).build()
        referrerClient.startConnection(object : InstallReferrerStateListener {

            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        // Connection established.
                        obtainReferrerDetails()
                    }
                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                        // API not available on the current Play Store app.
                    }
                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                        // Connection couldn't be established.
                    }
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    private fun obtainReferrerDetails(){
        val response: ReferrerDetails = referrerClient.installReferrer
        Log.d("response - ", response.toString())
        val referrerUrl: String = response.installReferrer
        Log.d("referrerUrl - ", referrerUrl)
        val referrerClickTime: Long = response.referrerClickTimestampSeconds
        Log.d("referrerClickTime - ", referrerClickTime.toString())
        val appInstallTime: Long = response.installBeginTimestampSeconds
        Log.d("appInstallTime - ", appInstallTime.toString())
        val instantExperienceLaunched: Boolean = response.googlePlayInstantParam
        Log.d("instantExperienceLaunch", instantExperienceLaunched.toString())
    }

    private class AdjustLifecycleCallbacks : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityResumed(activity: Activity) {
            Adjust.onResume()
        }

        override fun onActivityPaused(activity: Activity) {
            Adjust.onPause()
        } //...

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityDestroyed(activity: Activity) {
        }
    }
}
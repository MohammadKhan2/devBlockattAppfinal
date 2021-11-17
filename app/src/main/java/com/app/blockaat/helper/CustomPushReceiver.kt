package com.app.blockaat.helper

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import com.app.blockaat.navigation.NavigationActivity
import com.parse.ParsePushBroadcastReceiver
import org.json.JSONException
import org.json.JSONObject

class CustomPushReceiver : ParsePushBroadcastReceiver() {
    private val TAG = CustomPushReceiver::class.java.simpleName

    //  private NotificationUtils notificationUtils;

    private var pref_push_detail: SharedPreferences? = null
    private var parseIntent: Intent? = null


    override fun onPushReceive(context: Context, intent: Intent) {
        super.onPushReceive(context, intent)

        if (intent == null)
            return

        try {
            val json = JSONObject(intent.extras!!.getString("com.parse.Data"))
            if (json.has("target")) {
                /*     if (json.getString("target").equals("O")) {

                         if (!Global.isMyServiceRunning(PlayAudio::class.java,context)) {
                             val objIntent = Intent(context, PlayAudio::class.java)
                             context.startService(objIntent)
                         }
                     }*/
            }


            parseIntent = intent

            pref_push_detail = context.getSharedPreferences("push_detail", 0)
            val push_count = pref_push_detail!!.getInt("count", 0) + 1
            val editorpush = context.getSharedPreferences("push_detail", 0).edit()
            editorpush.putInt("count", push_count)
            editorpush.commit()


        } catch (e: JSONException) {
        }

    }

    private fun isAppIsInBackground(context: Context): Boolean {
        var isInBackground = true
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            val runningProcesses = am.runningAppProcesses
            for (processInfo in runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (activeProcess in processInfo.pkgList) {
                        if (activeProcess == context.packageName) {
                            isInBackground = false
                        }
                    }
                }
            }
        } else {
            val taskInfo = am.getRunningTasks(1)
            val componentInfo = taskInfo[0].topActivity
            if (componentInfo?.packageName == context.packageName) {
                isInBackground = false
            }
        }

        return isInBackground
    }


    override fun onPushDismiss(context: Context?, intent: Intent?) {
        super.onPushDismiss(context, intent)
    }

    override fun onPushOpen(context: Context, intent: Intent) {
        var intent = intent
        println("json data: " + JSONObject(intent.extras!!.getString("com.parse.Data")))
        try {
            val json = JSONObject(intent.extras!!.getString("com.parse.Data"))
            intent = Intent(context, NavigationActivity::class.java)
            intent.putExtra(Constants.PUSH_RECEIVE_EVENTS, json.toString())
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)

            /*if (json.has("target")) {
                if (Global.getStringFromSharedPref(context,Constants.PREFS_isUSER_LOGGED_IN)=="yes") {
                    val target = json.getString("target")
                    intent = Intent(context, SplashActivity::class.java)
                    intent.putExtra("isFromPush", "yes")
                    intent.putExtra("id", json.getString("target_id"))
                    intent.putExtra("target", json.getString("target"))
                    intent.putExtra("title", json.getString("title"))
                    intent.putExtra("alert", json.getString("alert"))
                    intent.putExtra("type", json.getString("target"))
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                }else{
                    intent = Intent(context, SplashActivity::class.java)
                    intent.putExtra("from", "push")
                    intent.putExtra("title", json.getString("title"))
                    intent.putExtra("alert", json.getString("alert"))
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                }
            } else {
                intent = Intent(context, SplashActivity::class.java)
                intent.putExtra("from", "push")
                intent.putExtra("title", json.getString("title"))
                intent.putExtra("alert", json.getString("alert"))
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            }*/
        } catch (e: Exception) {
                println("exception in pooshwoosh"+e.localizedMessage)
        }

    }
}
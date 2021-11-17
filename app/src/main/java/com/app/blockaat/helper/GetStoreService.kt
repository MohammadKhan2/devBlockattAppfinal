package com.app.blockaat.helper

import android.app.IntentService
import android.content.Intent
import android.content.Context
import io.reactivex.disposables.CompositeDisposable

// TODO: Rename actions, choose action names that describe tasks that this
// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
private const val ACTION_FOO = "com.app.boutikey.helper.action.FOO"
private const val ACTION_BAZ = "com.app.boutikey.helper.action.BAZ"

// TODO: Rename parameters
private const val EXTRA_PARAM1 = "com.app.boutikey.helper.extra.PARAM1"
private const val EXTRA_PARAM2 = "com.app.boutikey.helper.extra.PARAM2"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class GetStoreService : IntentService("GetStoreService") {

    private var mCompositeDisposable: CompositeDisposable? = null

    override fun onCreate() {
        super.onCreate()
        mCompositeDisposable = CompositeDisposable()

    }

    override fun onHandleIntent(intent: Intent?) {
          getStoreList()
    }

    private fun getStoreList()
    {
        try{
            val requestInterface = RestClient.create()

            val call = requestInterface.getStoresAsync(com.app.blockaat.apimanager.WebServices.StoreWs + Global.getStringFromSharedPref(this, Constants.PREFS_LANGUAGE))
            val storeListCall =  call.execute()
            if(storeListCall.isSuccessful)
            {
                if(storeListCall.body()!=null)
                {
                    val storeListResponse = storeListCall.body()

                    if (storeListResponse?.status == 200) {
                        Global.setStore(storeListResponse.data)
                    }
                }
            }
        }catch (e: Exception)
        {}

    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionFoo(param1: String, param2: String) {
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionBaz(param1: String, param2: String) {
    }

    companion object {
        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionFoo(context: Context, param1: String, param2: String) {
            val intent = Intent(context, GetStoreService::class.java).apply {
                action = ACTION_FOO
                putExtra(EXTRA_PARAM1, param1)
                putExtra(EXTRA_PARAM2, param2)
            }
            context.startService(intent)
        }

        /**
         * Starts this service to perform action Baz with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionBaz(context: Context, param1: String, param2: String) {
            val intent = Intent(context, GetStoreService::class.java).apply {
                action = ACTION_BAZ
                putExtra(EXTRA_PARAM1, param1)
                putExtra(EXTRA_PARAM2, param2)
            }
            context.startService(intent)
        }
    }
}

package com.app.blockaat.helper;

import android.content.Intent;

import androidx.annotation.MainThread;

import com.app.blockaat.navigation.NavigationActivity;
import com.pushwoosh.Pushwoosh;
import com.pushwoosh.notification.NotificationServiceExtension;
import com.pushwoosh.notification.PushMessage;

public class PushWooshNotificationService extends NotificationServiceExtension {
    @Override
    protected void startActivityForPushMessage(PushMessage message) {
        handlePush(message);
    }

    @MainThread
    private void handlePush(PushMessage message) {
        Intent launchIntent = new Intent(getApplicationContext(), NavigationActivity.class);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        launchIntent.putExtra(Pushwoosh.PUSH_RECEIVE_EVENT, message.toJson().toString());
        getApplicationContext().startActivity(launchIntent);
    }
}

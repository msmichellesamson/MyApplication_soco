package com.example.myapplication_soco;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessaging;
public class NotificationHelper {
    private static final String TAG = "NotificationHelper";

    // Topic to subscribe for notifications (Replace with your topic name)
    private static final String TOPIC_GENERAL = "general";

    // SharedPreferences key for notification settings
    private static final String PREF_NOTIFICATION_SETTINGS = "notification_settings";
    private static final String PREF_KEY_NOTIFICATION_ENABLED = "notification_enabled";

    // Subscribe to a topic (for receiving notifications)
    public static void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_GENERAL)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Subscribed to topic: " + TOPIC_GENERAL);
                    } else {
                        Log.w(TAG, "Subscribe to topic failed: " + TOPIC_GENERAL, task.getException());
                    }
                });
    }

    // Unsubscribe from a topic (to stop receiving notifications)
    public static void unsubscribeFromTopic() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC_GENERAL)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Unsubscribed from topic: " + TOPIC_GENERAL);
                    } else {
                        Log.w(TAG, "Unsubscribe from topic failed: " + TOPIC_GENERAL, task.getException());
                    }
                });
    }

    // Enable notifications (subscribe to topic and update settings)
    public static void enableNotifications(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NOTIFICATION_SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putBoolean(PREF_KEY_NOTIFICATION_ENABLED, true);
        editor.apply();

        subscribeToTopic();
    }

    // Disable notifications (unsubscribe from topic and update settings)
    public static void disableNotifications(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NOTIFICATION_SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putBoolean(PREF_KEY_NOTIFICATION_ENABLED, false);
        editor.apply();

        unsubscribeFromTopic();
    }

    // Check if notifications are enabled
    public static boolean areNotificationsEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NOTIFICATION_SETTINGS, Context.MODE_PRIVATE);
        return prefs.getBoolean(PREF_KEY_NOTIFICATION_ENABLED, false);
    }

}
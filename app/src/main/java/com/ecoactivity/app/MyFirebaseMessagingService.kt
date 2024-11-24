package com.ecoactivity.app

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

public class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "Mensagem recebida de: ${remoteMessage.from}")
        remoteMessage.notification?.let {
            Log.d("FCM", "Título: ${it.title}, Corpo: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        Log.d("FCM", "Novo token: $token")
    }
}

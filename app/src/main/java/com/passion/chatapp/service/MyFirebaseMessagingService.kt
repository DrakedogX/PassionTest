package com.passion.chatapp.service

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.passion.chatapp.Utils.AuthUtil
import com.passion.chatapp.Utils.FirestoreUtil

class MyFirebaseMessagingService : FirebaseMessagingService() {


    companion object {
        fun getInstanceId(): Unit {
            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        println("MyFirebaseMessagingService.getInstanceId:${task.exception}")
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result?.token
                    println("MyFirebaseMessagingService.s:${token}")
                    if (token != null) {
                        addTokenToUserDocument(token)
                    }

                })

        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        println("MyFirebaseMessagingService.onNewToken:${token}")
        addTokenToUserDocument(token)
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.notification != null) {
            println("MyFirebaseMessagingService.onMessageReceived:${remoteMessage.data}")
        }
    }


}

fun addTokenToUserDocument(token: String) {
    val loggedUserID = AuthUtil.firebaseAuthInstance.currentUser?.uid
    if (loggedUserID != null) {
        FirestoreUtil.firestoreInstance.collection("users").document(loggedUserID)
            .update("token", token)
    }

}
package com.passion.chatapp.Utils

import com.google.firebase.storage.FirebaseStorage

object StorageUtil {

    val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()

    }
}
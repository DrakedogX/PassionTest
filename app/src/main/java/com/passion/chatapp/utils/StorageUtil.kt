package com.passion.chatapp.utils

import com.google.firebase.storage.FirebaseStorage

object StorageUtil {

    val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()

    }
}
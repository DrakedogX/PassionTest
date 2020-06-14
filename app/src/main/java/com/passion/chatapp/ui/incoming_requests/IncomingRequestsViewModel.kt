package com.passion.chatapp.ui.incoming_requests

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.passion.chatapp.utils.FirestoreUtil
import com.passion.chatapp.data.model.User
import com.passion.chatapp.ui.different_user_profile.RECEIVED_REQUEST_ARRAY
import com.passion.chatapp.ui.different_user_profile.SENT_REQUEST_ARRAY

const val FRIENDS = "friends"


class IncomingRequestsViewModel : ViewModel() {

    private val usersRef = FirestoreUtil.firestoreInstance.collection("users")
    private val friendRequestersMutableLiveData = MutableLiveData<MutableList<User>?>()


    fun downloadRequests(receivedRequests: List<String>): LiveData<MutableList<User>?> {

        val friendRequesters = mutableListOf<User>()

        for (receivedRequest in receivedRequests) {
            usersRef.document(receivedRequest).get().addOnSuccessListener {
                val user = it?.toObject(User::class.java)
                user?.let { it1 -> friendRequesters.add(it1) }
                friendRequestersMutableLiveData.value = friendRequesters

            }.addOnFailureListener {
                friendRequestersMutableLiveData.value = null
            }
        }
        return friendRequestersMutableLiveData
    }


    fun addToFriends(
        requesterId: String,
        loggedUserId: String
    ) {
        deleteRequest(requesterId, loggedUserId)

        FirestoreUtil.firestoreInstance.collection("users").document(requesterId)
            .update(FRIENDS, FieldValue.arrayUnion(loggedUserId)).addOnSuccessListener {
                        //add loggedInUserId in receivedRequest array for other user
                FirestoreUtil.firestoreInstance.collection("users").document(loggedUserId)
                    .update(FRIENDS, FieldValue.arrayUnion(requesterId))
                            .addOnSuccessListener {

                            }.addOnFailureListener {

                            }
                    }.addOnFailureListener {

                    }
        }




    fun deleteRequest(
        requesterId: String,
        loggedUserId: String
    ) {
                FirestoreUtil.firestoreInstance.collection("users").document(loggedUserId)
                    .update(RECEIVED_REQUEST_ARRAY, FieldValue.arrayRemove(requesterId))
                    .addOnSuccessListener {
                        FirestoreUtil.firestoreInstance.collection("users").document(requesterId)
                            .update(
                                SENT_REQUEST_ARRAY,
                                FieldValue.arrayRemove(loggedUserId)
                            )
                            .addOnSuccessListener {

                            }.addOnFailureListener {

                            }
                    }.addOnFailureListener {

                    }
    }

}





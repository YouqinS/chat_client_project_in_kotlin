package sunc.youqin.chatapp04.user

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.util.Log
import com.google.firebase.auth.UserProfileChangeRequest



object UserManagement {
    val TAG = "User Management:"

    //https://firebase.google.com/docs/auth/android/manage-users
    fun getCurrentlySignedInUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    fun getUsername():String? {
        return getCurrentlySignedInUser()?.getDisplayName();
    }



    fun setUsername( name: String) {
        val user = FirebaseAuth.getInstance().currentUser

        val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()

        user!!.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User profile updated.")
                    }
                }

    }


}
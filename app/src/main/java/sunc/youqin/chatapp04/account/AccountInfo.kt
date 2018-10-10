package sunc.youqin.chatapp04.account

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_account_info.*
import sunc.youqin.chatapp04.R
import sunc.youqin.chatapp04.user.UserManagement

class AccountInfo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_info)

        //get current user from firebase and display user name, email
        val currentUser = FirebaseAuth.getInstance().currentUser

        userName_EditText_AccountActivity.setText(currentUser?.displayName)

        email_EditText_AccountActivity.setText(currentUser?.email)


        buttonConfirm_AccountActivity.setOnClickListener {
            updatePassword(currentUser)
        }

    }

    //update password in firebase
    private fun updatePassword(currentUser: FirebaseUser?) {

        val user = FirebaseAuth.getInstance().currentUser //get current user from firebase

        val newPassword = password_EditText_AccountActivity.text.toString() //get new password from user input

        user!!.updatePassword(newPassword).addOnCompleteListener { task ->  //update in firebase
            if (task.isSuccessful) {

                Toast.makeText(this, "Password has been updated successfully!", Toast.LENGTH_LONG).show()

                password_EditText_AccountActivity.text.clear()
            }else{
                Toast.makeText(this, "Failed to update password. Try later!", Toast.LENGTH_LONG).show()
            }
        }






    }
}

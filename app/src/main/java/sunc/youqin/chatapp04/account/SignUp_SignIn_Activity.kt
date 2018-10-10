package sunc.youqin.chatapp04.account

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_signup_and_signin.*
import sunc.youqin.chatapp04.home_screen.HomeScreenActivity
import sunc.youqin.chatapp04.R
import sunc.youqin.chatapp04.network.SocketManager
import sunc.youqin.chatapp04.user.UserManagement


class SignUp_SignIn_Activity() : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_and_signin)

        //sign up button operations
        buttonSignUp.setOnClickListener {
            signUp()
        }

        //sign in button operations
        buttonSignIn.setOnClickListener{
            signIn()
        }


        //cancel button, exit process
        buttonCancel.setOnClickListener {
            finish()
        }


    }



    private fun signUp() {
        val userEmail = email_EditText_SignUpActivity.text.toString()
        val userPassword = password_EditText_SignUpActivity.text.toString()
        val userName = userName_EditText_SignUpActivity.text.toString()

        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(this, "email or passowrd is empty", Toast.LENGTH_LONG).show()
        }

        Log.d("SignUp_SignIn_Activity", "signUp() with Email, Password,Username : ($userEmail,$userPassword,$userName")



        //Firebase authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener {
                    if (!it.isSuccessful) {
                        return@addOnCompleteListener
                    } else{
                        UserManagement.setUsername(userName)

                        SocketManager.send(":user $userName")
                        Log.d("SignUp", "pass user name to server")

                        //else if successful, go to set photo activity
                        Log.d("SignUp_SignIn_Activity", "successfully created user with uid: ${it.result.user.uid}")
                        Toast.makeText(this, "successfully registered", Toast.LENGTH_LONG).show()

                        val intentSetPhoto = Intent(this, SetPhotoStoreUserToFirebase::class.java)
                        intentSetPhoto.putExtra("userName", userName)
                        startActivity(intentSetPhoto)

                        Log.d("SignUp_SignIn_Activity", "going to set photo activity")
                    }
                }
                .addOnFailureListener {
                    Log.d("SignUp_SignIn_Activity", "Failed to create user: ${it.message}")
                    Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_LONG).show()

                }
        Log.d("SignUp_SignIn_Activity", "test signUp")
    }

    private fun signIn() {
        val userEmail = email_EditText_SignUpActivity.text.toString()
        val userPassword = password_EditText_SignUpActivity.text.toString()
        val userName = userName_EditText_SignUpActivity.text.toString()

        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(this, "email or passowrd is empty", Toast.LENGTH_LONG).show()
        }

        Log.d("SignUp_SignIn_Activity", "signIn() with Email, Password : ($userEmail,$userPassword")


        //Firebase authentication to log in with email and password
        FirebaseAuth.getInstance().signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener {
                    if (!it.isSuccessful) {return@addOnCompleteListener}

                    //else if log in successful, going to PublicGroupChat
                    else{
                        Log.d("SignUp_SignIn_Activity", "successfully logged in with uid: ${it.result.user.uid}")

                        SocketManager.send(":user $userName")
                        Log.d("SignIn_Activity", "pass user name to server")

                        password_EditText_SignUpActivity.text.clear()


                        val intentHomeScreenActivity = Intent(this, HomeScreenActivity::class.java)
                        startActivity(intentHomeScreenActivity)

                        Log.d("SignUp_SignIn_Activity", "going to HomeScreenActivity")
                    }
                }
                .addOnFailureListener {
                    Log.d("SignUp_SignIn_Activity", "Failed to log in: ${it.message}")
                    Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_LONG).show()

                }
        Log.d("SignUp_SignIn_Activity", "test log in")
    }




}

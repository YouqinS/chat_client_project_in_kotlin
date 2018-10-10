package sunc.youqin.chatapp04.account

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_set_photo.*
import java.util.*
import com.google.firebase.firestore.FirebaseFirestore
import sunc.youqin.chatapp04.home_screen.HomeScreenActivity
import sunc.youqin.chatapp04.R
import sunc.youqin.chatapp04.user.User
import sunc.youqin.chatapp04.user.UserManagement


class SetPhotoStoreUserToFirebase : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_photo)

        //click button to set photo
        buttonSetPhoto.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }

        //confirm set photo, upload image to firebase storage, and save user to firestore
        buttonConfirmSetPhoto.setOnClickListener {
            uploadImageToFirebaseStorage()
        }


        buttonCancel.setOnClickListener{
            //if user does not want to upload a photo, save user to firestore with default profile photo
            saveUserToFirebaseDatabase("https://plexiglas-opmaat.nl/wp-content/uploads/2015/10/vector-person-icon.png")
        }

    }

    // startActivityForResult(intent,0)
    var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            Log.d("Set photo","photo was selected")

            //check which image was selected
             selectedPhotoUri = data.data

            setUserPhotoUri(data)


            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            selectPhotoCircle.setImageBitmap(bitmap)
            buttonSetPhoto.alpha = 0f //set button to be transparent to hide button, show photo selected

        }
    }

    private fun setUserPhotoUri(data: Intent) {
        if (selectedPhotoUri == null) {
            Toast.makeText(this, "Uri is null", Toast.LENGTH_LONG).show()
            //selectedPhotoUri = "https://plexiglas-opmaat.nl/wp-content/uploads/2015/10/vector-person-icon.png"

        } else {
            selectedPhotoUri = data.data
        }

//        UserManagement.setPhotoUri(selectedPhotoUri!!)
    }

    //upload image to firebase storage and save user info to firebase database
    private fun uploadImageToFirebaseStorage(){
        val filename = UUID.randomUUID().toString()
        val reference = FirebaseStorage.getInstance().getReference("/images/$filename")

//if user did not select a photo, save user to firestore with default profile photo
        if (selectedPhotoUri == null) {
            saveUserToFirebaseDatabase("https://plexiglas-opmaat.nl/wp-content/uploads/2015/10/vector-person-icon.png")
        }
        //get selected photo uri
        reference.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Log.d("SetPhotoStoreUser","Sucessfully uploaded image: ${it.metadata?.path}")

                    reference.downloadUrl.addOnSuccessListener {
                        Log.d("SetPhotoStoreUser", "File location: $it")

                        //save user info to firebase database
                        saveUserToFirebaseDatabase(it.toString())
                    }
                }
                .addOnFailureListener{
                    Log.d("SetPhotoStoreUser", "failed to upload image: ${it.message}")
                }


    }



    //save user info to firebase database
    private fun saveUserToFirebaseDatabase(profileImageUrl:String){

        val uid = FirebaseAuth.getInstance().uid?:""  //get uid from firebase

        val userDatabase = FirebaseFirestore.getInstance()

        val userName = intent.extras.getString("userName", "empty")
        val user = User(uid, userName, profileImageUrl)

        userDatabase.collection("users")
                .add(user)
                .addOnSuccessListener{
                    Log.d("SetPhotoStoreUser", "Successfully saved user to firebase database")

                    //user saved to firebase firestore, going to HomeScreenActivity

                    val intentHomeScreenActivity = Intent(this, HomeScreenActivity::class.java)
                    startActivity(intentHomeScreenActivity)
                    //clear activities on stack
                    //intentGroupChatActivity.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)

                }
                .addOnFailureListener{
                    Log.d("SetPhotoStoreUser", "Failed to save user to firebase database: ${it.message}")

                }

    }







}

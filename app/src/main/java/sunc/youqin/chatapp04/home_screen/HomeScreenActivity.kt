package sunc.youqin.chatapp04.home_screen

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import sunc.youqin.chatapp04.R
import sunc.youqin.chatapp04.chat_manager.*
import sunc.youqin.chatapp04.account.AccountInfo
import sunc.youqin.chatapp04.account.SignUp_SignIn_Activity

class HomeScreenActivity : AppCompatActivity() {

   // lateinit private var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

      //  SocketManager.createSocket("192.168.10.57",52066, this)


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

    }



//    private fun listenToIncomingMessagesFromChatServer() {
//        //Each time we are notified about a new message coming from chat server we add it to the adapter so that it becomes visible in the view
//        val listener = object : IncomingMessageListener {
//            override fun incomingMessage(text: String) {
//                //adapter.add(text) //add it to the adapter so that it is shown in the screen (ListView)
//            }
//        }
//        SocketManager.addMessageListener(listener)
//    }

//    override fun socketCreated() {
//        //Create a PrintWriter to give to the Task responsible to send a message to chatserver
//        //output = socketOutput
//
//        //Create an asynchronous task which will be reading messages from chatserver messages and update the adapter with the new messages it has received
//        //ReceiveMessagesFromServerAsyncTask(adapter).execute(socketInput)
//
//
//        // when the socket is ready, first check if user has registered, if not, go to SignUp_SignIn_Activity
//        checkIfUserHasSignedIn()
//
//        //add a message listener to be notify when the app is receiving a new message from chat server so that we can update the adapter with the new messages it has received
//        listenToIncomingMessagesFromChatServer()
//
//    }



//    fun checkIfUserHasSignedIn() {
//        val uid = FirebaseAuth.getInstance().uid
//
//        //if user has not registered/logged in, go to register/login activity
//        if (uid == null) {
//            val intentRegisterAndLoginActivity = Intent(this, SignUp_SignIn_Activity::class.java)
//
//            //intentRegisterAndLoginActivity.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//
//            startActivity(intentRegisterAndLoginActivity)
//
//            //if user has logged in/registered, send user name to server
//        }else{
//            val userName = UserManagement.getUsername()//intent.getStringExtra("userName")//host user name
//
//            FirebaseAuth.getInstance().currentUser
//
//            Log.d("Public Group Chat", if (userName == null) "USERNAME IS NOT SET" else userName )
//
//
////            val listener = object : SendMessageTaskListener {
////                override fun successfullySentMessage() {
////                }
////                override fun failedToSendMessage() {
////                }
////            }
//
//            SocketManager.send(":user $userName")
//
//            val intentPublicGroupChat = Intent(this, PublicGroupChat::class.java)
//            startActivity(intentPublicGroupChat)
//
//            //SendMessageToServerAsyncTask(output).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ":user $userName")
//        }



   // }





//bottom navigation bar on click listener
    val onNavigationItemSelectedListener = object : BottomNavigationView.OnNavigationItemSelectedListener {

        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.chatlogs_bottom_nav -> {
                    val fragmentChats = ChatsFragment()
                    supportFragmentManager.beginTransaction().add(R.id.fragment_holder_frameLayout, fragmentChats).commit()
                    return true
                }
                R.id.contacts_bottom_nav -> {
                    val fragmentContacts = ContactsFragment()
                    supportFragmentManager.beginTransaction().add(R.id.fragment_holder_frameLayout, fragmentContacts).commit()
                    return true
                }
                R.id.groups_bottom_nav -> {
                    val fragmentGroups = GroupsFragment()
                    supportFragmentManager.beginTransaction().add(
                            R.id.fragment_holder_frameLayout, fragmentGroups).commit()
                    return true
                }
            }
            return false
        }

    }


    //create menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation_drawer, menu)
        return super.onCreateOptionsMenu(menu)
    }
    //navigation menu
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menu_public_group ->{
                val intentPublicGroupChat = Intent(this, PublicGroupChat::class.java)
                startActivity(intentPublicGroupChat)
            }

            R.id.menu_new_message ->{  //new message menu: go to SelectContactForNewChat
                val intentSelectContactForNewChatActivity = Intent(this, SelectContactForNewChat::class.java)
                startActivity(intentSelectContactForNewChatActivity)

            }

            R.id.newGroup ->{
                 val intentSetGroupName = Intent(this, SetGroupName::class.java)
                startActivity(intentSetGroupName)

            }

            R.id.menu_sign_out ->{ //sign out menu: go to SignUp_SignIn_Activity, to register or log in
                FirebaseAuth.getInstance().signOut()
                val intentRegisterAndLoginActivity = Intent(this, SignUp_SignIn_Activity::class.java)

                intentRegisterAndLoginActivity.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)

                startActivity(intentRegisterAndLoginActivity)
            }

            R.id.account ->{
                val intentAccountInfo = Intent(this, AccountInfo::class.java)

                intentAccountInfo.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)

                startActivity(intentAccountInfo)
            }

        }
        return super.onOptionsItemSelected(item)
    }

}

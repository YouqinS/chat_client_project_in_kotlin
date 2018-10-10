package sunc.youqin.chatapp04.chat_manager

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_select_contact_for_new_chat.*
//import jdk.nashorn.internal.runtime.ECMAException.getException
//import android.support.test.orchestrator.junit.BundleJUnitUtils.getResult
import com.google.firebase.firestore.QuerySnapshot
import com.google.android.gms.tasks.Task
import android.util.Log
import android.widget.TextView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.user_layout_row.*
import kotlinx.android.synthetic.main.user_layout_row.view.*
import sunc.youqin.chatapp04.R
import sunc.youqin.chatapp04.network.Config
import sunc.youqin.chatapp04.network.OneTimeUseSocketManager
import sunc.youqin.chatapp04.network.SocketManager
import sunc.youqin.chatapp04.user.User
import sunc.youqin.chatapp04.user.UserItem


class SelectContactForNewChat : AppCompatActivity() {

    val addedUsers = mutableSetOf<String>()
    lateinit var userSelected:String

    companion object {
        //key to pass user name to another activity through intent extra
        val INTENT_EXTRA_KEY = "user name of user selected for new private chat"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_contact_for_new_chat)

        supportActionBar?.title = "select a contact"
        //get all users from firestore, display as a list, so user can select one to send message
        retrieveUsersFromFireStore()

    }


    //get all users from firestore, display as a list for user to select one for new chat
     fun retrieveUsersFromFireStore() {
        //use group adapter, groupie library
        val adapter = GroupAdapter<ViewHolder>()
        //get reference from firestore
        val reference = FirebaseFirestore.getInstance().collection("users")

        //wait until task has completed
        reference.get().addOnCompleteListener(object : OnCompleteListener<QuerySnapshot> {
            override fun onComplete(task: Task<QuerySnapshot>) {
                if (task.isSuccessful) {
                    //iterate through each item, convert to User object, pass to UserItem, add to group adapter
                    for (document in task.result) {
                        val user = document.toObject<User>(User::class.java)

                        val currentUser = FirebaseAuth.getInstance().currentUser
                        if (user.userName == currentUser?.displayName){
                            //do not add self from contact list when selecting user for private chat
                        }else{
                            userSelected = user.userName
                            addedUsers.add(user.userName)
                            adapter.add(UserItem(user))
                        }
                    }

                    //display user not signed in through firebase

                    //ask server to give all the users
                    val listener = createListener(adapter)
                    OneTimeUseSocketManager.sendMessage(":users", listener)
                    //todo stop listening after all users have been added


                    //select a user and go to PrivateChat to chat
                    adapter.setOnItemClickListener{item, view ->
                        val intentPrivateChat = Intent(view.context, PrivateChat::class.java)
                        // pass the selected user info to another activity through intent
                        val userItem = item as UserItem

                        intentPrivateChat.putExtra(INTENT_EXTRA_KEY,userItem.user)

                        startActivity(intentPrivateChat)
                    }

                    recyclerViewNewMessage.adapter = adapter

                    Log.d("SelectContactForNewChat", "added user to adapter")

                } else {
                    Log.d("SelectContactForNewChat", "failed adding user to adapter", task.exception)
                }
            }
        })

    }

    private fun createListener(adapter: GroupAdapter<ViewHolder>):IncomingMessageListener{

        val listener = object : IncomingMessageListener {

            override fun incomingMessage(text: String) {
                val currentUser = FirebaseAuth.getInstance().currentUser
                //do not add self from contact list when selecting user for private chat

                //if user does not have a photo url, set a default one

                val nonFirebaseUser = User("", text, "https://plexiglas-opmaat.nl/wp-content/uploads/2015/10/vector-person-icon.png")

                if (text == currentUser!!.displayName || text.contains("@") ||text.contains("from $userSelected") || text.startsWith(Config.FAKE_USERNAME_PREFIX)) {

                }else if (!addedUsers.contains(text)){
                    addedUsers.add(text)
                    adapter.add(UserItem(nonFirebaseUser)) //add it to the adapter so that it is shown in the screen

                    }

                }

            }

        return listener



    }




}

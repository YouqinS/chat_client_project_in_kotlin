package sunc.youqin.chatapp04.chat_manager

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_select_contacts_for_new_group.*
import sunc.youqin.chatapp04.R
import sunc.youqin.chatapp04.network.Config
import sunc.youqin.chatapp04.network.OneTimeUseSocketManager
import sunc.youqin.chatapp04.network.SocketManager
import sunc.youqin.chatapp04.user.User
import sunc.youqin.chatapp04.user.UserItem

class SelectContactsForNewGroup : AppCompatActivity() {
    val addedUsers = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_contacts_for_new_group)

        supportActionBar?.title = "select contacts"
        //get all users from firestore, display as a list, so user can select one to send message
        retrieveAndDisplayUsers()

        button_next_selectContactsForNewGroup.setOnClickListener{
            val groupName = intent.getStringExtra("GROUP NAME")

            val intentNewGroupChat = Intent(this, PrivateGroupChat::class.java)
            intentNewGroupChat.putExtra("GROUP NAME", groupName)

           // intentNewGroupChat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intentNewGroupChat)
        }
    }


    //get all users from firestore, display as a list for user to select one for new chat
    private fun retrieveAndDisplayUsers() {
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
                            addedUsers.add(user.userName)
                            adapter.add(UserItem(user))
                        }
                    }

                    //display user not signed in through firebase
                    addNonFirebaseUsers(adapter)

                    //ask server to give all the users

                    //select users as group members and go to PrivateGroupChat to chat
                    adapter.setOnItemClickListener{item, view ->
                        // send selected group members to server
                        val groupName = intent.getStringExtra("GROUP NAME")
                        val userItem = item as UserItem
                        val userName = userItem.user.userName
                        SocketManager.send(":addmember $groupName $userName")

                        println(":addmember $groupName $userName")
                    }

                    recyclerViewNewGroupChat.adapter = adapter

                    Log.d("SelectContactForNewChat", "added user to adapter")

                } else {
                    Log.d("SelectContactForNewChat", "failed adding user to adapter", task.exception)
                }
            }
        })

    }

    private fun addNonFirebaseUsers(adapter: GroupAdapter<ViewHolder>) {

        val listener = createListener(adapter)
        OneTimeUseSocketManager.sendMessage(":users", listener)
    }

    private fun createListener(adapter: GroupAdapter<ViewHolder>):IncomingMessageListener {
        val listener = object : IncomingMessageListener {
            override fun incomingMessage(text: String) {

                val currentUser = FirebaseAuth.getInstance().currentUser

                //if user does not have a photo url, set a default one

                val nonFirebaseUser = User("", text,"https://plexiglas-opmaat.nl/wp-content/uploads/2015/10/vector-person-icon.png")

                if ( text.contains("from @") || text == currentUser!!.displayName || text.startsWith(Config.FAKE_USERNAME_PREFIX)){
                    //do nothing if it is a message sent by server, or it is a message sent by self

                }else if (text.contains("registered as group member of") ){

                    Toast.makeText(this@SelectContactsForNewGroup, text, Toast.LENGTH_LONG).show()

                }else if (!addedUsers.contains(text)){
                    addedUsers.add(text)
                    adapter.add(UserItem(nonFirebaseUser)) //add it to the adapter so that it is shown in the screen

                }
            }
        }
        return listener
    }



}

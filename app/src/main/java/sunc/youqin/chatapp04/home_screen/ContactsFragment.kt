package sunc.youqin.chatapp04.home_screen


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_contacts.*
import sunc.youqin.chatapp04.R
import sunc.youqin.chatapp04.chat_manager.IncomingMessageListener
import sunc.youqin.chatapp04.chat_manager.PrivateChat
import sunc.youqin.chatapp04.chat_manager.SelectContactForNewChat
import sunc.youqin.chatapp04.network.Config
import sunc.youqin.chatapp04.network.OneTimeUseSocketManager
import sunc.youqin.chatapp04.network.SocketManager
import sunc.youqin.chatapp04.user.User
import sunc.youqin.chatapp04.user.UserItem


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ContactsFragment : Fragment() {
    val addedUsers = mutableSetOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)
        retrieveAndDisplayUsers()
        return view

    }


    private fun retrieveAndDisplayUsers() {
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

                    val listener = createListener(adapter)
                    OneTimeUseSocketManager.sendMessage(":users", listener)
                    //todo stop listening after all users have been added


                    //select a user and go to PrivateChat to chat
                    adapter.setOnItemClickListener{item, view ->
                        val intentPrivateChat = Intent(view.context, PrivateChat::class.java)
                        // pass the selected user info to another activity through intent
                        val userItem = item as UserItem
                        intentPrivateChat.putExtra(SelectContactForNewChat.INTENT_EXTRA_KEY,userItem.user)

                        startActivity(intentPrivateChat)
                    }

                    recyclerViewFragmentContacts.adapter = adapter

                    Log.d("FragmentContacts", "added user to adapter")

                } else {
                    Log.d("FragmentContacts", "failed adding user to adapter", task.exception)
                }
            }
        })

    }

    private fun listenForIncomingMessage(adapter: GroupAdapter<ViewHolder>) {
        val listener = createListener(adapter)
        SocketManager.addMessageListener(listener)
    }

    private fun  createListener(adapter: GroupAdapter<ViewHolder>):IncomingMessageListener
    {
        val listener = object : IncomingMessageListener {
            override fun incomingMessage(text: String) {
                val currentUser = FirebaseAuth.getInstance().currentUser

                val nonFirebaseUser = User("", text, "https://plexiglas-opmaat.nl/wp-content/uploads/2015/10/vector-person-icon.png")

                if (text == currentUser!!.displayName || text.contains("@") || text.startsWith(Config.FAKE_USERNAME_PREFIX)){


                }else if (!addedUsers.contains(text)){
                    addedUsers.add(text)
                    adapter.add(UserItem(nonFirebaseUser)) //add it to the adapter so that it is shown in the screen

                }
            }
        }
        return listener
    }




}

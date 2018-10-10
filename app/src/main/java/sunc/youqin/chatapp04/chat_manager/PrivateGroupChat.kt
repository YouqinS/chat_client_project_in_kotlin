package sunc.youqin.chatapp04.chat_manager

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_group_chat.*
import kotlinx.android.synthetic.main.activity_private_chat_log.*
import sunc.youqin.chatapp04.R
import sunc.youqin.chatapp04.network.SocketManager

class PrivateGroupChat : AppCompatActivity() {
   // lateinit private var adapter: ArrayAdapter<String>
    lateinit private var userMessage_EditText: EditText
    lateinit var adapter: GroupAdapter<ViewHolder>




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_group_chat)

        val groupName = intent.getStringExtra("GROUP NAME")

        supportActionBar?.title = "Group $groupName"

        adapter = GroupAdapter<ViewHolder>()


        listenToIncomingMessagesFromChatServer()

        recyclerViewNewGroupChat.adapter = adapter



        userMessage_EditText = findViewById(R.id.userMessageNewGroupChat)

        buttonSendNewGroupChat.setOnClickListener{
            Log.d("PrivateGroupChat", "Click button to send message")

            sendMessage()
        }
    }



    private fun sendMessage() {
        val message = userMessage_EditText.text.toString()
        val groupName = intent.getStringExtra("GROUP NAME")

        val currentUser = FirebaseAuth.getInstance().currentUser

        val outGoingMessage = OutGoingMessage(currentUser!!,message)

        adapter.add(OutgoingMessageItem(outGoingMessage))



        if (message.isNotBlank()){
            println(message)
            SocketManager.send("@$groupName " + message.trim())
            userMessage_EditText.text.clear()
        }

    }


    private fun listenToIncomingMessagesFromChatServer() {
        val listener = object : IncomingMessageListener {
            override fun incomingMessage(text: String) {
                val userNameFrom = text.substringAfterLast("from ")

                val currentUser = FirebaseAuth.getInstance().currentUser
                val groupName = intent.getStringExtra("GROUP NAME")


                if (userNameFrom == currentUser!!.displayName){
                    //do not show message sent by self
                }else if(text.contains("@$groupName")){  //add messages to this group pnly
                    val messageFrom = text.substringAfter(":").substringBeforeLast(" from")
                    val incomingMessage = IncomingMessage(userNameFrom, messageFrom)
                    adapter.add(IncomingMessageItem(incomingMessage))
                }
            }
        }
        SocketManager.addMessageListener(listener)
    }





}

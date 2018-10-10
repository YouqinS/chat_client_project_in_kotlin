package sunc.youqin.chatapp04.chat_manager

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_public_group_chat.*
import sunc.youqin.chatapp04.R
import sunc.youqin.chatapp04.network.SocketManager

class PublicGroupChat : AppCompatActivity() /*, SocketListener*/{

    lateinit private var userMessage_EditText: EditText
    lateinit var adapter: GroupAdapter<ViewHolder>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_public_group_chat)

        supportActionBar?.title = "Public Group with Everybody"


        listenToIncomingMessagesFromChatServer()


        userMessage_EditText = findViewById(R.id.user_message_edit_text_PublicGroupChat)
        adapter = GroupAdapter<ViewHolder>()



        //click button to send message to selected user
        button_send_groupchat.setOnClickListener{
            Log.d("ChatLog", "Click button to send message")
            sendMessage()
        }

        recyclerView_PublicGoupChat.adapter = adapter


    }

    private fun listenToIncomingMessagesFromChatServer() {
        //Each time we are notified about a new message coming from chat server we add it to the adapter so that it becomes visible in the view
        val listener = object : IncomingMessageListener {

            override fun incomingMessage(text: String) {

                val userNameFrom = text.substringAfterLast("from ")
                val messageFrom = text.substringBeforeLast(" from")
                val incomingMessage = IncomingMessage(userNameFrom, messageFrom)
                val currentUser = FirebaseAuth.getInstance().currentUser

                if (userNameFrom == currentUser!!.displayName){}else
                    adapter.add(IncomingMessageItem(incomingMessage))            }
        }
        SocketManager.addMessageListener(listener)
    }


    private fun sendMessage(){
        val message = userMessage_EditText.text.toString()
        if (message.isNotBlank()){

            val listener = object : SendMessageTaskListener {
                override fun successfullySentMessage() {
                    userMessage_EditText.text.clear()
                }

                override fun failedToSendMessage() {
                    Toast.makeText(this@PublicGroupChat, "failed to send message!",Toast.LENGTH_LONG).show()
                }
            }

            SocketManager.send(message.trim(), listener)

            val currentUser = FirebaseAuth.getInstance().currentUser
            val outGoingMessage = OutGoingMessage(currentUser!!, message)
            adapter.add(OutgoingMessageItem(outGoingMessage))


        }else{
            Toast.makeText(this@PublicGroupChat, "message is empty",Toast.LENGTH_LONG).show()
        }
    }


}


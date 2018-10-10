package sunc.youqin.chatapp04.home_screen


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import sunc.youqin.chatapp04.R
import sunc.youqin.chatapp04.chat_manager.IncomingMessageListener
import sunc.youqin.chatapp04.network.SocketManager


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ChatsFragment : Fragment() {
    lateinit var messages:MutableList<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_chats, container, false)

        val messagesView: ListView = view.findViewById(R.id.listView_FragmentChats)

         messages = mutableListOf("History messages:")

        val adapter = ArrayAdapter<String>(activity, R.layout.adapter_text_view, messages)

        messagesView.adapter = adapter

        listenForIncomingMessage(adapter)

        SocketManager.send(":messages")


        showChatHistory(view)

        // Inflate the layout for this fragment
        return view

    }


    private fun ChatsFragment.showChatHistory(view: View) {
        val messagesView: ListView = view.findViewById(R.id.listView_FragmentChats)

        val messages = mutableListOf("History messages:")

        val adapter = ArrayAdapter<String>(activity, R.layout.adapter_text_view, messages)

        messagesView.adapter = adapter

        listenForIncomingMessage(adapter)

        SocketManager.send(":messages")

    }


    private fun listenForIncomingMessage(adapter: ArrayAdapter<String>) {
        val listener = object : IncomingMessageListener {
            override fun incomingMessage(text: String) {
                //add it to the adapter so that it is shown in the screen (ListView)

                adapter.add(text)
            }
        }
        SocketManager.addMessageListener(listener)
    }


}

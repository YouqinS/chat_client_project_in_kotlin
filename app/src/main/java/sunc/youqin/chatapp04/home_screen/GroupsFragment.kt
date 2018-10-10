package sunc.youqin.chatapp04.home_screen


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import sunc.youqin.chatapp04.R
import sunc.youqin.chatapp04.chat_manager.IncomingMessageListener
import sunc.youqin.chatapp04.chat_manager.PrivateGroupChat
import sunc.youqin.chatapp04.chat_manager.PublicGroupChat
import sunc.youqin.chatapp04.network.SocketManager


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class GroupsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_groups, container, false)
        // Inflate the layout for this fragment

        val groupsListView: ListView = view.findViewById(R.id.listView_FragmentGroups)

        val messages = mutableListOf("Available Groups: ", "Public Group")

        val adapter = ArrayAdapter<String>(activity, R.layout.adapter_text_view, messages)

        groupsListView.adapter = adapter

        SocketManager.send(":groups")

        val listener = object : IncomingMessageListener {
            override fun incomingMessage(text: String) {
                adapter.add(text) //add it to the adapter so that it is shown in the screen (ListView)
            }
        }
        SocketManager.addMessageListener(listener)

        groupsListView.setOnItemClickListener{ adapterView: AdapterView<*>, view1: View, position: Int, l: Long ->

            // go to the group chat of the clicked group

            if (position == 1){
               goToPublicGroup()
           }else{
               val groupName = adapter.getItem(position).toString()
                val intentNewGroupChat = Intent(activity, PrivateGroupChat::class.java)
               intentNewGroupChat.putExtra("GROUP NAME", groupName)
                startActivity(intentNewGroupChat)
           }

        }


        return view
    }

    fun goToPublicGroup(){
        val intentPublicGroupChat = Intent(activity, PublicGroupChat::class.java)
        startActivity(intentPublicGroupChat)
    }


}

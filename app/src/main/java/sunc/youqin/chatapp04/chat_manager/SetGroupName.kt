package sunc.youqin.chatapp04.chat_manager

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_set_group_name.*
import sunc.youqin.chatapp04.R
import sunc.youqin.chatapp04.network.SocketManager

class SetGroupName : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_group_name)


        button_next_setGroupName.setOnClickListener{
            val groupName = editText_setGroupName.text.toString()

            // SEND GROUP NAME TO SERVER
            SocketManager.send(":create $groupName")
            println(":create $groupName")


            val intentSelectContactsForNewGroup = Intent(this, SelectContactsForNewGroup::class.java)

            intentSelectContactsForNewGroup.putExtra("GROUP NAME", groupName)

           // intentSelectContactsForNewGroup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intentSelectContactsForNewGroup)


        }

    }


}

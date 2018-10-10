package sunc.youqin.chatapp04.chat_manager

import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.incoming_message.view.*
import kotlinx.android.synthetic.main.user_layout_row.view.*
import sunc.youqin.chatapp04.R
import sunc.youqin.chatapp04.user.User

class IncomingMessageItem(val incomingMessage: IncomingMessage): Item<ViewHolder>()  {

    override fun getLayout(): Int {
        return R.layout.incoming_message
    }




    override fun bind(viewHolder: ViewHolder, position: Int) {
        //show message at the message text view

        viewHolder.itemView.incomingMessage.text = incomingMessage.message

       // Picasso.get().load(incomingMessage.user.profileUrl).into(viewHolder.itemView.userPhoto_userLayout)
        viewHolder.itemView.textView_userName_incomingMessage.text = incomingMessage.userName


    }




}

class IncomingMessage(val userName: String, val message:String)
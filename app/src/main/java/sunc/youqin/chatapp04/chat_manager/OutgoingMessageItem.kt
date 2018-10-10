package sunc.youqin.chatapp04.chat_manager

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.outgoing_message.view.*
import kotlinx.android.synthetic.main.user_layout_row.view.*
import sunc.youqin.chatapp04.R
import sunc.youqin.chatapp04.user.User

class OutgoingMessageItem(val outGoingMessage: OutGoingMessage): Item<ViewHolder>() {

override fun getLayout(): Int {
    return R.layout.outgoing_message
}

override fun bind(viewHolder: ViewHolder, position: Int) {
    //show message at the message text view

    viewHolder.itemView.outgoingMesssage.text = outGoingMessage.message
    //Picasso.get().load(outGoingMessage.user.displayName).into(viewHolder.itemView.outgoingMsgerPhoto)
    viewHolder.itemView.textView_userName_outgoingMessage.text = outGoingMessage.user.displayName


}


}

class OutGoingMessage(val user: FirebaseUser, val message:String)
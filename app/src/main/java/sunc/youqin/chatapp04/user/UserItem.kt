package sunc.youqin.chatapp04.user

import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.user_layout_row.view.*
import sunc.youqin.chatapp04.R

class UserItem(val user: User):Item<ViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.user_layout_row

    }


    override fun bind(viewHolder: ViewHolder, position: Int) {
        //called in list for each user object
        //set user name to text view
        viewHolder.itemView.userName_userLayout.text = user.userName
        //set user photo to image view, using picasso library
        Picasso.get().load(user.profileUrl).into(viewHolder.itemView.userPhoto_userLayout)

    }
}
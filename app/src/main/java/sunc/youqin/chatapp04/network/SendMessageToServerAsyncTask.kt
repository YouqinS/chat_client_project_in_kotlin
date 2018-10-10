package sunc.youqin.chatapp04.network

import android.os.AsyncTask
import sunc.youqin.chatapp04.chat_manager.EmptySendMsgListener
import sunc.youqin.chatapp04.chat_manager.SendMessageTaskListener
import java.io.PrintWriter
import java.lang.Exception

class SendMessageToServerAsyncTask(val output: PrintWriter, val listener: SendMessageTaskListener = EmptySendMsgListener()):AsyncTask<String,Unit,Boolean>(){

    /**
     * returns true if message is successfully sent, false otherwise
     */
    override fun doInBackground(vararg messsage: String?): Boolean {

        val userText = messsage.first()

        println("try to send message:$userText")

        try {
            output.println(userText)  //send message to server

        }catch (e: Exception){
            e.printStackTrace()
            return false
        }
        return true

    }

    //this runs in the UI thread, it is invoked after the background computation finished
    override fun onPostExecute(result: Boolean?) {
        if (result == true) listener.successfullySentMessage() else listener.failedToSendMessage()
    }
}
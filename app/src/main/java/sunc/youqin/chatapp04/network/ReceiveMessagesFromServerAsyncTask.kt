package sunc.youqin.chatapp04.network

import android.os.AsyncTask
import android.util.Log
import sunc.youqin.chatapp04.chat_manager.IncomingMessageListener
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

class ReceiveMessagesFromServerAsyncTask(val listener: IncomingMessageListener):AsyncTask<InputStream,String,Unit>() {

    override fun doInBackground(vararg inputStream: InputStream?) {

        val reader: Reader = InputStreamReader(inputStream.first()) //reader: read string, not byte
        val buffer  = BufferedReader(reader) //used to readline
        try{
            //keep reading when the socket is not closed
            while (true) {
                val line = buffer.readLine() ?: break

                println(line)

                publishProgress(line) //give message to the listener inside UI thread
            }
        } catch(ex: Exception) { //if there is one exception stop listening
            Log.d("AsyncTaskListening", ex.message)
        }
    }

    //executed in UI thread
    override fun onProgressUpdate(vararg messages: String?) {
        //a new message was received from the server , inform SocketManager so that he can inform all listeners
        val message = messages.first() //take only the first parameter
        if (message != null) {
            listener.incomingMessage(message) //give message to listener
        }
    }

    override fun onPostExecute(result: Unit?) {
        println("Stopping listening to server")
    }
}

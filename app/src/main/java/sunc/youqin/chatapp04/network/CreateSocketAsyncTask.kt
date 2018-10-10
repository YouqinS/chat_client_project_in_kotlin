package sunc.youqin.chatapp04.network

import android.os.AsyncTask
import android.util.Log
import java.net.Socket

//  CREATE SOCKET TO CHAT SERVER

class CreateSocketAsyncTask(val ipOrHostname: String, val port: Int, val listener:SocketListener): AsyncTask<Unit, Unit, Socket>() {

    //Override this method to perform a computation on a background thread.
    override fun doInBackground(vararg params: Unit?): Socket {
        Log.d("CreateSocketAsyncTask", "Trying to create socket")
        return Socket(ipOrHostname, port)  //socket created in background thread when task.execute is called

    }

    //this runs in the UI thread, it is invoked after the background computation finished
    override fun onPostExecute(result: Socket?) {
        if(result != null) {
            Log.d("CreateSocketAsyncTask", "Created socket successfully")
            listener.socketCreated(result)
        }else {
            Log.d("CreateSocketAsyncTask", "Failed to create socket")
        }
    }
}


//when socket is ready, listener is called and it gives the socket created
interface SocketListener {
    fun socketCreated(socket:Socket)
}

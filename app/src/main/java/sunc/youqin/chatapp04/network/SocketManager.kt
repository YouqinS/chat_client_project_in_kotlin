package sunc.youqin.chatapp04.network

import android.os.AsyncTask
import android.util.Log
import sunc.youqin.chatapp04.chat_manager.IncomingMessageListener
import sunc.youqin.chatapp04.chat_manager.SendMessageTaskListener
import java.io.PrintWriter
import java.net.Socket

//create socket, send message to server, receive message from server and notify listeners
object SocketManager : SocketListener {

    //will be set later
    lateinit private var socket: Socket
    lateinit private var output: PrintWriter
    lateinit private var socketCreatedListener: SocketListener
    private var messageListeners = mutableSetOf<IncomingMessageListener>()

    fun createSocket(host: String, port: Int, listener: SocketListener) {
        socketCreatedListener = listener
        //create socket in a background thread
        CreateSocketAsyncTask(host, port, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }


    //socket is ready, can be used for output, input
    override fun socketCreated(createdSocket: Socket) {
        socket = createdSocket

        //Create a PrintWriter to give to the Task responsible to send a message to chatserver
        output = PrintWriter(socket.getOutputStream(), true) //enable autoflushing: flushing will be done when println printf or write is called

        //Create an asynchronous task which will be reading messages from chatserver messages and update the adapter with the new messages it has received
        val listener = object : IncomingMessageListener {
            override fun incomingMessage(text: String) {
                notifyListenersAboutIncomingMessage(text)
            }
        }
        ReceiveMessagesFromServerAsyncTask(listener).execute(socket.getInputStream())

        //informs the listener that the socket is ready to use (created)
        socketCreatedListener.socketCreated(socket)
    }

    //to send username.. to server
    fun send(text:String) {
        if(output != null) {
            SendMessageToServerAsyncTask(output).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, text)
        }
    }

    //to send message, listener to tell if it was successfully sent
    fun send(text:String, listener: SendMessageTaskListener) {
        if(output != null) {
            SendMessageToServerAsyncTask(output, listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, text)
        }
    }

//////////////////////////////////////////////////////////////
//  OBSERVER PATTERN: to listen to messages received from chat server
//////////////////////////////////////////////////////////////

    fun addMessageListener(listener: IncomingMessageListener) {
        messageListeners.add(listener)
    }


    fun removeMessageListener(listenter: IncomingMessageListener) {
        messageListeners.remove(listenter)
    }


    //notify all listeners of the incoming message
    private fun notifyListenersAboutIncomingMessage(message:String) {
        for (listener in messageListeners) {
            try {
                listener.incomingMessage(message)
            }
            catch (e: Exception) {
              e.printStackTrace()
            }
        }
    }




}







package sunc.youqin.chatapp04.network
import android.os.AsyncTask
import android.os.Handler
import android.util.Log
import sunc.youqin.chatapp04.chat_manager.IncomingMessageListener
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.io.Reader
import java.lang.Exception
import java.net.Socket

object OneTimeUseSocketManager {
     //Send a message (which is a command and we expect an answer) to chat server (by creating a socket and closing it right after response is received)
    fun sendMessage( command:String,  callerListener:IncomingMessageListener) {
        val task = CreateSocketAsyncTask(command,callerListener)
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    /**
     * Send a message to chat server (by creating a socket and closing it right after response is received)
     *
     * 1) create a one time use socket
     * 2) set fake username "android-client_XXXXXXXX" for that socket
     * 3) send the system command to the chat server
     * 4) listen for messages and give all the messages coming after "User name set to..." to the caller via the callListener
     * 5) After 1s of listening close the socket (all the messages should have been received already)
     */
    class CreateSocketAsyncTask(val command:String, val callerListener:IncomingMessageListener) :  AsyncTask<Unit, String, Unit>() {
        var giveMessageToCaller = false
        var isTimerStarted = false
        lateinit var socket:Socket


        //Override this method to perform a computation on a background thread.
        override fun doInBackground(vararg params: Unit?) {
            socket = Socket(Config.CHAT_SERVER_IP, Config.CHAT_SERVER_PORT)
            //Create a PrintWriter for sending a message to chatserver
            val output = PrintWriter(socket.getOutputStream(), true) //enable autoflushing: flushing will be done when println printf or write is called
            sendToChatServerMessageSettingUsername(output)//call this first because otherwise it will not be runned (threadpool size not big enough?)
            sendCommand(command, output) //call this second otherwise it will not be runned (threadpool size not big enough?)
            readMessagesFromChatServer(output, socket, callerListener, command)
        }

        private fun readMessagesFromChatServer(output: PrintWriter, socket: Socket, callerListener: IncomingMessageListener, command: String) {
            val reader: Reader = InputStreamReader(socket.getInputStream())
            val buffer: BufferedReader = BufferedReader(reader)
            try {
                while (true) {
                    val line = buffer.readLine() ?: break
                    println(line)
                    publishProgress(line)
                }
            } catch (ex: Exception) { //if there is one exception stop listening
                Log.d("OneTimeUseSocketManager", ex.message)
            }
        }

        override fun onProgressUpdate(vararg messages: String?) {
            //a new message was received from the server
            val message = messages.first()
            if (message != null && message.isNotBlank()) {
                when {
                    giveMessageToCaller == true  -> giveIncomingMessageToCaller(message)
                    message.startsWith("User name set to ") -> giveMessageToCaller = true//From now incoming messages should be given to caller because they contain the answer to its command
                    else -> {} //ignore message because we only want the server response to the command we have sent
                }
            }
        }

        private fun giveIncomingMessageToCaller(text: String) {
            if (!isTimerStarted) {
                //https://stackoverflow.com/questions/43348623/how-to-call-a-function-after-delay-in-kotlin
                Handler().postDelayed({
                    Log.d("OneTimeUseSocketManager", "Closing socket")
                    socket.close()
                }, 1000)//close the socket automatically after 1s, this should give enough time for all messages to arrive
                //Timer("ClosingSocket", false).schedule( 500) { }
                isTimerStarted = true
            }
            callerListener.incomingMessage(text)
        }

        private fun sendToChatServerMessageSettingUsername(output: PrintWriter) {
            val username = Config.FAKE_USERNAME_PREFIX + System.currentTimeMillis()
            output.println( ":user " + username)
        }


        private fun sendCommand(command: String, output: PrintWriter) {
            output.println( command)
        }
    }
}




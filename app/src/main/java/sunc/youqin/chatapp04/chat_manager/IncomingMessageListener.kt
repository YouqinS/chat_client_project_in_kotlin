package sunc.youqin.chatapp04.chat_manager

//to be notified when a message is received from chat server
interface IncomingMessageListener {

    fun incomingMessage(text:String)
}
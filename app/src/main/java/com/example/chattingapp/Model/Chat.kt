package com.example.chattingapp.Model

data class Chat(var senderId:String="", var receiverId:String="", var message :String="")
{
    var messageID:String
        get()
        {
            return messageID
        }

        set(messageId)
        {
            this.messageID= messageId
        }
}
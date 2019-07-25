package com.kiven.sample.libs.chatkit

import com.kiven.sample.util.Const
import com.stfalcon.chatkit.commons.models.IDialog
import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser
import java.util.*

/**
 * Created by oukobayashi on 2019-07-25.
 */
class DefaultDailog : IDialog<DefaultMessage> {
    var lastMsg: DefaultMessage? = DefaultMessage("msgId004", Date(), DefaultUser("x33", "34k", ""), "i love u")
    var dialogId = "0"

    override fun getDialogPhoto(): String {
        return Const.randomImage()
    }

    override fun getUnreadCount(): Int {
        return (Math.random() * 100).toInt()
    }

    override fun setLastMessage(message: DefaultMessage?) {
        lastMsg = message
    }

    override fun getId(): String {
        return dialogId
    }

    override fun getUsers(): MutableList<out IUser> {
        return mutableListOf(
                DefaultUser("123", "Katter", Const.randomImage()),
                DefaultUser("143", "Katter", Const.randomImage())

        )
    }

    override fun getLastMessage(): DefaultMessage? {
        return lastMsg
    }

    override fun getDialogName(): String {
        return "群名"
    }

}

class DefaultMessage(private val msgId: String,
                     private val time: Date,
                     private val who: IUser,
                     private val msgInfo: String?) : IMessage {
    override fun getId(): String = msgId

    override fun getCreatedAt(): Date = time

    override fun getUser(): IUser = who

    override fun getText(): String? = msgInfo

}

class DefaultUser(private val userId: String,
                  private val userName: String,
                  private val header: String) : IUser {

    override fun getAvatar(): String = header

    override fun getName(): String = userName

    override fun getId(): String = userId
}
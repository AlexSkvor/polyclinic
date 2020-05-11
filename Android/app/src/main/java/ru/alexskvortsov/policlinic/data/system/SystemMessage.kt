package ru.alexskvortsov.policlinic.data.system

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

class SystemMessage {
    private val notifierRelay = PublishRelay.create<Message>()

    val notifier: Observable<Message> = notifierRelay.hide()
            .observeOn(AndroidSchedulers.mainThread())

    private fun Message.log(msg: String): Message =
            if (type is Type.Progress) this
            else this

    fun sendSystemMessage(message: Message) = notifierRelay.accept(message)

    fun send(message: String) = notifierRelay.accept(
        Message(
            message
        )
    )

    fun sendToast(message: String) = notifierRelay.accept(
        Message(
            message
        ).copy(type = Type.Toast))

    fun showProgress(visible: Boolean, message: String = "") = notifierRelay.accept(
        Message(
            message,
            visible,
            Type.Progress
        )
    )

    data class Message(
            val text: String,
            val progress: Boolean = false,
            val type: Type = Type.Snackbar
    )

    sealed class Type {
        object Toast : Type()
        object Snackbar : Type()
        object Progress : Type()
    }

}




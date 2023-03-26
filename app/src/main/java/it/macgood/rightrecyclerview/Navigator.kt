package it.macgood.rightrecyclerview

import it.macgood.rightrecyclerview.model.User

interface Navigator {

    fun showDetails(user: User)

    fun goBack()

    fun toast(messagesRes: Int)

}
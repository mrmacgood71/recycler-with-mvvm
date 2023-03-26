package it.macgood.rightrecyclerview

import android.app.Application
import it.macgood.rightrecyclerview.model.UsersService

class App : Application() {
    val usersService: UsersService = UsersService()
}
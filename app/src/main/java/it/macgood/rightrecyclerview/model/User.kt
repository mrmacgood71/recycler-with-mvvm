package it.macgood.rightrecyclerview.model

import android.media.MediaRouter.UserRouteInfo

data class User(
    var id: Long,
    var photo: String,
    var name: String,
    var company: String
)

data class UserDetails(
    val user: User,
    val details: String
)
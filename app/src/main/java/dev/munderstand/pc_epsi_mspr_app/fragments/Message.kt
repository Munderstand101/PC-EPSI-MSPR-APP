package dev.munderstand.pc_epsi_mspr_app.fragments

import java.util.*

class Message(
   val id: Int,
   val content: String,
   val target_id: Int,
   val username: String,
   val firstName: String,
   val lastName: String,
   val pictureUrl: String,
   val createdAt: Date
) {

}

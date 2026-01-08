package My.Nutrition.Model

import java.time.LocalDateTime

case class User(
                 id: Int,
                 fullName: String,
                 email: String,
                 password: String,
                 phoneNumber: String,
                 createdAt: LocalDateTime,
                 profileImagePath: String
               )
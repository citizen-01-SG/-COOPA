package My.Nutrition.Util

import scalikejdbc._
import My.Nutrition.Model.User
import java.time.LocalDateTime

object UserDAO:

  // 1. REGISTER: Insert with default image
  def register(fullName: String, email: String, pass: String, phone: String): Boolean =
    DB localTx { implicit session =>
      try
        sql"""
          INSERT INTO users (full_name, email, password, phone_number, profile_image_path, created_at)
          VALUES ($fullName, $email, $pass, $phone, 'default', CURRENT_TIMESTAMP)
        """.update.apply()
        true
      catch
        case e: Exception =>
          println(s"❌ Registration Failed: ${e.getMessage}")
          false
    }

  // 2. LOGIN: Retrieve the image path
  def checkLogin(emailInput: String, passwordInput: String): Option[User] =
    DB readOnly { implicit session =>
      sql"""
        SELECT * FROM users 
        WHERE email = $emailInput AND password = $passwordInput
      """
        .map(rs => User(
          id = rs.int("id"),
          fullName = rs.string("full_name"),
          email = rs.string("email"),
          password = rs.string("password"),
          phoneNumber = rs.string("phone_number"),
          createdAt = rs.localDateTime("created_at"),
          profileImagePath = rs.stringOpt("profile_image_path").getOrElse("default") // Handle nulls safely
        ))
        .single
        .apply()
    }

  // 3. CHECK EXISTS
  def isEmailTaken(emailInput: String): Boolean =
    DB readOnly { implicit session =>
      sql"SELECT count(*) FROM users WHERE email = $emailInput"
        .map(rs => rs.long(1)).single.apply().getOrElse(0L) > 0
    }

  // 4. UPDATE: Save new details + image path
  def update(user: User): Boolean =
    DB localTx { implicit session =>
      try
        sql"""
          UPDATE users 
          SET full_name = ${user.fullName}, 
              password = ${user.password}, 
              phone_number = ${user.phoneNumber},
              profile_image_path = ${user.profileImagePath}
          WHERE email = ${user.email}
        """.update.apply()
        true
      catch
        case e: Exception =>
          println(s"❌ Update Failed: ${e.getMessage}")
          false
    }
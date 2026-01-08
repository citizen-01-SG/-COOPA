package My.Nutrition.Util

import scalikejdbc._
import My.Nutrition.Model.User
import java.time.LocalDateTime

object UserDAO:

  // --- 1. REGISTER: Insert a new user ---
  // Returns true if successful, false if email exists or error occurs
  def register(fullName: String, email: String, pass: String, phone: String): Boolean =
    DB localTx { implicit session =>
      try
        sql"""
          INSERT INTO users (full_name, email, password, phone_number, created_at)
          VALUES ($fullName, $email, $pass, $phone, CURRENT_TIMESTAMP)
        """.update.apply()
        true
      catch
        case e: Exception =>
          println(s"❌ Registration Failed: ${e.getMessage}")
          false
    }

  // --- 2. LOGIN: Check if email/password match ---
  // Returns Some(User) if found, or None if invalid
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
          createdAt = rs.localDateTime("created_at")
        ))
        .single
        .apply()
    }

  // --- 3. CHECK EXISTS: Check if an email is already taken ---
  def isEmailTaken(emailInput: String): Boolean =
    DB readOnly { implicit session =>
      sql"SELECT count(*) FROM users WHERE email = $emailInput"
        .map(rs => rs.long(1))
        .single
        .apply()
        .getOrElse(0L) > 0
    }
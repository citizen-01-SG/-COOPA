package ch.makery.address.util
import scalikejdbc.*

import scala.language.postfixOps

trait Database :
  val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"
  val dbURL = "jdbc:derby:/Users/ordinarycitizen/Documents/practicaladdressproject-citizen-01-SG/myDB;"
  // initialize JDBC driver & connection pool
  Class.forName(derbyDriverClassname)
  ConnectionPool.singleton(dbURL, "me", "mine") //Auto create username and password

  //Implicit session for ScalikeJDBC queries
  implicit val session: AutoSession = AutoSession


object Database extends Database:
  def setupDB(): Unit =
    if !hasDBInitialize then
      println("Error: Database not found. Please run setup.sql in DataGrip.")
    else
      println("Success: Database connected to Nutrition App!")

  def hasDBInitialize: Boolean =
    // Checks if the "FOOD_ITEMS" table exists
    DB getTable "FOOD_ITEMS" match
      case Some(x) => true
      case None => false




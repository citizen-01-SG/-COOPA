package My.Nutrition.Util

import scalikejdbc._
import My.Nutrition.Model.Category
import scala.util.{Try, Success, Failure}

object CategoryDAO:

  // Initialize Table
  def setupTable(): Unit =
    DB autoCommit { implicit session =>
      Try {
        sql"""
          CREATE TABLE category (
            id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
            name VARCHAR(100) NOT NULL,
            PRIMARY KEY (id)
          )
        """.execute.apply()
      } match
        case Success(_) => println("✅ Table 'category' created.")
        case Failure(_) => println("ℹ️ Table 'category' already exists.")

      // Seed Data
      val count = sql"SELECT count(*) FROM category".map(rs => rs.int(1)).single.apply().getOrElse(0)

      if (count == 0) then
        println("🌱 Seeding default categories...")
        val defaults = List("Vegetables", "Fruits", "Meat & Poultry", "Seafood", "Grains", "Dairy", "Snacks", "Beverages")
        defaults.foreach { cat =>
          sql"INSERT INTO category (name) VALUES ($cat)".update.apply()
        }
    }

  // --- EXISTING METHOD ---
  def selectAll(): List[Category] =
    DB readOnly { implicit session =>
      sql"SELECT * FROM category".map(rs =>
        new Category(rs.int("id"), rs.string("name"))
      ).list.apply()
    }

  // --- NEW METHOD (Alias for Controller compatibility) ---
  def getAllCategories(): List[Category] = selectAll()

  // Get Name by ID
  def getName(categoryId: Int): String =
    val allCategories = selectAll()
    allCategories.find(_.id == categoryId).map(_.name).getOrElse("Unknown")
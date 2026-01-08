package My.Nutrition.Util

import scalikejdbc._
import My.Nutrition.Model.Category
import scala.util.{Try, Success, Failure} // Import Try for safe error handling

object CategoryDAO:

  // Initialize: Create Table and Insert Defaults if empty
  def setupTable(): Unit =
    DB autoCommit { implicit session =>
      // 1. Create Table (Wrapped in Try to handle "Table already exists" safely)
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

      // 2. Seed Data (Insert defaults if table is empty)
      // We check if the table is empty (count == 0)
      val count = sql"SELECT count(*) FROM category".map(rs => rs.int(1)).single.apply().getOrElse(0)

      if (count == 0) then
        println("🌱 Seeding default categories...")
        sql"INSERT INTO category (name) VALUES ('Vegetables')".update.apply()
        sql"INSERT INTO category (name) VALUES ('Fruits')".update.apply()
        sql"INSERT INTO category (name) VALUES ('Meat & Poultry')".update.apply()
        sql"INSERT INTO category (name) VALUES ('Seafood')".update.apply()
        sql"INSERT INTO category (name) VALUES ('Grains')".update.apply()
        sql"INSERT INTO category (name) VALUES ('Dairy')".update.apply()
        sql"INSERT INTO category (name) VALUES ('Snacks')".update.apply()
        sql"INSERT INTO category (name) VALUES ('Beverages')".update.apply()
    }

  // Fetch all categories for the UI Dropdown
  def selectAll(): List[Category] =
    DB readOnly { implicit session =>
      sql"SELECT * FROM category".map(rs =>
        new Category(rs.int("id"), rs.string("name"))
      ).list.apply()
    }

  def getName(categoryId: Int): String =
    val allCategories = selectAll()
    // Find the category with this ID, or return "Unknown" if not found
    allCategories.find(_.id == categoryId).map(_.name).getOrElse("Unknown")
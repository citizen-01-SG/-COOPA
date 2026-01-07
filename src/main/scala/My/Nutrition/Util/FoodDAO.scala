package My.Nutrition.Util

import My.Nutrition.Model.FoodItem
import scalikejdbc._

object FoodDAO extends BaseDAO[FoodItem]:

  // Method to get ALL food from the database
  def selectAll(): List[FoodItem] =
    DB readOnly { implicit session =>
      sql"SELECT * FROM food_item"
        .map(rs => new FoodItem(
          rs.int("food_id"),
          rs.string("food_name"),
          rs.double("serving_size"),
          rs.double("calories"),
          rs.double("protein"),
          rs.double("carbs"),
          rs.double("fat"),
          rs.double("sugar"),
          rs.double("salt"),
          rs.string("image_paths"),
          rs.int("category_id")
        )).list.apply()
    }

  // Method to Save a NEW food
  def insert(food: FoodItem): Unit =
    DB autoCommit { implicit session =>
      sql"""
        INSERT INTO food_item (food_name, serving_size, calories, protein, carbs, fat, sugar, salt, image_paths, category_id)
        VALUES (
          ${food.name.value},
          ${food.servingSizeProp.value},
          ${food.caloriesProp.value},
          ${food.proteinProp.value},
          ${food.carbsProp.value},
          ${food.fatProp.value},
          ${food.sugarProp.value},
          ${food.saltProp.value},
          ${food.imagePathProp.value},
          ${food.categoryIDProp.value}
        )
      """.update.apply()
    }

  // UPDATE an existing food
  def update(food: FoodItem): Unit =
    DB autoCommit { implicit session =>
      sql"""
        UPDATE food_item
        SET food_name = ${food.name.value},
            calories  = ${food.caloriesProp.value},
            protein   = ${food.proteinProp.value},
            carbs     = ${food.carbsProp.value},
            fat       = ${food.fatProp.value},
            sugar     = ${food.sugarProp.value},
            salt      = ${food.saltProp.value}
        WHERE food_id = ${food.id.value}
      """.update.apply()
    }

  // DELETE a food
  def delete(food: FoodItem): Unit =
    DB autoCommit { implicit session =>
      sql"DELETE FROM food_item WHERE food_id = ${food.id.value}".update.apply()
    }

    // UPDATE an existing food
    def update(food: FoodItem): Unit =
      DB autoCommit { implicit session =>
        sql"""
          UPDATE food_item
          SET food_name = ${food.name.value},
              calories  = ${food.caloriesProp.value},
              protein   = ${food.proteinProp.value},
              carbs     = ${food.carbsProp.value},
              fat       = ${food.fatProp.value},
              sugar     = ${food.sugarProp.value},
              salt      = ${food.saltProp.value}
          WHERE food_id = ${food.id.value}
        """.update.apply()
      }

    // DELETE a food
    def delete(food: FoodItem): Unit =
      DB autoCommit { implicit session =>
        sql"DELETE FROM food_item WHERE food_id = ${food.id.value}".update.apply()
      }

    def exists(name: String): Boolean =
      DB readOnly { implicit session =>
        sql"SELECT count(*) FROM food_item WHERE LOWER(food_name) = LOWER(${name})"
          .map(rs => rs.int(1))
          .single.apply()
          .getOrElse(0) > 0
      }

      // --- NEW: Check for duplicates ---
  def exists(name: String): Boolean =
    DB readOnly { implicit session =>
      sql"SELECT count(*) FROM food_item WHERE LOWER(food_name) = LOWER(${name})"
        .map(rs => rs.int(1))
        .single.apply()
        .getOrElse(0) > 0
    }
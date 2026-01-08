package My.Nutrition.Util

import My.Nutrition.Model.FoodItem
import scalikejdbc._

object FoodDAO extends BaseDAO[FoodItem]:

  // 1. SELECT ALL
  def selectAll(): List[FoodItem] =
    DB readOnly { implicit session =>
      sql"SELECT * FROM food_item"
        .map(rs => new FoodItem(
          rs.int("food_id"),
          rs.string("food_name"),
          0.0, // <--- DUMMY VALUE for servingSize (Fixes the mismatch)
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

  // 2. INSERT
  def insert(food: FoodItem): Unit =
    DB autoCommit { implicit session =>
      sql"""
        INSERT INTO food_item (
            food_name, calories, protein, carbs, fat, sugar, salt, image_paths, category_id
        ) VALUES (
          ${food.name.value},
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

  // 3. UPDATE
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
            salt      = ${food.saltProp.value},
            image_paths = ${food.imagePathProp.value},
            category_id = ${food.categoryIDProp.value}
        WHERE food_id = ${food.id.value}
      """.update.apply()
    }

  // 4. DELETE
  def delete(food: FoodItem): Unit =
    DB autoCommit { implicit session =>
      sql"DELETE FROM food_item WHERE food_id = ${food.id.value}".update.apply()
    }

  // 5. EXISTS CHECK
  def exists(name: String): Boolean =
    DB readOnly { implicit session =>
      sql"SELECT count(*) FROM food_item WHERE LOWER(food_name) = LOWER(${name})"
        .map(rs => rs.int(1))
        .single.apply()
        .getOrElse(0) > 0
    }
package My.Nutrition.Util

import My.Nutrition.Model.FoodItem
import scalikejdbc._

object FoodDAO {

  // Method to get ALL food from the database
  def selectAll(): List[FoodItem] = {
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
  }

  // Method to Save a NEW food (We will use this later for the 'Add' button)
  def insert(food: FoodItem): Unit = {
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
  }
}
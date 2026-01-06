package My.Nutrition.Util
import My.Nutrition.Model.FoodItem
import scalikejdbc._

object FoodDAO: 

  // Save Food Information (Create)
  def insert(f: FoodItem): Long =
    DB autoCommit { implicit session =>
      sql"""
        INSERT INTO food_item (
          food_name, serving_size, calories, protein, carbs, fat, sugar, salt, image_paths, category_id
        ) VALUES (
          ${f.name.value},
          ${f.servingSizeProp.value},
          ${f.caloriesProp.value.toDouble},
          ${f.proteinProp.value},
          ${f.carbsProp.value},
          ${f.fatProp.value},
          ${f.sugarProp.value},
          ${f.saltProp.value},
          ${f.imagePathProp.value},
          ${f.categoryIDProp.value}
        )
      """.updateAndReturnGeneratedKey.apply()
    }

  // Read (Find All) 
  // Maps Database Columns -> Your 'FoodItem' Constructor
  def findAll(): List[FoodItem] =
    DB readOnly { implicit session =>
      sql"SELECT * FROM food_item".map(rs => new FoodItem(
        foodID = rs.int("food_id"),
        foodName = rs.string("food_name"),
        servingSize = rs.double("serving_size"),
        calories = rs.double("calories"),
        protein = rs.double("protein"),
        carbs = rs.double("carbs"),
        fat = rs.double("fat"),
        sugar = rs.double("sugar"),
        salt = rs.double("salt"),
        // We use stringOpt just in case the database has a NULL value for image
        imagePaths = rs.stringOpt("image_paths").getOrElse(""),
        categoryID = rs.int("category_id")
      )).list.apply()
    }

  // Update (Modify)
  // Updates a specific row based on the Food ID
  def update(f: FoodItem): Unit =
    DB autoCommit { implicit session =>
      sql"""
        UPDATE food_item SET
          food_name = ${f.name.value},
          serving_size = ${f.servingSizeProp.value},
          calories = ${f.caloriesProp.value.toDouble},
          protein = ${f.proteinProp.value},
          carbs = ${f.carbsProp.value},
          fat = ${f.fatProp.value},
          sugar = ${f.sugarProp.value},
          salt = ${f.saltProp.value},
          image_paths = ${f.imagePathProp.value},
          category_id = ${f.categoryIDProp.value}
        WHERE food_id = ${f.id.value}
      """.update.apply()
    }

  // Delete (Remove)
  // Deletes a row based on ID
  def delete(foodId: Int): Unit =
    DB autoCommit { implicit session =>
      sql"DELETE FROM food_item WHERE food_id = $foodId".update.apply()
    }

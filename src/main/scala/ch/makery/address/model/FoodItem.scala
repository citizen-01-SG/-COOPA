package ch.makery.address.model
import scalikejdbc.*
import ch.makery.address.util.Database
import scala.beans.BeanProperty

// FoodItem Model
class FoodItem (
                 val foodID: Int,
                 var name: String,
                 var servingSize: Double,
                 var calories: Double,
                 var protein: Double,
                 var carbs: Double,
                 var fat: Double,
                 var sugar: Double,
                 var categoryID: Int
               ) extends Database:

  // Features a Smart Label Logic (Green vs Red)
  def healthLabel: String =
    if sugar > 20.0 || fat > 15.0 then "Red (Unhealthy)"
    else "Green (Healthy)"

object FoodItem extends Database:

  def getAllFoods: List[FoodItem] =
    DB readOnly { implicit session =>
      sql"SELECT * FROM food_items".map(rs => new FoodItem(
        rs.int("food_id"),
        rs.string("name"),
        rs.double("serving_size_g"),
        rs.double("calories"),
        rs.double("protein_g"),
        rs.double("carbs_g"),
        rs.double("fat_g"),
        rs.double("sugar_g"),
        rs.int("category_id")
      )).list.apply()
    }

package My.Nutrition.Model

import scalafx.beans.property.{StringProperty, IntegerProperty, DoubleProperty}

// Class Definition
class FoodItem(
                foodID: Int,
                foodName: String,
                servingSize: Double,
                calories: Double,
                protein: Double,
                carbs: Double,
                fat: Double,
                sugar: Double,
                salt: Double,
                imagePaths: String,
                categoryID: Int
              ):
  
  val id          = new IntegerProperty(this, "id", foodID)
  val name        = new StringProperty(this, "name", foodName)
  val servingSizeProp = new DoubleProperty(this, "servingSize", servingSize)
  val caloriesProp    = new IntegerProperty(this, "calories", calories.toInt)
  val proteinProp     = new DoubleProperty(this, "protein", protein)
  val carbsProp       = new DoubleProperty(this, "carbs", carbs)
  val fatProp         = new DoubleProperty(this, "fat", fat)
  val sugarProp       = new DoubleProperty(this, "sugar", sugar)
  val saltProp        = new DoubleProperty(this, "salt", salt)
  val imagePathProp   = new StringProperty(this, "imagePath", imagePaths)
  val categoryIDProp  = new IntegerProperty(this, "categoryID", categoryID)

  def healthLabel: String =
    if fatProp.value > 15.0 || sugarProp.value > 20.0 || saltProp.value > 1.5 then
      "Red (Unhealthy)"
    else
      "Green (Healthy)"
  

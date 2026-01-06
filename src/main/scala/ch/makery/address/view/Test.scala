package ch.makery.address.view
import ch.makery.address.util.Database
import ch.makery.address.model.FoodItem

object Test extends App:
  // Initialize Database 
  Database.setupDB()

  println("Fetch Food Data ")
  val foods = FoodItem.getAllFoods

  for food <- foods do 
    println(s"Name: ${food.name}, Calories: ${food.calories} (${food.healthLabel})")



    
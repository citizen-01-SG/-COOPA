package My.Nutrition.Util

import My.Nutrition.Model.FoodItem

object FoodAnalyzer:

  // --- TRAFFIC LIGHT LOGIC (Based on your Chart) ---

  // 1. RED LIGHTS (Unhealthy / High)
  // Fat > 17.5g | Sugar > 22.5g | Salt > 1.5g
  def getWarnings(food: FoodItem): List[String] =
    var warnings = List[String]()

    if (food.fatProp.value > 17.5)   warnings = warnings :+ f"High Fat (>17.5g) - RED"
    if (food.sugarProp.value > 22.5) warnings = warnings :+ f"High Sugar (>22.5g) - RED"
    if (food.saltProp.value > 1.5)   warnings = warnings :+ f"High Salt (>1.5g) - RED"

    // Extra safety check for calories
    if (food.caloriesProp.value > 600.0) warnings = warnings :+ "High Calorie (>600)"

    warnings

  // 2. GREEN LIGHTS (Healthy / Low)
  // Fat <= 3g | Sugar <= 5g | Salt <= 0.3g
  def getHighlights(food: FoodItem): List[String] =
    var highlights = List[String]()

    if (food.fatProp.value <= 3.0)   highlights = highlights :+ f"Low Fat (<3g) - GREEN"
    if (food.sugarProp.value <= 5.0) highlights = highlights :+ f"Low Sugar (<5g) - GREEN"
    if (food.saltProp.value <= 0.3)  highlights = highlights :+ f"Low Salt (<0.3g) - GREEN"

    // Extra check for protein (always good!)
    if (food.proteinProp.value > 15.0) highlights = highlights :+ "High Protein Source"

    highlights

  // 3. OVERALL GRADE
  // Calculate a grade based on how many RED flags exist
  def getGrade(food: FoodItem): String =
    val badCount = getWarnings(food).length
    val goodCount = getHighlights(food).length

    if (badCount >= 2) "C (Unhealthy)"       // Mostly Red
    else if (badCount == 1) "B (Moderate)"   // Some Red/Amber
    else if (goodCount >= 2) "A (Healthy)"   // Mostly Green
    else "B (Balanced)"                      // Mostly Amber (Middle)
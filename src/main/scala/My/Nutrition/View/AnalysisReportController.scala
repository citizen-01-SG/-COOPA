package My.Nutrition.View

import My.Nutrition.Main
import javafx.fxml.FXML
import javafx.scene.chart.{BarChart, CategoryAxis, XYChart}
import scalafx.collections.ObservableBuffer
import scalafx.Includes._

class AnalysisReportController:

  // --- THESE VARIABLES WERE MISSING ---
  @FXML private var barChart: BarChart[String, Number] = _
  @FXML private var xAxis: CategoryAxis = _

  @FXML
  private def initialize(): Unit =
    // --- SMART DATA FILTERING ---

    // 1. Sort data by Calories (Highest to Lowest) and take only the Top 10
    val topFoods = Main.foodData
      .sortBy(_.caloriesProp.value) // Sorts low -> high
      .reverse                      // Flips it to high -> low
      .take(10)                     // Keeps only the top 10

    // 2. Prepare the names for the X-Axis (Only for the top 10)
    val foodNames = topFoods.map(_.name.value)
    xAxis.setCategories(ObservableBuffer.from(foodNames))

    // 3. Create a Data Series
    val series = new XYChart.Series[String, Number]()
    series.setName("Calories")

    // 4. Add data (Only for the top 10)
    for (food <- topFoods) {
      series.getData.add(new XYChart.Data[String, Number](food.name.value, food.caloriesProp.value))
    }

    // 5. Add to chart
    barChart.getData.clear() // Safety clear
    barChart.getData.add(series)

  @FXML
  def handleBack(): Unit =
    Main.showDashboard()
    
    
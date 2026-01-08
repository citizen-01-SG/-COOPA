package My.Nutrition.View

import My.Nutrition.Main
import My.Nutrition.Model.FoodItem
import My.Nutrition.Util.FoodAnalyzer
import javafx.fxml.FXML
import javafx.scene.control.{ComboBox, Label, ListView}
import javafx.scene.image.{Image, ImageView}
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import java.io.File

class SmartLabelController:

  // --- FXML FIELDS ---
  @FXML private var foodSelector: ComboBox[FoodItem] = _
  @FXML private var foodImage: ImageView = _
  @FXML private var gradeLabel: Label = _
  @FXML private var goodList: ListView[String] = _
  @FXML private var badList: ListView[String] = _

  @FXML
  private def initialize(): Unit =
    // 1. Load all food items from Main data into the dropdown
    foodSelector.setItems(Main.foodData.delegate)

  // --- HANDLERS ---

  // When user picks a food from the dropdown
  @FXML
  private def handleScan(): Unit =
    val selected = foodSelector.getSelectionModel.getSelectedItem
    if (selected != null) then
      analyzeFood(selected)

  // Go back to Home
  @FXML
  def handleBack(): Unit =
    Main.showDashboard()

  // --- LOGIC ---
  private def analyzeFood(food: FoodItem): Unit =
    // 1. Set the Image
    val path = food.imagePathProp.value
    if (path != null && path.nonEmpty) then
      val file = new File(path)
      if (file.exists()) foodImage.setImage(new Image(file.toURI.toString))
      else foodImage.setImage(null)
    else
      foodImage.setImage(null)

    // 2. Call your FoodAnalyzer (Step 1)
    val goods = FoodAnalyzer.getHighlights(food)
    val bads  = FoodAnalyzer.getWarnings(food)
    val grade = FoodAnalyzer.getGrade(food)

    // 3. Update the Lists
    goodList.setItems(ObservableBuffer.from(goods))
    badList.setItems(ObservableBuffer.from(bads))

    // 4. Update the Grade Label & Color
    gradeLabel.setText(grade)

    if (grade.contains("A"))
      gradeLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 48px; -fx-font-weight: bold;") // Green
    else if (grade.contains("C"))
      gradeLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 48px; -fx-font-weight: bold;") // Red
    else
      gradeLabel.setStyle("-fx-text-fill: #f1c40f; -fx-font-size: 48px; -fx-font-weight: bold;") // Amber/Yellow
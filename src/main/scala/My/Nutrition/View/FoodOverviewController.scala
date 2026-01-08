package My.Nutrition.View

import My.Nutrition.Model.FoodItem
import My.Nutrition.Main
import javafx.fxml.FXML
import javafx.scene.control.{Label, TableColumn, TableView}
import javafx.scene.image.{Image, ImageView}
import scalafx.Includes._
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import My.Nutrition.Util.{FoodDAO, CategoryDAO} // Imported CategoryDAO
import java.io.File

class FoodOverviewController:

  // Private FXML Fields
  @FXML private var foodTable: TableView[FoodItem] = _
  @FXML private var foodNameColumn: TableColumn[FoodItem, String] = _
  @FXML private var caloriesColumn: TableColumn[FoodItem, java.lang.Number] = _

  @FXML private var nameLabel: Label = _
  @FXML private var caloriesLabel: Label = _
  @FXML private var proteinLabel: Label = _
  @FXML private var carbsLabel: Label = _
  @FXML private var fatLabel: Label = _
  @FXML private var sugarLabel: Label = _
  @FXML private var saltLabel: Label = _
  @FXML private var foodImageView: ImageView = _

  // NEW: Category Label
  @FXML private var categoryLabel: Label = _

  @FXML
  private def initialize(): Unit =
    foodNameColumn.setCellValueFactory(cellData => cellData.getValue.name.delegate)
    caloriesColumn.setCellValueFactory(cellData => cellData.getValue.caloriesProp.delegate)

    foodTable.setItems(Main.foodData.delegate)

    showFoodDetails(None)

    val sTable = new scalafx.scene.control.TableView(foodTable)
    sTable.selectionModel().selectedItem.onChange {
      (_, _, newValue) => showFoodDetails(Option(newValue))
    }

  // Private Helper
  private def showFoodDetails(food: Option[FoodItem]): Unit =
    food match
      case Some(f) =>
        nameLabel.setText(f.name.value)
        caloriesLabel.setText(f.caloriesProp.value.toString)
        proteinLabel.setText(f.proteinProp.value.toString + " g")
        carbsLabel.setText(f.carbsProp.value.toString + " g")
        fatLabel.setText(f.fatProp.value.toString + " g")
        sugarLabel.setText(f.sugarProp.value.toString + " g")
        saltLabel.setText(f.saltProp.value.toString + " g")

        // NEW: Get Category Name from Database
        val catName = CategoryDAO.getName(f.categoryIDProp.value)
        categoryLabel.setText(catName)

        // Image Loader
        val imagePath = f.imagePathProp.value
        if (imagePath != null && imagePath.nonEmpty) then
          val file = new File(imagePath)
          if (file.exists()) then
            val image = new Image(file.toURI.toString)
            foodImageView.setImage(image)
          else
            foodImageView.setImage(null)
        else
          foodImageView.setImage(null)

      case None =>
        nameLabel.setText("")
        caloriesLabel.setText("")
        proteinLabel.setText("")
        carbsLabel.setText("")
        fatLabel.setText("")
        sugarLabel.setText("")
        saltLabel.setText("")
        // Clear Category
        categoryLabel.setText("")
        foodImageView.setImage(null)


  // --- PUBLIC HANDLERS ---
  @FXML
  def handleNewFood(): Unit =
    val tempFood = new FoodItem()
    val okClicked = Main.showFoodEditDialog(tempFood)
    if (okClicked) then
      FoodDAO.insert(tempFood)
      Main.foodData += tempFood

  @FXML
  def handleEditFood(): Unit =
    val selectedFood = foodTable.getSelectionModel.getSelectedItem
    if (selectedFood != null) then
      val okClicked = Main.showFoodEditDialog(selectedFood)
      if (okClicked) then
        FoodDAO.update(selectedFood)
        showFoodDetails(Some(selectedFood))
    else
      val alert = new Alert(AlertType.Warning) {
        initOwner(Main.stage)
        title = "No Selection"
        headerText = "No Food Selected"
        contentText = "Please select a food in the table."
      }
      alert.showAndWait()

  @FXML
  def handleDeleteFood(): Unit =
    val selectedIndex = foodTable.getSelectionModel.getSelectedIndex
    val selectedFood = foodTable.getSelectionModel.getSelectedItem

    if (selectedIndex >= 0) then
      FoodDAO.delete(selectedFood)
      foodTable.getItems.remove(selectedIndex)
    else
      val alert = new Alert(AlertType.Warning) {
        initOwner(Main.stage)
        title = "No Selection"
        headerText = "No Food Selected"
        contentText = "Please select a food in the table."
      }
      alert.showAndWait()

  // --- NAVIGATION HANDLER ---
  @FXML
  def handleBack(): Unit =
    Main.showDashboard()
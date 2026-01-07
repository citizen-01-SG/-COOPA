package My.Nutrition.View

import My.Nutrition.Model.FoodItem
import My.Nutrition.Main
import javafx.fxml.FXML
import javafx.scene.control.{Label, TableColumn, TableView} // FIX: Using JavaFX types here!
import scalafx.Includes._
import My.Nutrition.Util.Database

class FoodOverviewController:

  // --- 1. Link to the Table (Using JavaFX Types) ---
  @FXML var foodTable: TableView[FoodItem] = _
  @FXML var foodNameColumn: TableColumn[FoodItem, String] = _
  @FXML var caloriesColumn: TableColumn[FoodItem, java.lang.Number] = _

  // --- 2. Link to the Labels (Using JavaFX Types) ---
  @FXML var nameLabel: Label = _
  @FXML var caloriesLabel: Label = _
  @FXML var proteinLabel: Label = _
  @FXML var carbsLabel: Label = _
  @FXML var fatLabel: Label = _
  @FXML var sugarLabel: Label = _
  @FXML var saltLabel: Label = _

  // Reference to the Main Application
  private var mainApp: Main.type = _

  @FXML
  def initialize(): Unit =
    // --- FIX: Using .delegate to talk to JavaFX columns ---

    // 1. Name Column
    foodNameColumn.setCellValueFactory(cellData =>
      cellData.getValue.name.delegate
    )

    // 2. Calories Column
    caloriesColumn.setCellValueFactory(cellData =>
      cellData.getValue.caloriesProp.delegate
    )

    // Clear details first
    showFoodDetails(None)

    // Listen for selection changes
    // We wrap the table in a Scala object just for this listener part
    val sTable = new scalafx.scene.control.TableView(foodTable)
    sTable.selectionModel().selectedItem.onChange {
      (_, _, newValue) => showFoodDetails(Option(newValue))
    }

  // --- 4. Helper to Display Data ---
  private def showFoodDetails(food: Option[FoodItem]): Unit =
    food match
      case Some(f) =>
        // FIX: Using .setText() (Standard Java method) to avoid type conflicts
        nameLabel.setText(f.name.value)

        caloriesLabel.setText(f.caloriesProp.value.toString)
        proteinLabel.setText(f.proteinProp.value.toString + " g")
        carbsLabel.setText(f.carbsProp.value.toString + " g")
        fatLabel.setText(f.fatProp.value.toString + " g")
        sugarLabel.setText(f.sugarProp.value.toString + " g")
        saltLabel.setText(f.saltProp.value.toString + " g")

      case None =>
        nameLabel.setText("")
        caloriesLabel.setText("")
        proteinLabel.setText("")
        carbsLabel.setText("")
        fatLabel.setText("")
        sugarLabel.setText("")
        saltLabel.setText("")

  // --- 5. Connect to Main ---
  def setMainApp(main: Main.type): Unit =
    mainApp = main
    // Pass the data directly to the JavaFX table
    foodTable.setItems(main.foodData.delegate)
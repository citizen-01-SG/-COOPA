package My.Nutrition.View
import My.Nutrition.Model.FoodItem
import My.Nutrition.Main
import scalafx.scene.control.{Label, TableColumn, TableView, Alert}
import scalafx.scene.control.Alert.AlertType
import scalafx.Includes._
import javafx.fxml.FXML
import scala.util.control.NonFatal

class FoodOverviewController:

  @FXML var foodTable: TableView[FoodItem] = _
  @FXML var foodNameColumn: TableColumn[FoodItem, String] = _
  @FXML var caloriesColumn: TableColumn[FoodItem, java.lang.Number] = _
  @FXML var nameLabel: Label = _
  @FXML var caloriesLabel: Label = _
  @FXML var proteinLabel: Label = _
  @FXML var carbsLabel: Label = _
  @FXML var fatLabel: Label = _
  @FXML var sugarLabel: Label = _
  @FXML var saltLabel: Label = _

  private var mainApp: Main.type = _

  @FXML
  def initialize(): Unit =
    // Initialize the Table Columns
    foodNameColumn.cellValueFactory = {
      _.value.name
    }
    caloriesColumn.cellValueFactory = {
      _.value.caloriesProp.delegate
    }

    // Clear details first
    showFoodDetails(None)

    // Listen for selection changes in the table
    // When user clicks a row -> showDetails() runs
    foodTable.selectionModel().selectedItem.onChange {
      (_, _, newValue) => showFoodDetails(Option(newValue))
    }

  private def showFoodDetails(food: Option[FoodItem]): Unit =
    food match
      case Some(f) =>
        // Fill the labels with data from the FoodItem object
        nameLabel.text <== f.name

        // String conversion
        caloriesLabel.text = f.caloriesProp.value.toString
        proteinLabel.text = f.proteinProp.value.toString + " g"
        carbsLabel.text = f.carbsProp.value.toString + " g"
        fatLabel.text = f.fatProp.value.toString + " g"
        sugarLabel.text = f.sugarProp.value.toString + " g"
        saltLabel.text = f.saltProp.value.toString + " g"

      case None =>
        // If nothing is selected, clear the text
        nameLabel.text = ""
        caloriesLabel.text = ""
        proteinLabel.text = ""
        carbsLabel.text = ""
        fatLabel.text = ""
        sugarLabel.text = ""
        saltLabel.text = ""

  def setMainApp(main: Main.type): Unit =
    mainApp = main
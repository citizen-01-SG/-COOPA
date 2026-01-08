package My.Nutrition.View

import My.Nutrition.Main
import javafx.fxml.FXML
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

class DashboardController:

  // 1. NUTRITION INFO BUTTON
  @FXML
  def handleGoToNutrition(): Unit =
    Main.showFoodOverview()

  // 2. SMART LABEL BUTTON (Active)
  @FXML
  def handleGoToSmartLabel(): Unit =
    Main.showSmartLabel()

  // 3. REPORT BUTTON (Still Coming Soon)
  @FXML
  def handleGoToReport(): Unit =
    val alert = new Alert(AlertType.Information) {
      initOwner(Main.stage)
      title = "Coming Soon"
      headerText = "Feature In Development"
      contentText = "Analysis Reports will be available in the next update!"
    }
    alert.showAndWait()
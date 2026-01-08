package My.Nutrition.View

import My.Nutrition.Main
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.shape.Circle
import javafx.scene.paint.{ImagePattern, Color}
import javafx.scene.image.Image
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import java.io.File

class DashboardController:

  // --- HEADER ELEMENTS ---
  @FXML var userNameLabel: Label = _
  @FXML var profileCircle: Circle = _

  // --- INITIALIZE ---
  @FXML
  def initialize(): Unit =
    Main.currentUser match
      case Some(user) =>
        userNameLabel.setText(user.fullName)
        val path = user.profileImagePath

        if path == "default" || path == null || path.isEmpty then
          profileCircle.setFill(Color.web("#1f1f1f"))
        else
          try
            val file = new File(path)
            if file.exists() then
              val image = new Image(file.toURI.toString)
              profileCircle.setFill(new ImagePattern(image))
            else
              profileCircle.setFill(Color.web("#1f1f1f"))
          catch
            case e: Exception => println(s"Failed to load dashboard image: ${e.getMessage}")

      case None =>
        userNameLabel.setText("Guest")

  // --- ACTIONS ---

  // 1. NUTRITION INFO
  @FXML
  def handleGoToNutrition(): Unit =
    Main.showFoodOverview()

  // 2. SMART LABEL
  @FXML
  def handleGoToSmartLabel(): Unit =
    Main.showSmartLabel()

  // 3. REPORT
  @FXML
  def handleGoToReport(): Unit =
    Main.showAnalysisReport()

  // 4. MY PROFILE
  @FXML
  def handleGoToProfile(): Unit =
    Main.showUserProfile()

  // 5. LOGOUT (NEW)
  @FXML
  def handleLogout(): Unit =
    // 1. Clear the current user session
    Main.currentUser = None
    println("User logged out.")

    // 2. Return to Login Screen
    Main.showLoginScreen()
package My.Nutrition.View

import My.Nutrition.Main
import My.Nutrition.Util.UserDAO
import javafx.fxml.FXML
import javafx.scene.control.{PasswordField, TextField}
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.Includes._

class LoginController:

  @FXML var emailField: TextField = _
  @FXML var passwordField: PasswordField = _

  @FXML
  def handleLogin(): Unit =
    val email = emailField.getText
    val pass = passwordField.getText

    if email.isEmpty || pass.isEmpty then
      showAlert("Error", "Please fill in all fields.")
    else
      val user = UserDAO.checkLogin(email, pass)
      user match
        case Some(u) =>
          println(s"Logged in as: ${u.fullName}")

          // CRITICAL: Set the session variable!
          Main.currentUser = Some(u)

          Main.showMainApp()

        case None =>
          showAlert("Login Failed", "Invalid email or password.")

  @FXML
  def handleGoToRegister(): Unit =
    Main.showRegisterScreen()

  private def showAlert(header: String, content: String): Unit =
    new Alert(AlertType.Error) {
      initOwner(Main.stage)
      title = "Login Error"
      headerText = header
      contentText = content
    }.showAndWait()
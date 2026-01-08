package My.Nutrition.View

import My.Nutrition.Main
import My.Nutrition.Util.UserDAO
import javafx.fxml.FXML
// Keep JavaFX for the input fields (needed for @FXML)
import javafx.scene.control.{PasswordField, TextField}
// Use SCALAFX for the Alert dialogs
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.Includes._

class RegisterController:

  @FXML var nameField: TextField = _
  @FXML var emailField: TextField = _
  @FXML var phoneField: TextField = _
  @FXML var passField: PasswordField = _

  @FXML
  def handleRegister(): Unit =
    if isInputValid() then
      val success = UserDAO.register(
        nameField.getText,
        emailField.getText,
        passField.getText,
        phoneField.getText
      )

      if success then
        new Alert(AlertType.Information) {
          initOwner(Main.stage)
          title = "Success"
          headerText = "Account Created"
          contentText = "You can now log in."
        }.showAndWait()

        Main.showLoginScreen() // Go back to login
      else
        new Alert(AlertType.Error) {
          initOwner(Main.stage)
          title = "Error"
          headerText = "Registration Failed"
          contentText = "Email might already exist."
        }.showAndWait()

  @FXML
  def handleBackToLogin(): Unit =
    Main.showLoginScreen()

  private def isInputValid(): Boolean =
    var errorMessage = ""

    if nameField.getText == null || nameField.getText.isEmpty then
      errorMessage += "No valid name!\n"
    if emailField.getText == null || emailField.getText.isEmpty then
      errorMessage += "No valid email!\n"
    if passField.getText == null || passField.getText.isEmpty then
      errorMessage += "No valid password!\n"

    if errorMessage.isEmpty then
      true
    else
      new Alert(AlertType.Error) {
        initOwner(Main.stage)
        title = "Invalid Fields"
        headerText = "Please correct invalid fields"
        contentText = errorMessage
      }.showAndWait()
      false
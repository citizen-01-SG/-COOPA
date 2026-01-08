package My.Nutrition.View

import My.Nutrition.Main
import My.Nutrition.Model.User
import My.Nutrition.Util.UserDAO
import javafx.fxml.FXML
import javafx.scene.control.{PasswordField, TextField, Button}
import javafx.scene.image.Image
import javafx.scene.shape.Circle
import javafx.scene.paint.{ImagePattern, Color}
import javafx.stage.FileChooser
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.Includes._

import java.io.File
import java.nio.file.{Files, Paths, StandardCopyOption}

class UserProfileController:

  @FXML var nameField: TextField = _
  @FXML var emailField: TextField = _
  @FXML var phoneField: TextField = _
  @FXML var passField: PasswordField = _
  @FXML var profileCircle: Circle = _

  var currentImagePath: String = "default"

  @FXML
  def initialize(): Unit =
    Main.currentUser match
      case Some(user) =>
        nameField.setText(user.fullName)
        emailField.setText(user.email)
        phoneField.setText(user.phoneNumber)
        passField.setText(user.password)
        currentImagePath = user.profileImagePath
        loadProfileImage(currentImagePath)
      case None =>
        println("Error: No user logged in!")

  @FXML
  def handleUploadPhoto(): Unit =
    val fileChooser = new FileChooser()
    fileChooser.setTitle("Select Profile Picture")
    fileChooser.getExtensionFilters.addAll(
      new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
    )

    val selectedFile = fileChooser.showOpenDialog(Main.stage.delegate)

    if selectedFile != null then
      try {
        val destFolder = Paths.get("user_images")
        if !Files.exists(destFolder) then Files.createDirectories(destFolder)

        val userEmail = emailField.getText.replaceAll("[^a-zA-Z0-9]", "")
        val newFileName = s"profile_${userEmail}_${System.currentTimeMillis()}.jpg"
        val destPath = destFolder.resolve(newFileName)

        Files.copy(selectedFile.toPath, destPath, StandardCopyOption.REPLACE_EXISTING)

        currentImagePath = destPath.toAbsolutePath.toString
        loadProfileImage(currentImagePath)
      } catch {
        case e: Exception => showAlert(AlertType.Error, "Upload Error", e.getMessage)
      }

  private def loadProfileImage(path: String): Unit =
    if path == "default" || path.isEmpty then
      profileCircle.setFill(Color.web("#1f1f1f"))
    else
      try
        val file = new File(path)
        if file.exists() then
          val image = new Image(file.toURI.toString)
          profileCircle.setFill(new ImagePattern(image))
      catch
        case e: Exception => println(s"Could not load image: ${e.getMessage}")

  @FXML
  def handleSave(): Unit =
    if isInputValid() then
      Main.currentUser match
        case Some(currentUser) =>
          val updatedUser = currentUser.copy(
            fullName = nameField.getText,
            phoneNumber = phoneField.getText,
            password = passField.getText,
            profileImagePath = currentImagePath
          )

          if UserDAO.update(updatedUser) then
            Main.currentUser = Some(updatedUser)

            // 1. Show Success Message
            showAlert(AlertType.Information, "Success", "Profile Updated Successfully!")

            // 2. NEW: Go back to Dashboard immediately
            Main.showDashboard()

          else
            showAlert(AlertType.Error, "Error", "Could not update profile in database.")

        case None => showAlert(AlertType.Error, "Error", "No user logged in.")

  @FXML
  def handleCancel(): Unit =
    Main.showDashboard()

  private def isInputValid(): Boolean =
    var errorMessage = ""
    if (nameField.getText == null || nameField.getText.isEmpty) errorMessage += "Invalid Name!\n"
    if (passField.getText == null || passField.getText.isEmpty) errorMessage += "Invalid Password!\n"

    if (errorMessage.isEmpty) true
    else
      showAlert(AlertType.Error, "Invalid Fields", errorMessage)
      false

  private def showAlert(alertType: AlertType, titleStr: String, content: String): Unit =
    new Alert(alertType) {
      initOwner(Main.stage)
      title = titleStr
      headerText = titleStr
      contentText = content
    }.showAndWait()
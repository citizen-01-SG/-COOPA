package My.Nutrition.View

import My.Nutrition.Model.FoodItem
import javafx.fxml.FXML
import javafx.scene.control.{Label, TextField, Alert}
import javafx.stage.FileChooser
import javafx.stage.Stage
import scalafx.Includes._
import java.io.File
import java.nio.file.{Files, StandardCopyOption}
import My.Nutrition.Util.FoodDAO

class FoodEditDialogController:

  // --- PRIVATE FXML FIELDS ---
  @FXML private var nameField: TextField = _
  @FXML private var caloriesField: TextField = _
  @FXML private var proteinField: TextField = _
  @FXML private var carbsField: TextField = _
  @FXML private var fatField: TextField = _
  @FXML private var sugarField: TextField = _
  @FXML private var saltField: TextField = _
  @FXML private var imagePathLabel: Label = _

  private var selectedImageFile: File = _

  // --- PUBLIC VARIABLES ---
  var dialogStage: Stage = _
  var food: FoodItem = _
  var okClicked: Boolean = false

  @FXML
  private def initialize(): Unit = {}

  // --- PUBLIC SETUP ---
  def setFood(f: FoodItem): Unit =
    this.food = f
    nameField.setText(f.name.value)

    // UPDATED: If value is 0, show empty string "". Otherwise show value.
    caloriesField.setText(numberToString(f.caloriesProp.value))
    proteinField.setText(numberToString(f.proteinProp.value))
    carbsField.setText(numberToString(f.carbsProp.value))
    fatField.setText(numberToString(f.fatProp.value))
    sugarField.setText(numberToString(f.sugarProp.value))
    saltField.setText(numberToString(f.saltProp.value))

    if (f.imagePathProp.value != null && f.imagePathProp.value.nonEmpty) then
      imagePathLabel.setText(f.imagePathProp.value)
    else
      imagePathLabel.setText("No image selected")


  // --- HANDLERS ---
  @FXML
  private def handleChooseImage(): Unit =
    val fileChooser = new FileChooser()
    fileChooser.setTitle("Select Food Image")
    fileChooser.getExtensionFilters.add(
      new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
    )

    val file = fileChooser.showOpenDialog(dialogStage)
    if (file != null) then
      selectedImageFile = file
      imagePathLabel.setText(file.getName)


  @FXML
  private def handleOk(): Unit =
    if (isInputValid()) {
      food.name.value         = nameField.getText

      // We parse the text. Since isInputValid passed, we know these are valid numbers!
      food.caloriesProp.value = caloriesField.getText.toDouble
      food.proteinProp.value  = proteinField.getText.toDouble
      food.carbsProp.value    = carbsField.getText.toDouble
      food.fatProp.value      = fatField.getText.toDouble
      food.sugarProp.value    = sugarField.getText.toDouble
      food.saltProp.value     = saltField.getText.toDouble

      if (selectedImageFile != null) then
        val destFolder = new File("images")
        if (!destFolder.exists()) destFolder.mkdir()
        val destFile = new File(destFolder, selectedImageFile.getName)
        Files.copy(selectedImageFile.toPath, destFile.toPath, StandardCopyOption.REPLACE_EXISTING)
        food.imagePathProp.value = "images/" + selectedImageFile.getName

      okClicked = true
      dialogStage.close()
    }


  @FXML
  private def handleCancel(): Unit =
    dialogStage.close()


  // --- PRIVATE HELPERS ---

  // NEW HELPER: Converts 0.0 to "" (Empty), otherwise returns string
  private def numberToString(value: Double): String =
    if (value == 0.0) "" else value.toString

  private def isInputValid(): Boolean =
    var errorMessage = ""

    // 1. Check Name
    if (nameField.getText == null || nameField.getText.trim.length() == 0)
      errorMessage += "No valid food name!\n"

    // 2. Check Duplicates (Only if name changed)
    if (food.name.value == "" || !food.name.value.equalsIgnoreCase(nameField.getText)) then
      if (FoodDAO.exists(nameField.getText)) then
        errorMessage += "Food name already exists! Please choose another.\n"

    // 3. Check Numbers (Must NOT be empty, must be valid Double)
    if (!isValidDouble(caloriesField.getText)) errorMessage += "Calories cannot be empty (enter 0 for none)!\n"
    if (!isValidDouble(proteinField.getText))  errorMessage += "Protein cannot be empty (enter 0 for none)!\n"
    if (!isValidDouble(carbsField.getText))    errorMessage += "Carbs cannot be empty (enter 0 for none)!\n"
    if (!isValidDouble(fatField.getText))      errorMessage += "Fat cannot be empty (enter 0 for none)!\n"
    if (!isValidDouble(sugarField.getText))    errorMessage += "Sugar cannot be empty (enter 0 for none)!\n"
    if (!isValidDouble(saltField.getText))     errorMessage += "Salt cannot be empty (enter 0 for none)!\n"

    if (errorMessage.length() == 0)
      return true
    else
      val alert = new Alert(Alert.AlertType.ERROR)
      alert.initOwner(dialogStage)
      alert.setTitle("Invalid Fields")
      alert.setHeaderText("Please correct invalid fields")
      alert.setContentText(errorMessage)
      alert.showAndWait()
      return false


  private def isValidDouble(str: String): Boolean =
    try {
      if (str == null || str.trim.isEmpty) return false // REJECT EMPTY
      str.toDouble // Try parsing
      true // Success
    } catch {
      case e: NumberFormatException => false // Failed
    }
package My.Nutrition.View

import My.Nutrition.Model.{FoodItem, Category}
import My.Nutrition.Util.{FoodDAO, CategoryDAO}
import javafx.fxml.FXML
import javafx.scene.control.{Label, TextField, Alert, ComboBox}
import javafx.stage.FileChooser
import javafx.stage.Stage
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import java.io.File
import java.nio.file.{Files, StandardCopyOption}

class FoodEditDialogController:

  // --- FXML FIELDS ---
  @FXML private var nameField: TextField = _
  @FXML private var caloriesField: TextField = _
  @FXML private var proteinField: TextField = _
  @FXML private var carbsField: TextField = _
  @FXML private var fatField: TextField = _
  @FXML private var sugarField: TextField = _
  @FXML private var saltField: TextField = _
  @FXML private var imagePathLabel: Label = _

  // NEW: The Dropdown Box (This is the critical fix!)
  @FXML private var categoryBox: ComboBox[Category] = _

  private var selectedImageFile: File = _

  // --- PUBLIC VARIABLES ---
  var dialogStage: Stage = _
  var food: FoodItem = _
  var okClicked: Boolean = false

  @FXML
  private def initialize(): Unit =
    // 1. Load Categories from Database
    val catList = CategoryDAO.selectAll()
    val observableList = ObservableBuffer.from(catList)

    // 2. Put them into the Dropdown
    categoryBox.setItems(observableList)

  // --- PUBLIC SETUP ---
  def setFood(f: FoodItem): Unit =
    this.food = f
    nameField.setText(f.name.value)

    // Helper to handle 0.0 vs Empty String
    caloriesField.setText(numberToString(f.caloriesProp.value))
    proteinField.setText(numberToString(f.proteinProp.value))
    carbsField.setText(numberToString(f.carbsProp.value))
    fatField.setText(numberToString(f.fatProp.value))
    sugarField.setText(numberToString(f.sugarProp.value))
    saltField.setText(numberToString(f.saltProp.value))

    // Set Image Label
    if (f.imagePathProp.value != null && f.imagePathProp.value.nonEmpty) then
      imagePathLabel.setText(f.imagePathProp.value)
    else
      imagePathLabel.setText("No image selected")

    // NEW: Select the correct Category in the dropdown
    // We look through the box items and find the one matching the food's ID
    if (categoryBox.getItems != null) then
      categoryBox.getItems.forEach { cat =>
        if (cat.id == f.categoryIDProp.value) then
          categoryBox.getSelectionModel.select(cat)
      }


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
      food.caloriesProp.value = caloriesField.getText.toDouble
      food.proteinProp.value  = proteinField.getText.toDouble
      food.carbsProp.value    = carbsField.getText.toDouble
      food.fatProp.value      = fatField.getText.toDouble
      food.sugarProp.value    = sugarField.getText.toDouble
      food.saltProp.value     = saltField.getText.toDouble

      // NEW: Save the selected Category
      val selectedCat = categoryBox.getSelectionModel.getSelectedItem
      if (selectedCat != null) then
        food.categoryIDProp.value = selectedCat.id

      // Image Logic
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
  private def numberToString(value: Double): String =
    if (value == 0.0) "" else value.toString

  private def isInputValid(): Boolean =
    var errorMessage = ""

    if (nameField.getText == null || nameField.getText.trim.length() == 0)
      errorMessage += "No valid food name!\n"

    // Check duplicates ONLY if name changed
    if (food.name.value == "" || !food.name.value.equalsIgnoreCase(nameField.getText)) then
      if (FoodDAO.exists(nameField.getText)) then
        errorMessage += "Food name already exists! Please choose another.\n"

    // Check Category Selection
    if (categoryBox.getSelectionModel.getSelectedItem == null) then
      errorMessage += "Please select a Category!\n"

    // Number Validation
    if (!isValidDouble(caloriesField.getText)) errorMessage += "Calories must be a number!\n"
    if (!isValidDouble(proteinField.getText))  errorMessage += "Protein must be a number!\n"
    if (!isValidDouble(carbsField.getText))    errorMessage += "Carbs must be a number!\n"
    if (!isValidDouble(fatField.getText))      errorMessage += "Fat must be a number!\n"
    if (!isValidDouble(sugarField.getText))    errorMessage += "Sugar must be a number!\n"
    if (!isValidDouble(saltField.getText))     errorMessage += "Salt must be a number!\n"

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
      if (str == null || str.trim.isEmpty) return false
      str.toDouble
      true
    } catch {
      case e: NumberFormatException => false
    }
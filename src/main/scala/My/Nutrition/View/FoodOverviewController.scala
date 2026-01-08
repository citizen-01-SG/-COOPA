package My.Nutrition.View

import My.Nutrition.Model.FoodItem
import My.Nutrition.Main
import javafx.fxml.FXML
import javafx.scene.control.{Label, TableColumn, TableView, TextField, ComboBox} // Added Inputs
import javafx.scene.image.{Image, ImageView}
import javafx.collections.transformation.{FilteredList, SortedList} // Added for Filtering
import scalafx.Includes._
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import My.Nutrition.Util.{FoodDAO, CategoryDAO}
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

  // New FXML Fields for Filtering
  @FXML private var categoryLabel: Label = _
  @FXML private var searchField: TextField = _
  @FXML private var categoryFilter: ComboBox[String] = _

  // The wrapper list for filtering
  private var filteredData: FilteredList[FoodItem] = _

  @FXML
  private def initialize(): Unit =
    foodNameColumn.setCellValueFactory(cellData => cellData.getValue.name.delegate)
    caloriesColumn.setCellValueFactory(cellData => cellData.getValue.caloriesProp.delegate)

    // 1. Wrap the Master Data in a FilteredList
    filteredData = new FilteredList(Main.foodData, _ => true)

    // 2. Wrap FilteredList in a SortedList (so we can still click column headers)
    val sortedData = new SortedList(filteredData)
    sortedData.comparatorProperty().bind(foodTable.comparatorProperty())

    // 3. Set Table to use this Sorted/Filtered data
    foodTable.setItems(sortedData)

    // 4. Setup Filters
    setupSearchFilter()
    setupCategoryFilter()

    showFoodDetails(None)

    val sTable = new scalafx.scene.control.TableView(foodTable)
    sTable.selectionModel().selectedItem.onChange {
      (_, _, newValue) => showFoodDetails(Option(newValue))
    }

  // --- FILTER LOGIC ---

  private def setupSearchFilter(): Unit =
    // Listen to text changes in search bar
    searchField.textProperty().addListener((_, _, _) => updateFilter())

  private def setupCategoryFilter(): Unit =
    // Load categories into ComboBox
    val categories = CategoryDAO.getAllCategories()
    categoryFilter.getItems.add("All Categories")
    categories.foreach(cat => categoryFilter.getItems.add(cat.categoryName))

    // Listen to selection changes
    categoryFilter.getSelectionModel.selectedItemProperty().addListener((_, _, _) => updateFilter())

  private def updateFilter(): Unit =
    val searchText = if searchField.getText == null then "" else searchField.getText.toLowerCase()
    val selectedCategory = categoryFilter.getSelectionModel.getSelectedItem

    filteredData.setPredicate { food =>
      // 1. Check Name Match
      val matchesName = if searchText.isEmpty then true else food.name.value.toLowerCase().contains(searchText)

      // 2. Check Category Match
      val matchesCategory =
        if selectedCategory == null || selectedCategory == "All Categories" then true
        else
          // Convert ID to Name to check match
          val catName = CategoryDAO.getName(food.categoryIDProp.value)
          catName == selectedCategory

      matchesName && matchesCategory
    }

  @FXML
  def handleClearFilter(): Unit =
    searchField.setText("")
    categoryFilter.getSelectionModel.selectFirst() // Select "All Categories" (or null)
    categoryFilter.getSelectionModel.clearSelection()


  // --- EXISTING HELPERS ---

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

        val catName = CategoryDAO.getName(f.categoryIDProp.value)
        categoryLabel.setText(catName)

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
        categoryLabel.setText("")
        foodImageView.setImage(null)


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
      // Note: We remove from Main.foodData, the FilteredList updates automatically
      Main.foodData -= selectedFood
    else
      val alert = new Alert(AlertType.Warning) {
        initOwner(Main.stage)
        title = "No Selection"
        headerText = "No Food Selected"
        contentText = "Please select a food in the table."
      }
      alert.showAndWait()

  @FXML
  def handleBack(): Unit =
    Main.showDashboard()
package My.Nutrition

import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes._
import javafx.fxml.FXMLLoader
import scalafx.collections.ObservableBuffer
import javafx.scene.Parent
import scalafx.stage.{Modality, Stage}

import My.Nutrition.Model.FoodItem
import My.Nutrition.Util.{Database, FoodDAO}
import My.Nutrition.View.{FoodEditDialogController, FoodOverviewController}

object Main extends JFXApp3:

  // Master Data List
  val foodData = new ObservableBuffer[FoodItem]()

  // Reference to the main window layout (Only exists AFTER login)
  var rootLayout: Option[javafx.scene.layout.BorderPane] = None

  // Theme Tracker
  var isDarkTheme = true

  override def start(): Unit =
    // 1. Setup Database
    Database.setupDatabase()

    // 2. Initialize the Stage
    stage = new PrimaryStage:
      title = "Nutrition App"

    // 3. Start with the Login Screen
    showLoginScreen()


  // --- AUTHENTICATION SCREENS ---

  def showLoginScreen(): Unit =
    val resource = getClass.getResource("View/Login.fxml")
    val loader = new FXMLLoader(resource)
    val root: Parent = loader.load()

    val loginScene = new Scene(root)
    loginScene.stylesheets.add(getClass.getResource("View/Style.css").toExternalForm)

    // Capture state BEFORE switching scene
    val wasMaximized = if (stage.scene() != null) stage.isMaximized else false

    stage.scene = loginScene

    // FIX: Force a "refresh" of the maximized state to prevent shrinking
    if wasMaximized then
      stage.maximized = false
      stage.maximized = true
    else
      if !stage.isShowing then stage.centerOnScreen()

    stage.show()

  def showRegisterScreen(): Unit =
    val resource = getClass.getResource("View/Register.fxml")
    val loader = new FXMLLoader(resource)
    val root: Parent = loader.load()

    val regScene = new Scene(root)
    regScene.stylesheets.add(getClass.getResource("View/Style.css").toExternalForm)

    // Capture state
    val wasMaximized = if (stage.scene() != null) stage.isMaximized else false

    stage.scene = regScene

    // FIX: Force a "refresh" of the maximized state
    if wasMaximized then
      stage.maximized = false
      stage.maximized = true

    stage.show()


  // --- MAIN APPLICATION ---

  def showMainApp(): Unit =
    // 1. Load Data
    foodData.clear()
    val dataFromDB = FoodDAO.selectAll()
    foodData ++= dataFromDB

    // 2. Load Root Layout
    val rootResource = getClass.getResource("View/RootLayout.fxml")
    if (rootResource == null) throw new RuntimeException("❌ Cannot find RootLayout.fxml!")

    val loader = new FXMLLoader(rootResource)
    val roots = loader.load().asInstanceOf[javafx.scene.layout.BorderPane]
    rootLayout = Some(roots)

    // 3. Create Main Scene
    val mainScene = new Scene(roots)

    // 4. Set Theme
    isDarkTheme = true
    mainScene.stylesheets.add(getClass.getResource("View/DarkTheme.css").toExternalForm)

    // Capture state
    val wasMaximized = if (stage.scene() != null) stage.isMaximized else false

    stage.scene = mainScene

    // FIX: Force a "refresh" of the maximized state
    if wasMaximized then
      stage.maximized = false
      stage.maximized = true
    else
      stage.centerOnScreen()

    stage.show()

    // 6. Load Content
    showFoodOverview()


  def showFoodOverview(): Unit =
    val resource = getClass.getResource("View/FoodOverview.fxml")
    val loader = new FXMLLoader(resource)
    val view = loader.load().asInstanceOf[javafx.scene.layout.AnchorPane]
    rootLayout.get.setCenter(view)


  def showFoodEditDialog(food: FoodItem): Boolean =
    val resource = getClass.getResource("View/FoodEditDialog.fxml")
    val loader = new FXMLLoader(resource)
    val page = loader.load().asInstanceOf[javafx.scene.layout.AnchorPane]

    val dialogStage = new Stage:
      title = "Edit Food"
      initModality(Modality.WindowModal)
      initOwner(stage)
      scene = new Scene(page)

    val controller = loader.getController[FoodEditDialogController]
    controller.dialogStage = dialogStage
    controller.setFood(food)

    dialogStage.showAndWait()

    controller.okClicked


  def toggleTheme(): Unit =
    if isDarkTheme then
      stage.getScene.getStylesheets.clear()
    else
      stage.getScene.getStylesheets.add(getClass.getResource("View/DarkTheme.css").toExternalForm)
    isDarkTheme = !isDarkTheme
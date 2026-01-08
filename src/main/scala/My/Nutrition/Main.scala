package My.Nutrition

import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes._
import javafx.fxml.FXMLLoader
import scalafx.collections.ObservableBuffer
import javafx.scene.Parent
import scalafx.stage.{Modality, Stage}

// Imports needed for the new features
import My.Nutrition.Model.{FoodItem, User}
import My.Nutrition.Util.{Database, FoodDAO}
import My.Nutrition.View.{FoodEditDialogController, FoodOverviewController}

object Main extends JFXApp3:

  // Master Data List
  val foodData = new ObservableBuffer[FoodItem]()

  // Reference to the main window layout
  var rootLayout: Option[javafx.scene.layout.BorderPane] = None

  // --- NEW: Session State (Required for Profile) ---
  var currentUser: Option[User] = None

  // Theme Tracker
  var isDarkTheme = true

  override def start(): Unit =
    // 1. Setup Database
    Database.setupDatabase()

    // 2. Initialize the Stage
    stage = new PrimaryStage:
      title = "Nutrition App"

    // 3. Start with Login (So we can load the User Profile later)
    showLoginScreen()


  // --- AUTHENTICATION (Needed for Profile) ---

  def showLoginScreen(): Unit =
    val resource = getClass.getResource("View/Login.fxml")
    val loader = new FXMLLoader(resource)
    val root: Parent = loader.load()

    val scene = new Scene(root)
    scene.stylesheets.add(getClass.getResource("View/Style.css").toExternalForm)

    // Window State logic
    val wasMaximized = if (stage.scene() != null) stage.isMaximized else false
    stage.scene = scene
    if (wasMaximized) { stage.maximized = false; stage.maximized = true }

    stage.show()

  def showRegisterScreen(): Unit =
    val resource = getClass.getResource("View/Register.fxml")
    val loader = new FXMLLoader(resource)
    val root: Parent = loader.load()

    val scene = new Scene(root)
    scene.stylesheets.add(getClass.getResource("View/Style.css").toExternalForm)

    val wasMaximized = if (stage.scene() != null) stage.isMaximized else false
    stage.scene = scene
    if (wasMaximized) { stage.maximized = false; stage.maximized = true }

    stage.show()


  // --- MAIN APP LOADER (Called after Login) ---
  // This contains the logic from your previous 'start()' method
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

    // 3. Create Scene
    val mainScene = new Scene(roots)
    mainScene.stylesheets.add(getClass.getResource("View/DarkTheme.css").toExternalForm)

    // 4. Show Window
    val wasMaximized = if (stage.scene() != null) stage.isMaximized else false
    stage.scene = mainScene
    if (wasMaximized) { stage.maximized = false; stage.maximized = true } else stage.centerOnScreen()

    stage.show()

    // 5. Show Default View
    showDashboard()


  // --- VIEWS ---

  def showDashboard(): Unit =
    loadView("View/Dashboard.fxml")

  def showSmartLabel(): Unit =
    loadView("View/SmartLabel.fxml")

  def showAnalysisReport(): Unit =
    loadView("View/AnalysisReport.fxml")

  def showFoodOverview(): Unit =
    loadView("View/FoodOverview.fxml")

  // --- PROFILE VIEW ---
  def showUserProfile(): Unit =
    loadView("View/UserProfile.fxml")


  // --- HELPER TO FIX CRASHES ---
  // This replaces the repeated code in your previous file.
  // Crucially, it uses 'Parent' instead of 'AnchorPane' so it doesn't crash on the Profile page.
  private def loadView(fxmlPath: String): Unit =
    val resource = getClass.getResource(fxmlPath)
    if (resource == null) println(s"❌ Cannot find $fxmlPath")
    else
      val loader = new FXMLLoader(resource)
      loader.load()
      val view = loader.getRoot[javafx.scene.Parent] // <--- THE FIX

      rootLayout match
        case Some(layout) => layout.setCenter(view)
        case None => println("Error: Root layout is null!")


  // --- DIALOGS ---

  def showFoodEditDialog(food: FoodItem): Boolean =
    val resource = getClass.getResource("View/FoodEditDialog.fxml")
    val loader = new FXMLLoader(resource)
    loader.load()
    val page = loader.getRoot[javafx.scene.layout.AnchorPane]

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


  // --- THEME ---
  def toggleTheme(): Unit =
    if isDarkTheme then
      stage.getScene.getStylesheets.clear()
    else
      stage.getScene.getStylesheets.add(getClass.getResource("View/DarkTheme.css").toExternalForm)
    isDarkTheme = !isDarkTheme
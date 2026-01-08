package My.Nutrition

import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes._
import javafx.fxml.FXMLLoader
import scalafx.collections.ObservableBuffer
import javafx.scene.Parent
import scalafx.stage.{Modality, Stage}

import My.Nutrition.Model.{FoodItem, User}
import My.Nutrition.Util.{Database, FoodDAO}
import My.Nutrition.View.{FoodEditDialogController, FoodOverviewController}

object Main extends JFXApp3:

  // Master Data List
  val foodData = new ObservableBuffer[FoodItem]()

  // Reference to the main window layout (Only exists AFTER login)
  var rootLayout: Option[javafx.scene.layout.BorderPane] = None

  // --- SESSION STATE ---
  var currentUser: Option[User] = None

  // Theme Tracker
  var isDarkTheme = true

  override def start(): Unit =
    Database.setupDatabase()

    stage = new PrimaryStage:
      title = "Nutrition App"

    // Start with Login
    showLoginScreen()


  // --- AUTHENTICATION SCREENS ---

  def showLoginScreen(): Unit =
    val resource = getClass.getResource("View/Login.fxml")
    val loader = new FXMLLoader(resource)
    val root: Parent = loader.load()

    val loginScene = new Scene(root)
    loginScene.stylesheets.add(getClass.getResource("View/Style.css").toExternalForm)

    val wasMaximized = if (stage.scene() != null) stage.isMaximized else false

    stage.scene = loginScene

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

    val wasMaximized = if (stage.scene() != null) stage.isMaximized else false

    stage.scene = regScene

    if wasMaximized then
      stage.maximized = false
      stage.maximized = true

    stage.show()


  // --- MAIN APPLICATION ---

  def showMainApp(): Unit =
    foodData.clear()
    val dataFromDB = FoodDAO.selectAll()
    foodData ++= dataFromDB

    val rootResource = getClass.getResource("View/RootLayout.fxml")
    if (rootResource == null) throw new RuntimeException("❌ Cannot find RootLayout.fxml!")

    val loader = new FXMLLoader(rootResource)
    val roots = loader.load().asInstanceOf[javafx.scene.layout.BorderPane]
    rootLayout = Some(roots)

    val mainScene = new Scene(roots)
    isDarkTheme = true
    mainScene.stylesheets.add(getClass.getResource("View/DarkTheme.css").toExternalForm)

    val wasMaximized = if (stage.scene() != null) stage.isMaximized else false

    stage.scene = mainScene

    if wasMaximized then
      stage.maximized = false
      stage.maximized = true
    else
      stage.centerOnScreen()

    stage.show()

    // Show Dashboard initially
    showDashboard()


  // --- VIEW NAVIGATION ---

  def showDashboard(): Unit =
    loadView("View/Dashboard.fxml")

  def showSmartLabel(): Unit =
    loadView("View/SmartLabel.fxml")

  def showFoodOverview(): Unit =
    loadView("View/FoodOverview.fxml")

  def showUserProfile(): Unit =
    loadView("View/UserProfile.fxml")


  // --- HELPER: CHANGED TO ACCEPT ANY PARENT ---
  private def loadView(fxmlPath: String): Unit =
    val resource = getClass.getResource(fxmlPath)
    if (resource == null) println(s"❌ Cannot find $fxmlPath")
    else
      val loader = new FXMLLoader(resource)
      loader.load()

      // FIXED: Changed from AnchorPane to Parent so it accepts StackPane too
      val view = loader.getRoot[javafx.scene.Parent]

      rootLayout match
        case Some(layout) => layout.setCenter(view)
        case None => println("Error: Root layout is null!")


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


  def toggleTheme(): Unit =
    if isDarkTheme then
      stage.getScene.getStylesheets.clear()
    else
      stage.getScene.getStylesheets.add(getClass.getResource("View/DarkTheme.css").toExternalForm)
    isDarkTheme = !isDarkTheme
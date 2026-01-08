package My.Nutrition

import scalafx.stage.{Modality, Stage}
import My.Nutrition.View.FoodEditDialogController
import My.Nutrition.Model.FoodItem
import My.Nutrition.Util.{Database, FoodDAO}
import My.Nutrition.View.FoodOverviewController
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes._
import javafx.fxml.FXMLLoader
import scalafx.collections.ObservableBuffer


object Main extends JFXApp3:

  // Master Data List
  val foodData = new ObservableBuffer[FoodItem]()

  // Reference to the main window layout
  var rootLayout: Option[javafx.scene.layout.BorderPane] = None

  // Theme Tracker
  var isDarkTheme = true

  override def start(): Unit =
    Database.setupDatabase()

    val dataFromDB = FoodDAO.selectAll()
    foodData ++= dataFromDB

    val rootResource = getClass.getResource("/My/Nutrition/View/RootLayout.fxml")
    if (rootResource == null) throw new RuntimeException("❌ Cannot find RootLayout.fxml!")

    val loader = new FXMLLoader(rootResource)
    loader.load()

    val roots = loader.getRoot[javafx.scene.layout.BorderPane]
    rootLayout = Some(roots)

    stage = new PrimaryStage:
      title = "Nutrition App"
      scene = new Scene(roots):
        // Initialize with Dark Theme
        stylesheets += getClass.getResource("View/DarkTheme.css").toExternalForm

    showDashboard()


  def showFoodOverview(): Unit =
    val resource = getClass.getResource("/My/Nutrition/View/FoodOverview.fxml")
    if (resource == null) throw new RuntimeException("❌ Cannot find FoodOverview.fxml!")

    val loader = new FXMLLoader(resource)
    loader.load()

    val view = loader.getRoot[javafx.scene.layout.AnchorPane]
    rootLayout.get.setCenter(view)


  def showFoodEditDialog(food: FoodItem): Boolean =
    val resource = getClass.getResource("/My/Nutrition/View/FoodEditDialog.fxml")
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

  // --- THEME TOGGLE LOGIC (Now visible!) ---
  def toggleTheme(): Unit =
    if isDarkTheme then
      // Remove Stylesheet (Light Mode)
      stage.getScene.getStylesheets.clear()
    else
      // Add Stylesheet (Dark Mode)
      stage.getScene.getStylesheets.add(getClass.getResource("View/DarkTheme.css").toExternalForm)

    isDarkTheme = !isDarkTheme

  // --- NEW DASHBOARD LOADER ---
  def showDashboard(): Unit =
    val resource = getClass.getResource("View/Dashboard.fxml")
    val loader = new FXMLLoader(resource)
    loader.load()
    val view = loader.getRoot[javafx.scene.layout.AnchorPane]

    // Set the Dashboard into the center
    // of the Root Layout
    this.rootLayout match
      case Some(layout) => layout.setCenter(view)
      case None => println("Error: Root layout is null!")

  // --- SHOW SMART LABEL SCANNER ---
  def showSmartLabel(): Unit =
    val resource = getClass.getResource("View/SmartLabel.fxml")
    val loader = new FXMLLoader(resource)
    loader.load()
    val view = loader.getRoot[javafx.scene.layout.AnchorPane]

    this.rootLayout match
      case Some(layout) => layout.setCenter(view)
      case None => println("Error: Root layout is null!")
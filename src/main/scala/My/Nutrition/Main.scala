package My.Nutrition

import My.Nutrition.Model.FoodItem
import My.Nutrition.Util.{Database, FoodDAO} // Import the DAO
import My.Nutrition.View.FoodOverviewController
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.BorderPane
import scalafx.Includes._
import javafx.fxml.FXMLLoader
import scalafx.collections.ObservableBuffer

object Main extends JFXApp3:

  // Master Data List
  val foodData = new ObservableBuffer[FoodItem]()

  // Reference to the main window layout
  var rootLayout: Option[javafx.scene.layout.BorderPane] = None

  override def start(): Unit =
    // Initialize DB
    Database.setupDatabase()

    // Load Data
    // Fetch Data from DB once Connection Successful
    val dataFromDB = FoodDAO.selectAll()
    foodData ++= dataFromDB

    // Load the Data Root Layout
    val rootResource = getClass.getResource("/My/Nutrition/View/RootLayout.fxml")
    if (rootResource == null) throw new RuntimeException("❌ Cannot find RootLayout.fxml!")

    val loader = new FXMLLoader(rootResource)
    loader.load()

    val roots = loader.getRoot[javafx.scene.layout.BorderPane]
    rootLayout = Some(roots)

    // Show Window
    stage = new PrimaryStage:
      title = "Nutrition App"
      scene = new Scene(roots)

    // Load Data to Inner Content
    showFoodOverview()


  def showFoodOverview(): Unit =
    val resource = getClass.getResource("/My/Nutrition/View/FoodOverview.fxml")
    if (resource == null) throw new RuntimeException("❌ Cannot find FoodOverview.fxml!")

    val loader = new FXMLLoader(resource)
    loader.load()

    val view = loader.getRoot[javafx.scene.layout.AnchorPane]

    rootLayout.get.setCenter(view)

    // Data Connection
    val controller = loader.getController[FoodOverviewController]
    controller.setMainApp(this)
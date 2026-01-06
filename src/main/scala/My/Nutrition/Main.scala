package My.Nutrition

import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes.*
// FIX: We import the Standard JavaFX Loader (No external library needed!)
import javafx.fxml.FXMLLoader
import javafx.{scene => jfxs}
import My.Nutrition.Util.Database
object Main extends JFXApp3:

  // Reference to the main window (RootLayout)
  var rootLayout: Option[jfxs.layout.BorderPane] = None

  override def start(): Unit =
    // Establish Database Connection
    Database.setupDatabase()

    // Load the Window Frame (RootLayout)
    val rootResource = getClass.getResource("/My/Nutrition/View/RootLayout.fxml")
    if (rootResource == null) throw new RuntimeException("❌ Cannot find RootLayout.fxml! Did you move the files to resources/My/Nutrition/View?")

    val loader = new FXMLLoader(rootResource)
    loader.load()

    val roots = loader.getRoot[jfxs.layout.BorderPane]
    rootLayout = Some(roots)

    // Set the Scene
    stage = new PrimaryStage:
      title = "Nutrition App"
      scene = new Scene(roots)

    // Load Content
    showFoodOverview()

  def showFoodOverview(): Unit =
    val resource = getClass.getResource("/My/Nutrition/View/FoodOverview.fxml")
    if (resource == null) throw new RuntimeException("❌ Cannot find FoodOverview.fxml!")

    val loader = new FXMLLoader(resource)
    loader.load()

    val view = loader.getRoot[jfxs.layout.AnchorPane]
    // Put the Food Panel into the center of the Root Layout
    rootLayout.get.setCenter(view)
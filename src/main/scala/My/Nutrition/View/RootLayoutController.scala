package My.Nutrition.View

import My.Nutrition.Main
import javafx.fxml.FXML

class RootLayoutController:

  @FXML
  def handleToggleTheme(): Unit =
    // Call the method we just wrote in Main
    Main.toggleTheme()

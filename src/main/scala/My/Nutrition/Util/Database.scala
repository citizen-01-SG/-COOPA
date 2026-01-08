package My.Nutrition.Util
import scalikejdbc._

object Database:

  // String of Connection Details
  private val dbUrl = "jdbc:derby:myDB;create=true"
  private val dbDriver = "org.apache.derby.jdbc.EmbeddedDriver"

  // Credentials
  private val dbUser = "me"
  private val dbPassword = "mine"

  def setupDatabase(): Unit =
    try {
      Class.forName(dbDriver)

      if !ConnectionPool.isInitialized() then
        ConnectionPool.singleton(dbUrl, dbUser, dbPassword)

      println(s"✅ Connected to Database as user '$dbUser'")

      // Attempt to create the table if not found
      initializeSchema()

    }catch
      case e: Exception => println(s"❌ DB Connection Error: ${e.getMessage}")
    CategoryDAO.setupTable()


  private def initializeSchema(): Unit =
    DB autoCommit { implicit session =>
      try
        // This creates the table inside the 'ME' schema automatically
        sql"""
            CREATE TABLE food_item (
              food_id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
              food_name VARCHAR(100) NOT NULL,
              serving_size DOUBLE DEFAULT 100.0,
              category_id INT,
              calories DOUBLE NOT NULL,
              protein DOUBLE DEFAULT 0.0,
              carbs DOUBLE DEFAULT 0.0,
              fat DOUBLE DEFAULT 0.0,
              sugar DOUBLE DEFAULT 0.0,
              salt DOUBLE DEFAULT 0.0,
              image_paths VARCHAR(255),
              PRIMARY KEY (food_id)
            )
          """.execute.apply()
        println("✅ New table 'food_item' created successfully!")
      catch
        case e: Exception =>
          if e.getMessage.contains("already exists") then
            println("ℹ️ Table 'food_item' found. Connected successfully.")
          else
            println(s"❌ Error checking schema: ${e.getMessage}")
    }






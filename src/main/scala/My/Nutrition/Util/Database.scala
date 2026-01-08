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

      // 1. Create tables if they don't exist
      initializeSchema()

      // 2. Patch existing tables (Add profile picture column if missing)
      updateSchema()

    } catch
      case e: Exception => println(s"❌ DB Connection Error: ${e.getMessage}")

    // Initialize other DAOs if needed
    CategoryDAO.setupTable()


  private def initializeSchema(): Unit =
    DB autoCommit { implicit session =>

      // --- 1. Create FOOD_ITEM Table ---
      try
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
            println("ℹ️ Table 'food_item' found.")
          else
            println(s"❌ Error checking schema (food_item): ${e.getMessage}")

      // --- 2. Create USERS Table ---
      try
        // Note: We include profile_image_path here for FRESH installs
        sql"""
            CREATE TABLE users (
              id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
              full_name VARCHAR(255) NOT NULL,
              email VARCHAR(255) NOT NULL UNIQUE,
              password VARCHAR(255) NOT NULL,
              phone_number VARCHAR(50) NOT NULL,
              profile_image_path VARCHAR(500) DEFAULT 'default',
              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
          """.execute.apply()
        println("✅ New table 'users' created successfully!")
      catch
        case e: Exception =>
          if e.getMessage.contains("already exists") then
            println("ℹ️ Table 'users' found.")
          else
            println(s"❌ Error checking schema (users): ${e.getMessage}")

      // --- 3. Create Index for Email (Faster Login) ---
      try
        sql"CREATE INDEX idx_email ON users(email)".execute.apply()
      catch
        case e: Exception =>
          // Ignore if index already exists
          if !e.getMessage.contains("already exists") then
            println(s"⚠️ Could not create index: ${e.getMessage}")
    }

  // --- NEW: Safe Migration Method ---
  // This runs every time the app starts. It tries to add the column.
  // If the column exists, Derby throws an error, which we catch and ignore.
  private def updateSchema(): Unit =
    DB autoCommit { implicit session =>
      try
        sql"ALTER TABLE users ADD COLUMN profile_image_path VARCHAR(500) DEFAULT 'default'".execute.apply()
        println("✅ MIGRATION SUCCESS: Added 'profile_image_path' to users table.")
      catch
        case e: Exception =>
          // Derby error code X0Y32 means "Column already exists"
          if e.getMessage.toLowerCase.contains("exists") || e.getMessage.contains("X0Y32") then
            // Do nothing, column is already there!
            print("")
          else
            println(s"ℹ️ Schema Update Check: ${e.getMessage}")
    }
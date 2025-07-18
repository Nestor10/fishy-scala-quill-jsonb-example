import io.getquill._
import io.getquill.jdbczio.Quill
import zio._
import zio.json._

// Fish characteristics model
case class FishCharacteristics(
  species: String,
  color: String,
  age: String,
  has_stripes: Boolean,
  personality: String,
  size: String,
  has_star: Option[Boolean] = None
)

// Fish entity with JSONB characteristics - using JsonbValue wrapper
case class Fish(
  id: Long,
  name: String,
  // ⚠️  CRITICAL: JsonbValue wrapper is ESSENTIAL for JSONB support! ⚠️
  // 🔥 This is the KEY to making Quill 4.x + Scala 3 + PostgreSQL JSONB work! 🔥
  // 💡 Without JsonbValue[T], JSONB columns will NOT work properly with Quill
  // 🎯 This wrapper tells Quill to treat this field as a JSONB database type
  characteristics: JsonbValue[FishCharacteristics]  // 🚀 THE MAGIC HAPPENS HERE! 🚀
)

// JSON encoders/decoders for our fish characteristics
given JsonEncoder[FishCharacteristics] = DeriveJsonEncoder.gen[FishCharacteristics]
given JsonDecoder[FishCharacteristics] = DeriveJsonDecoder.gen[FishCharacteristics]

// Fish service with database operations
case class FishService(quill: Quill.Postgres[Literal]):
  import quill.*
  
  // Fish queries
  inline def allFish = quote(query[Fish])
  inline def fishById(id: Long) = quote(query[Fish].filter(_.id == lift(id)))
  inline def fishByName(name: String) = quote(query[Fish].filter(_.name == lift(name)))
  
  // Insert a new fish
  inline def insertFish(fish: Fish) = quote {
    query[Fish].insertValue(lift(fish)).returningGenerated(_.id)
  }
  
  // Get all fish from the aquarium
  def getAllFish: ZIO[Any, Throwable, List[Fish]] =
    quill.run(allFish)
  
  // Find a fish by name
  def findFishByName(name: String): ZIO[Any, Throwable, Option[Fish]] =
    quill.run(fishByName(name)).map(_.headOption)
  
  // Add a new fish to the aquarium
  def addFish(name: String, characteristics: FishCharacteristics): ZIO[Any, Throwable, Long] =
    val fish = Fish(0L, name, JsonbValue(characteristics))
    quill.run(insertFish(fish))
  
  // Display fish information nicely
  def displayFish(fish: Fish): String =
    val chars = fish.characteristics.value
    s"""
       |🐟 ${fish.name} (#${fish.id})
       |   Species: ${chars.species}
       |   Color: ${chars.color}
       |   Age: ${chars.age}
       |   Size: ${chars.size}
       |   Personality: ${chars.personality}
       |   Has stripes: ${if chars.has_stripes then "Yes" else "No"}
       |   Has star: ${chars.has_star.map(if _ then "Yes" else "No").getOrElse("No")}
       |""".stripMargin

object FishService:
  def getAllFish: ZIO[FishService, Throwable, List[Fish]] =
    ZIO.serviceWithZIO[FishService](_.getAllFish)
  
  def findFishByName(name: String): ZIO[FishService, Throwable, Option[Fish]] =
    ZIO.serviceWithZIO[FishService](_.findFishByName(name))
  
  def addFish(name: String, characteristics: FishCharacteristics): ZIO[FishService, Throwable, Long] =
    ZIO.serviceWithZIO[FishService](_.addFish(name, characteristics))
  
  def displayFish(fish: Fish): URIO[FishService, String] =
    ZIO.serviceWith[FishService](_.displayFish(fish))

object Main extends ZIOAppDefault:
  def run: ZIO[Any, Throwable, Unit] =
    val program = for {
      _ <- Console.printLine("🐟 Welcome to the Fish Aquarium Database! 🐟")
      _ <- Console.printLine("=" * 50)
      
      // Get all fish
      _ <- Console.printLine("📋 Fish in the aquarium:")
      allFish <- FishService.getAllFish
      _ <- ZIO.foreach(allFish) { fish =>
        FishService.displayFish(fish).flatMap(info => Console.printLine(info))
      }
      
      // Add some new fish inspired by classic children's books
      _ <- Console.printLine("➕ Adding some more colorful fish...")
      
      // Add a new red fish (different from the existing one)
      newRedFishChars = FishCharacteristics(
        species = "cardinal_tetra",
        color = "red",
        age = "young", 
        has_stripes = true,
        personality = "active",
        size = "tiny",
        has_star = Some(false)
      )
      newRedFishId <- FishService.addFish("Little Red Fish", newRedFishChars)
      _ <- Console.printLine(s"✅ Added Little Red Fish with ID: $newRedFishId")
      
      // Add a new blue fish (different from the existing one)
      newBlueFishChars = FishCharacteristics(
        species = "blue_gourami",
        color = "blue",
        age = "adult",
        has_stripes = false,
        personality = "gentle",
        size = "medium",
        has_star = Some(true)
      )
      newBlueFishId <- FishService.addFish("Big Blue Fish", newBlueFishChars)
      _ <- Console.printLine(s"✅ Added Big Blue Fish with ID: $newBlueFishId")
      
      // Find the original red fish from our initial data
      _ <- Console.printLine("🔍 Looking for the original Red Fish...")
      redFish <- FishService.findFishByName("Red Fish")
      _ <- redFish match {
        case Some(fish) => 
          FishService.displayFish(fish).flatMap(info => 
            Console.printLine(s"Found the original Red Fish!$info")
          )
        case None => 
          Console.printLine("Original Red Fish not found 😢")
      }
      
      // Look for One Fish and Two Fish
      _ <- Console.printLine("🔍 Looking for One Fish and Two Fish...")
      oneFish <- FishService.findFishByName("One Fish")
      twoFish <- FishService.findFishByName("Two Fish")
      _ <- (oneFish, twoFish) match {
        case (Some(fish1), Some(fish2)) =>
          for {
            info1 <- FishService.displayFish(fish1)
            info2 <- FishService.displayFish(fish2)
            _ <- Console.printLine(s"Found them both!$info1$info2")
          } yield ()
        case _ =>
          Console.printLine("One Fish or Two Fish not found 😢")
      }
      
      _ <- Console.printLine("")
      _ <- Console.printLine("🎉 From there to here, from here to there, funny things are everywhere")
    } yield ()

    program.provide(
      ZLayer.fromFunction(FishService.apply),
      Quill.DataSource.fromPrefix("myDatabaseConfig"),
      Quill.Postgres.fromNamingStrategy(Literal)
    )
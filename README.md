# 🐟 Fish Aquarium Database - ZIO + Quill + PostgreSQL JSONB
[![CI](https://github.com/Nestor10/fishy-scala-quill-jsonb-example/workflows/CI/badge.svg)](https://github.com/Nestor10/fishy-scala-quill-jsonb-example/actions)
A fun, working example of how to use JSONB with Quill in Scala 3 and ZIO. This project demonstrates a complete fish aquarium database with type-safe JSONB operations using modern Scala.

## 🎯 What This Project Does

This example shows you how to:
- ✅ Build a complete Scala 3 + ZIO + Quill application
- ✅ Work with PostgreSQL JSONB columns using `JsonbValue[T]`
- ✅ Store and query complex fish characteristics as JSON
- ✅ Use type-safe JSON encoding/decoding with ZIO-JSON
- ✅ Handle environment variables with sbt-dotenv
- ✅ Manage PostgreSQL with Kubernetes pods via go-task

## 🐠 Fish Aquarium Features

Our demo aquarium includes:
- **Fish Management**: Add, query, and display fish with detailed characteristics
- **JSONB Storage**: Fishy characteristics (species, color, age, personality, etc.) stored as JSONB
- **Type Safety**: Automatic JSON serialization/deserialization with compile-time safety
- **Sample Data**: Pre-loaded with Nemo, Dory, Goldie, Shark, and Rainbow fish
- **Interactive Demo**: Add new fish, search by name, and display all aquarium residents

## 📋 Prerequisites

Before you start, make sure you have these installed on your system:

### Required Software
- **Java 21** (OpenJDK recommended)
- **sbt** (Scala Build Tool)
- **Podman** or **Docker** (for PostgreSQL container)
- **go-task** (task runner - **required for this project**)

> **💡 Tip**: We recommend using [SDKMan](https://sdkman.io/) to install Java and sbt as it makes version management much easier!

### Quick Installation (Ubuntu/Debian)
```bash
# Install SDKMan first
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Install Java 21 and sbt via SDKMan
sdk install java 21.0.4-tem
sdk install sbt

# Install Podman
sudo apt install podman

# Install go-task
sh -c "$(curl --location https://taskfile.dev/install.sh)" -- -d -b ~/.local/bin
```

### Quick Installation (Fedora/RHEL)
```bash
# Install SDKMan first
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Install Java 21 and sbt via SDKMan
sdk install java 21.0.4-tem
sdk install sbt

# Install Podman (usually pre-installed)
sudo dnf install podman

# Install go-task (available in Fedora repos!)
sudo dnf install go-task
```

### Quick Installation (macOS)
```bash
# Install SDKMan first
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Install Java 21 and sbt via SDKMan
sdk install java 21.0.4-tem
sdk install sbt

# Install Podman via Homebrew
brew install podman

# Install go-task via Homebrew
brew install go-task
```

### Verify Installation
```bash
# Check versions
java -version        # Should show Java 21
sbt -version         # Should show sbt version
podman --version     # Should show Podman version
go-task --version    # Should show go-task version

# Useful SDKMan commands
sdk list java        # List available Java versions
sdk list sbt         # List available sbt versions
sdk current          # Show current versions
sdk use java 21.0.4-tem  # Switch Java version for current session
sdk default java 21.0.4-tem  # Set default Java version
```

## 🚀 Getting Started

### ⚙️ Configuration Setup

This project uses a `.env` file for configuration. This makes it easy to customize ports and database settings for your local environment.

```bash
# Copy the example environment file
cp .env.example .env

# Edit .env to match your preferences
nano .env
```

**Important**: If port 5432 is already in use on your system, change `POSTGRES_PORT=5433` (or any other available port) in your `.env` file.

### 🏃‍♂️ Quick Start (All-in-One)
```bash
# Clone the repo
git clone git@github.com:Nestor10/fishy-scala-quill-jsonb-example.git
cd fishy-scala-quill-jsonb-example

# Copy and customize environment file
cp .env.example .env
# Edit .env if needed (especially POSTGRES_PORT if 5432 is taken)

# Full setup in one command - starts PostgreSQL pod with demo data
go-task up

# Run the fish aquarium demo
go-task run
```

**Expected Output:**
```
🐟 Welcome to the Fish Aquarium Database! 🐟
==================================================
📋 Fish in the aquarium:

🐟 One Fish (#1)
   Species: goldfish
   Color: red
   Age: adult
   Size: small
   Personality: cheerful
   Has stripes: No
   Has star: No


🐟 Two Fish (#2)
   Species: goldfish
   Color: blue
   Age: adult
   Size: small
   Personality: calm
   Has stripes: No
   Has star: No


🐟 Red Fish (#3)
   Species: goldfish
   Color: red
   Age: young
   Size: medium
   Personality: energetic
   Has stripes: No
   Has star: No


🐟 Blue Fish (#4)
   Species: tang
   Color: blue
   Age: young
   Size: medium
   Personality: peaceful
   Has stripes: No
   Has star: No


🐟 Big Fish (#5)
   Species: grouper
   Color: grey
   Age: old
   Size: large
   Personality: wise
   Has stripes: No
   Has star: No


🐟 Little Fish (#6)
   Species: neon_tetra
   Color: silver
   Age: young
   Size: tiny
   Personality: playful
   Has stripes: Yes
   Has star: No

➕ Adding some more colorful fish...
✅ Added Little Red Fish with ID: 7
✅ Added Big Blue Fish with ID: 8
🔍 Looking for the original Red Fish...
Found the original Red Fish!
🐟 Red Fish (#3)
   Species: goldfish
   Color: red
   Age: young
   Size: medium
   Personality: energetic
   Has stripes: No
   Has star: No

🔍 Looking for One Fish and Two Fish...
Found them both!
🐟 One Fish (#1)
   Species: goldfish
   Color: red
   Age: adult
   Size: small
   Personality: cheerful
   Has stripes: No
   Has star: No

🐟 Two Fish (#2)
   Species: goldfish
   Color: blue
   Age: adult
   Size: small
   Personality: calm
   Has stripes: No
   Has star: No


🎉 From there to here, from here to there, funny things are everywhere!
```

### 📋 Step-by-Step Setup

### 1. Clone and Setup
```bash
git clone git@github.com:Nestor10/fishy-scala-quill-jsonb-example.git
cd fishy-scala-quill-jsonb-example

# Copy and customize environment file
cp .env.example .env
# Edit .env if needed (especially POSTGRES_PORT if 5432 is taken)
```

### 2. Start PostgreSQL with Demo Data

PostgreSQL runs as a Kubernetes pod with schema auto-loaded from a ConfigMap:

```bash
# Start PostgreSQL pod - schema loads automatically
go-task start
```

### 3. Run the Application
```bash
# Compile and run
go-task run

# Or step by step
go-task compile
go-task run
```

## 📁 Project Structure

```
fishy-scala-quill-jsonb-example/
├── build.sbt                 # Project dependencies and configuration
├── Taskfile.yml             # Task definitions for easy setup
├── .env                     # Environment configuration (create from .env.example)
├── .env.example             # Example environment configuration
├── .gitignore               # Git ignore patterns
├── .jvmopts                 # JVM options for Java 21 module access
├── .scalafmt.conf           # Scalafmt code formatting configuration
├── .github/workflows/       # GitHub Actions CI/CD workflows
│   ├── ci.yml               # Continuous integration (build, test, format check)
│   └── dependencies.yml     # Dependency submission for security insights
├── k8s/
│   └── postgres-pod.yaml    # Kubernetes/Podman pod definition
├── project/
│   ├── build.properties     # SBT version
│   └── plugins.sbt          # SBT plugins (dotenv, scalafmt)
├── src/main/
│   ├── scala/
│   │   └── Main.scala       # Main application with JSONB examples
│   └── resources/
│       └── application.conf # Database configuration
├── src/test/scala/
│   └── MySuite.scala        # Test suite
└── README.md                # This file
```

## 🔧 Key Dependencies

This project uses these major dependencies:

```scala
libraryDependencies ++= Seq(
  "io.getquill" %% "quill-jdbc-zio" % "4.8.3",    // Quill with ZIO integration
  "dev.zio" %% "zio-json" % "0.6.2",               // JSON encoding/decoding
  "org.postgresql" % "postgresql" % "42.7.7",      // PostgreSQL driver
  "dev.zio" %% "zio" % "2.1.19"                    // ZIO runtime
)
```

**Additional Setup:**
- **sbt-dotenv**: Automatic `.env` file loading for environment variables
- **JsonbValue wrapper**: Type-safe JSONB operations with Quill
- **scalafmt**: Code formatting with Scala 3 optimized settings
- **GitHub Actions**: CI/CD workflows for testing and dependency monitoring

## 🎯 Example Usage

The fish aquarium application demonstrates:

```scala
// Define your data model with JSONB
case class FishCharacteristics(
  species: String,
  color: String,
  age: String,
  has_stripes: Boolean,
  personality: String,
  size: String,
  has_star: Option[Boolean] = None
)

// Entity with JSONB characteristics - note the JsonbValue wrapper
case class Fish(
  id: Long,
  name: String,
  characteristics: JsonbValue[FishCharacteristics]
)

// Insert JSONB data
val newFish = Fish(0L, "Bubbles", JsonbValue(FishCharacteristics(
  species = "betta",
  color = "purple",
  age = "young",
  has_stripes = false,
  personality = "curious",
  size = "small"
)))
quill.run(insertFish(newFish))

// Query JSONB data
val allFish = quill.run(query[Fish])
val nemo = quill.run(query[Fish].filter(_.name == "Nemo"))

// Access JSONB values
val characteristics = fish.characteristics.value // Gets FishCharacteristics
println(s"${fish.name} is a ${characteristics.species}")
```

## 🛠️ Available Tasks

### PostgreSQL Pod Commands
```bash
go-task start                   # Start PostgreSQL pod
go-task stop                    # Stop PostgreSQL pod
go-task logs                    # View PostgreSQL pod logs
go-task shell                   # Connect to PostgreSQL shell
```

### Database & Application Commands
```bash
go-task --list                  # Show all available tasks
go-task db:reset               # Reset database (nukes and recreates pod)
go-task compile                # Compile the project
go-task run                    # Run the application
go-task clean                  # Clean build artifacts
go-task test                   # Run tests
go-task status                 # Show system status
```

### Code Formatting
```bash
go-task fmt                    # Format code with scalafmt
go-task fmt:check              # Check code formatting
go-task fmt:all                # Format all code (main, test, sbt files)
```

## 🤖 GitHub Actions CI/CD

This project includes GitHub Actions workflows for automated testing and dependency management:

### CI Workflow (`.github/workflows/ci.yml`)
- **Triggers**: On push/PR to main/master branches
- **Java 21** with Temurin distribution
- **Checks**: Code formatting with scalafmt
- **Builds**: Compiles the project
- **Tests**: Runs the test suite

### Dependency Submission (`.github/workflows/dependencies.yml`)
- **Triggers**: On push to main/master branches
- **Submits**: Project dependencies to GitHub for security insights
- **Enables**: Dependabot alerts and dependency graph visualization

The workflows automatically run when you push code to GitHub, ensuring code quality and security monitoring.

### Development Commands
```bash
go-task dev:setup              # Full development environment setup
go-task dev:reset              # Reset everything for clean development
```

### Quick Shortcuts
```bash
go-task up                     # Quick start: postgres pod + compile
go-task down                   # Quick stop: stop postgres
```

## 🐛 Troubleshooting

### Common Issues

**1. "Port already in use" errors**
```bash
# Check what's using the port
ss -tulpn | grep 5432

# Change the port in your .env file
POSTGRES_PORT=5433  # or any available port

# Check status with your configured port
go-task status
```

**2. "Connection refused" errors**
```bash
# Check if PostgreSQL pod is running
podman pod list | grep postgres-jsonb

# Check if your configured port is available
ss -tulpn | grep $(grep POSTGRES_PORT .env | cut -d'=' -f2)

# Try restarting PostgreSQL
go-task down && go-task up
```

**3. "No .env file found" warnings**
```bash
# Make sure you've copied the example file
cp .env.example .env

# Verify your .env file is properly formatted
cat .env
```

**4. Java module access warnings**
- The project includes a `.jvmopts` file to handle Java 21 module restrictions
- If you see reflection warnings, they're automatically handled by sbt-dotenv

### Success Indicators

When everything is working correctly, you should see:
- ✅ `go-task status` shows PostgreSQL pod running
- ✅ `go-task run` displays the fish aquarium welcome message
- ✅ Fish data is displayed with proper JSONB characteristics
- ✅ New fish can be added and queried successfully

### Getting Help

If you run into issues:
1. Check your `.env` file configuration
2. Run `go-task status` to see current environment
3. Check the [Quill documentation](https://getquill.io/)
4. Look at the [ZIO-JSON documentation](https://zio.dev/zio-json/)
5. Open an issue in this repository

## 📝 Configuration

### Environment Variables

Configuration is handled through a `.env` file in the project root. Copy `.env.example` to `.env` and customize as needed:

```bash
# PostgreSQL Configuration
POSTGRES_HOST=localhost
POSTGRES_PORT=5433           # Change this if 5432 is already in use
POSTGRES_DB=testdb
POSTGRES_USER=testuser
POSTGRES_PASSWORD=testpass
POSTGRES_CONTAINER=postgres-jsonb

# Application Settings
APP_NAME=naughty_quill
LOG_LEVEL=INFO
```

### Database Connection

The database connection is automatically configured from your `.env` file. The application configuration in `src/main/resources/application.conf` uses these environment variables:

```hocon
myDatabaseConfig {
  dataSourceClassName = "org.postgresql.ds.PGSimpleDataSource"
  dataSource.url = "jdbc:postgresql://"${POSTGRES_HOST}":"${POSTGRES_PORT}"/"${POSTGRES_DB}
  dataSource.user = ${POSTGRES_USER}
  dataSource.password = ${POSTGRES_PASSWORD}
}
```

### Port Conflicts

If you get "port already in use" errors, change the `POSTGRES_PORT` in your `.env` file:

```bash
# Common alternatives:
POSTGRES_PORT=5433  # Most common alternative
POSTGRES_PORT=5434  # Another alternative
POSTGRES_PORT=15432 # High port alternative
```

## 🤝 Contributing

Found a bug or have an improvement? Please open an issue or submit a pull request!

## 📄 License

This project is provided as-is for educational purposes.

---

**Made with ❤️ to help developers get started with Scala 3 + ZIO + Quill + PostgreSQL JSONB**

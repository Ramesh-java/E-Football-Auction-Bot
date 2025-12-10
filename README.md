# eFootball WhatsApp Auction Bot

A WhatsApp-based auction management system for eFootball player auctions. This project consists of a Node.js WhatsApp bot that communicates with a Spring Boot REST API backend to manage player auctions, team balances, and bidding operations.

## 🎯 Features

- **WhatsApp Integration**: Manage auctions directly through WhatsApp using Baileys library
- **Player Auction Management**: Sell players to teams with price tracking
- **Team Balance Tracking**: Monitor and query team balances
- **Player Set Management**: Organize players into sets for sequential auction
- **Rollback Functionality**: Undo the last transaction if needed
- **Unsold Player Tracking**: Mark and retrieve unsold players
- **Real-time Updates**: Get instant responses via WhatsApp

## 🏗️ Architecture

The project consists of two main components:

1. **Node.js WhatsApp Bot** (`bot.js`): Handles WhatsApp communication and command processing
2. **Spring Boot Backend** (`auctionBot/`): REST API server managing auction logic, database operations, and business rules

## 📋 Prerequisites

- **Node.js** (v14 or higher)
- **Java 17** or higher
- **Maven** 3.6+
- **MySQL** database
- **WhatsApp** account for bot authentication

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone <repository-url>
cd efootball-whatsapp-bot
```

### 2. Backend Setup (Spring Boot)

Navigate to the backend directory:

```bash
cd auctionBot
```

#### Database Configuration

1. Create a MySQL database named `efootball`:
```sql
CREATE DATABASE efootball;
```

2. Update database credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/efootball
spring.datasource.username=your_username
spring.datasource.password=your_password
```

#### Build and Run

```bash
# Build the project
mvn clean install

# Run the Spring Boot application
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### 3. Frontend Setup (WhatsApp Bot)

Navigate back to the root directory:

```bash
cd ..
```

#### Install Dependencies

```bash
npm install
```

#### Run the Bot

```bash
npm start
```

On first run, you'll see a QR code in the terminal. Scan it with WhatsApp to authenticate the bot.

## 📱 Available Commands

All commands are prefixed with `!` and sent via WhatsApp:

| Command | Description | Example |
|---------|-------------|---------|
| `!sell <player> <price>M <team>` | Sell a player to a team | `!sell Messi 50M Barcelona` |
| `!balance <team_name>` | Check balance of a specific team | `!balance Barcelona` |
| `!allBalance` | View balance of all teams | `!allBalance` |
| `!view <team_name>` | View all players of a team | `!view Barcelona` |
| `!next` | Get the next player in the auction set | `!next` |
| `!rollback` | Undo the last transaction | `!rollback` |
| `!addAll` | Add multiple players (newline-separated) | `!addAll\nPlayer1\nPlayer2` |
| `!unsold <player_name>` | Mark a player as unsold | `!unsold PlayerName` |
| `!getUnsold` | Get list of all unsold players | `!getUnsold` |
| `!commands` | View all available commands | `!commands` |

## 🔧 Configuration

### Backend Configuration

Edit `auctionBot/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/efootball
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### Bot Configuration

The bot connects to the backend API at `http://localhost:8080`. If you need to change this, modify the API URLs in `bot.js`.

## 📁 Project Structure

```
efootball-whatsapp-bot/
├── bot.js                          # Main WhatsApp bot file
├── package.json                    # Node.js dependencies
├── auth/                           # WhatsApp authentication files (auto-generated)
│
└── auctionBot/                     # Spring Boot backend
    ├── src/
    │   └── main/
    │       ├── java/
    │       │   └── com/example/auctionbot/
    │       │       ├── controller/
    │       │       │   └── BotController.java    # REST API endpoints
    │       │       ├── service/
    │       │       │   ├── PlayerService.java    # Player business logic
    │       │       │   └── TeamService.java      # Team business logic
    │       │       ├── model/
    │       │       │   ├── Player.java           # Player entity
    │       │       │   └── Team.java             # Team entity
    │       │       ├── repository/
    │       │       │   ├── PlayerRepo.java       # Player data access
    │       │       │   └── TeamRepo.java         # Team data access
    │       │       └── DTO/
    │       │           ├── Data.java             # Request DTO
    │       │           └── Message.java          # Response DTO
    │       └── resources/
    │           └── application.properties        # Configuration
    └── pom.xml                     # Maven dependencies
```

## 🔌 API Endpoints

The Spring Boot backend exposes the following REST endpoints:

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auction/bid` | Process a player sale |
| GET | `/auction/balance?team={name}` | Get team balance |
| GET | `/auction/getAllBalance` | Get all team balances |
| GET | `/auction/getPlayers/{team}` | Get team's players |
| GET | `/auction/next` | Get next player in set |
| GET | `/auction/rollback` | Rollback last transaction |
| POST | `/auction/addAll` | Add multiple players |
| GET | `/auction/unsold/{name}` | Mark player as unsold |
| GET | `/auction/getUnsold` | Get unsold players |
| GET | `/auction/allCommands` | Get all available commands |

## 🛠️ Technologies Used

### Frontend (Bot)
- **@whiskeysockets/baileys**: WhatsApp Web API library
- **axios**: HTTP client for API requests
- **qrcode-terminal**: QR code generation for authentication
- **@hapi/boom**: HTTP error handling

### Backend
- **Spring Boot 3.5.3**: Java framework
- **Spring Data JPA**: Database abstraction layer
- **MySQL**: Relational database
- **Lombok**: Java boilerplate reduction
- **Maven**: Dependency management

## 🔐 Authentication

The bot uses WhatsApp Web authentication:
1. On first run, a QR code is displayed in the terminal
2. Scan the QR code with your WhatsApp mobile app
3. Authentication files are saved in the `auth/` directory
4. Subsequent runs will use saved credentials automatically

**Note**: If you get logged out, delete the `auth/` directory and scan the QR code again.

## 🐛 Troubleshooting

### Bot not connecting
- Ensure the backend is running on port 8080
- Check if the `auth/` directory exists and has valid credentials
- Delete `auth/` folder and re-authenticate if needed

### Database connection errors
- Verify MySQL is running
- Check database credentials in `application.properties`
- Ensure the `efootball` database exists

### API errors
- Verify the backend is running: `http://localhost:8080`
- Check backend logs for detailed error messages
- Ensure database tables are created (Hibernate will auto-create them)

## 📝 Notes

- The bot processes only text messages starting with `!`
- Player prices should end with `M` (e.g., `50M`)
- The auction set system processes players in batches of 10
- Rollback only works for the most recent transaction

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## 📄 License

This project is open source and available for personal use.

---

**Happy Auctioning! ⚽**










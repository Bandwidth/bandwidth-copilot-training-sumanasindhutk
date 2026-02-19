# Daily Journal Web Application

A standalone Spring Boot web application for managing daily journal entries with comprehensive health and productivity tracking features. All data is stored locally in the file system as individual JSON files.

## Features

- **📅 Daily Entries**: Create, view, edit, and delete journal entries
- **💧 Water Intake Tracking**: Track daily water consumption with an interactive slider
- **🏃 Exercise Logging**: Record exercise duration in minutes
- **😊 Mood Tracking**: Select from multiple mood options (Happy, Neutral, Sad, Stressed, Energetic)
- **✅ To-Do List**: Manage tasks with 1-5 star priority ratings and completion status
- **📝 Rich Notes**: Free-form text area for daily reflections and thoughts
- **🔍 Search & Filter**: Search entries by content and filter by date
- **💾 Local Storage**: All data stored as JSON files in the file system
- **📱 Responsive Design**: Works seamlessly on desktop and mobile devices

## Technology Stack

- **Backend**: Spring Boot 3.2.0 with Java 21
- **Frontend**: Thymeleaf templates with vanilla JavaScript
- **Storage**: Local file system (JSON format)
- **Build Tool**: Maven
- **Testing**: JUnit 5 + Mockito

## Project Structure

```
journal/
├── src/
│   ├── main/
│   │   ├── java/com/journal/
│   │   │   ├── JournalApplication.java          # Main application class
│   │   │   ├── controller/
│   │   │   │   ├── JournalController.java       # REST API endpoints
│   │   │   │   └── JournalViewController.java   # Template rendering
│   │   │   ├── service/
│   │   │   │   └── JournalService.java          # Business logic
│   │   │   ├── repository/
│   │   │   │   └── JournalFileRepository.java   # File system operations
│   │   │   ├── model/
│   │   │   │   ├── JournalEntry.java            # Entry model
│   │   │   │   └── TodoItem.java                # To-do item model
│   │   │   └── exception/
│   │   │       ├── GlobalExceptionHandler.java  # Error handling
│   │   │       └── JournalStorageException.java # Custom exception
│   │   └── resources/
│   │       ├── application.properties            # Configuration
│   │       └── templates/
│   │           └── journal.html                  # Main UI template
│   └── test/
│       └── java/com/journal/
│           ├── controller/
│           │   └── JournalControllerTest.java
│           └── service/
│               └── JournalServiceTest.java
├── pom.xml                                       # Maven configuration
└── README.md                                     # This file
```

## Getting Started

### Prerequisites

- **Java 21** or higher
- **Maven 3.6+** (or use the included Maven wrapper)

### Installation

1. **Clone or navigate to the project directory:**

   ```bash
   cd journal
   ```

2. **Build the project:**

   ```bash
   ./mvnw clean package
   ```

3. **Run the application:**

   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access the application:**
   Open your browser and navigate to: **http://localhost:8081**

### Running Tests

Run all tests with:

```bash
./mvnw test
```

## Configuration

The application can be configured via `src/main/resources/application.properties`:

```properties
# Server port (default: 8081)
server.port=8081

# Journal storage location (default: ./data/journals)
journal.storage.path=./data/journals

# Maximum file size (default: 10MB)
journal.max-file-size=10485760

# Logging level
logging.level.com.journal=DEBUG
```

## API Documentation

### REST Endpoints

#### Get All Entries

```http
GET /api/journal
```

Returns all journal entries, sorted by date (newest first).

**Response:** `200 OK`

```json
[
  {
    "id": "abc123",
    "date": "2026-02-19",
    "timestamp": "2026-02-19T10:30:00",
    "waterIntake": 8,
    "exerciseMinutes": 30,
    "mood": "Happy",
    "todos": [
      {
        "id": "todo-1",
        "description": "Complete project",
        "completed": false,
        "priority": 5
      }
    ],
    "notes": "Great day today!"
  }
]
```

#### Get Entry by ID

```http
GET /api/journal/{id}
```

Returns a specific journal entry.

**Response:** `200 OK` or `404 Not Found`

#### Get Entries by Date

```http
GET /api/journal/date/{date}
```

Returns all entries for a specific date (format: `yyyy-MM-dd`).

**Example:** `GET /api/journal/date/2026-02-19`

#### Get Entries by Date Range

```http
GET /api/journal/range?start={date}&end={date}
```

Returns entries within a date range (inclusive).

**Example:** `GET /api/journal/range?start=2026-02-01&end=2026-02-28`

#### Search Entries

```http
GET /api/journal/search?q={query}
```

Searches entries by query string (searches notes, mood, and to-do descriptions).

**Example:** `GET /api/journal/search?q=exercise`

#### Create Entry

```http
POST /api/journal
Content-Type: application/json

{
  "date": "2026-02-19",
  "waterIntake": 8,
  "exerciseMinutes": 30,
  "mood": "Happy",
  "todos": [],
  "notes": "Great day!"
}
```

**Response:** `201 Created`

#### Update Entry

```http
PUT /api/journal/{id}
Content-Type: application/json

{
  "date": "2026-02-19",
  "waterIntake": 10,
  "exerciseMinutes": 45,
  "mood": "Energetic",
  "todos": [],
  "notes": "Updated notes"
}
```

**Response:** `200 OK` or `404 Not Found`

#### Delete Entry

```http
DELETE /api/journal/{id}
```

**Response:** `204 No Content` or `404 Not Found`

## Data Storage

Journal entries are stored as individual JSON files in the configured storage directory:

- **Location:** `./data/journals/` (configurable via `journal.storage.path`)
- **File naming:** `{date}_{id}.json` (e.g., `2026-02-19_abc123.json`)
- **Format:** Pretty-printed JSON

**Example file content:**

```json
{
  "id": "abc123",
  "date": "2026-02-19",
  "timestamp": "2026-02-19T10:30:00",
  "waterIntake": 8,
  "exerciseMinutes": 30,
  "mood": "Happy",
  "todos": [
    {
      "id": "todo-1",
      "description": "Complete project",
      "completed": false,
      "priority": 5
    }
  ],
  "notes": "Great day today!"
}
```

## User Interface

The web interface is a single-page application with:

- **Sidebar:** List of all entries with date and mood preview
- **Main Content:** Entry form with all tracking features
- **Header:** Date filter and search bar

### Creating an Entry

1. Click the "New Entry" button
2. Fill in the date (defaults to today)
3. Set water intake using the slider
4. Enter exercise minutes
5. Select your mood
6. Add to-do items with priorities (1-5 stars)
7. Write notes in the text area
8. Click "Save"

### Editing an Entry

1. Click an entry in the sidebar
2. Modify any fields
3. Click "Save" to update

### Deleting an Entry

1. Open an entry
2. Click the "Delete" button
3. Confirm deletion

## Architecture

The application follows a clean 4-layer architecture:

1. **Controller Layer:** Handles HTTP requests and responses
2. **Service Layer:** Contains business logic and validation
3. **Repository Layer:** Manages file system operations
4. **Model Layer:** Defines data structures (JournalEntry, TodoItem)

### Design Decisions

- **File Storage:** Uses local file system instead of a database for simplicity and data portability
- **Spring Boot:** Provides robust REST API framework with dependency injection
- **No External Database:** All data is stored as JSON files for easy backup and migration
- **Vanilla JavaScript:** Keeps frontend lightweight without framework dependencies
- **Responsive Design:** Mobile-first CSS ensures usability on all devices

## Error Handling

The application includes comprehensive error handling:

- **400 Bad Request:** Invalid input data or validation failures
- **404 Not Found:** Entry not found
- **500 Internal Server Error:** File system errors or unexpected issues

All errors return consistent JSON responses:

```json
{
  "timestamp": "2026-02-19T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "path": "/api/journal"
}
```

## Testing

The project includes comprehensive unit tests:

- **Service Tests:** Test business logic and validation
- **Controller Tests:** Test REST API endpoints with MockMvc
- **Test Coverage:** All major CRUD operations and edge cases

Run tests with:

```bash
./mvnw test
```

## Future Enhancements

Potential features for future development:

- [ ] Data export (CSV, PDF)
- [ ] Data import from other formats
- [ ] Charts and visualizations (water intake trends, mood tracking)
- [ ] Reminders and notifications
- [ ] Tags and categories
- [ ] Markdown support for notes
- [ ] Dark mode
- [ ] Multi-user support with authentication
- [ ] Cloud backup integration

## Troubleshooting

### Application won't start

- Ensure Java 21 is installed: `java -version`
- Check if port 8081 is available
- Review logs for error messages

### Data not persisting

- Verify the `journal.storage.path` directory exists and is writable
- Check file system permissions
- Review application logs for I/O errors

### Tests failing

- Ensure all dependencies are downloaded: `./mvnw dependency:resolve`
- Clean and rebuild: `./mvnw clean test`

## License

This project is created for educational purposes as part of the Bandwidth Copilot Training workshop.

## Author

Created with GitHub Copilot

---

**Happy Journaling! 📔✨**

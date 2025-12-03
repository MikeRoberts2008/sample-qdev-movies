# Movie Service - Spring Boot Demo Application 🏴‍☠️

A simple movie catalog web application built with Spring Boot, demonstrating Java application development best practices with a pirate-themed search and filtering system!

## Features

- **Movie Catalog**: Browse 12 classic movies with detailed information
- **🔍 Advanced Movie Search**: Search and filter movies by name, ID, or genre with pirate-themed interface
- **⚓ REST API**: Comprehensive search API with JSON responses and pirate language
- **Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **Responsive Design**: Mobile-first design that works on all devices
- **Modern UI**: Dark theme with gradient backgrounds, smooth animations, and pirate flair

## Technology Stack

- **Java 8**
- **Spring Boot 2.7.18**
- **Maven** for dependency management
- **Thymeleaf** for templating
- **Log4j 2.20.0**
- **JUnit 5.8.2**

## Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **Movie List with Search**: http://localhost:8080/movies
- **Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)
- **Search API**: http://localhost:8080/movies/search?name=prison&genre=drama

## 🏴‍☠️ Search & Filtering Features

### Web Interface Search

The main movies page (`/movies`) now includes a comprehensive search form with:

- **Movie Name Search**: Find movies by partial name match (case-insensitive)
- **Movie ID Search**: Find specific movie by exact ID
- **Genre Filter**: Filter movies by genre using dropdown selection
- **Combined Search**: Use multiple criteria together for precise results
- **Pirate-themed Messages**: Get search results with authentic pirate language!

**Example Searches:**
- Search for "prison" to find "The Prison Escape"
- Filter by "Crime/Drama" genre to see crime movies
- Search by ID "1" to get the first movie
- Combine name "family" with genre "crime" for precise results

### REST API Search

#### Search Movies Endpoint
```
GET /movies/search
```

**Query Parameters:**
- `name` (optional): Movie name to search for (partial match, case-insensitive)
- `id` (optional): Specific movie ID to find (exact match)
- `genre` (optional): Genre to filter by (partial match, case-insensitive)

**Note**: At least one search parameter must be provided.

**Example Requests:**
```bash
# Search by movie name
curl "http://localhost:8080/movies/search?name=prison"

# Search by genre
curl "http://localhost:8080/movies/search?genre=drama"

# Search by ID
curl "http://localhost:8080/movies/search?id=1"

# Combined search
curl "http://localhost:8080/movies/search?name=family&genre=crime"

# Search with special characters
curl "http://localhost:8080/movies/search?name=space%20wars"
```

**Response Format:**
```json
{
  "success": true,
  "message": "Ahoy! Found 1 movie in our treasure chest, ye savvy sailor!",
  "movies": [
    {
      "id": 1,
      "movieName": "The Prison Escape",
      "director": "John Director",
      "year": 1994,
      "genre": "Drama",
      "description": "Two imprisoned men bond over a number of years...",
      "duration": 142,
      "imdbRating": 5.0,
      "icon": "🎬"
    }
  ],
  "totalResults": 1,
  "searchCriteria": {
    "name": "prison"
  }
}
```

**Error Responses:**
```json
{
  "success": false,
  "message": "Arrr! Ye need to provide at least one search criterion, matey!",
  "movies": []
}
```

### Search Features

- **Case-Insensitive**: All text searches are case-insensitive
- **Partial Matching**: Name and genre searches support partial matches
- **Whitespace Handling**: Leading/trailing whitespace is automatically trimmed
- **Combined Criteria**: Use multiple search parameters with AND logic
- **Empty Results Handling**: Shows helpful pirate-themed messages when no results found
- **Input Validation**: Comprehensive validation with pirate-themed error messages
- **Genre Dropdown**: Pre-populated with all available genres from the movie collection

## Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── movies/
│   │       │   ├── MoviesApplication.java    # Main Spring Boot application
│   │       │   ├── MoviesController.java     # REST controller with search endpoints
│   │       │   ├── MovieService.java         # Business logic with search methods
│   │       │   ├── Movie.java                # Movie data model
│   │       │   ├── Review.java               # Review data model
│   │       │   └── ReviewService.java        # Review business logic
│   │       └── utils/
│   │           ├── MovieIconUtils.java       # Movie icon utilities
│   │           └── MovieUtils.java           # Movie validation utilities
│   └── resources/
│       ├── application.yml                   # Application configuration
│       ├── movies.json                       # Movie data
│       ├── mock-reviews.json                 # Mock review data
│       ├── log4j2.xml                        # Logging configuration
│       ├── static/css/
│       │   └── movies.css                    # Enhanced CSS with search styling
│       └── templates/
│           ├── movies.html                   # Main page with search form
│           └── movie-details.html            # Movie details page
└── test/                                     # Comprehensive unit tests
    └── java/
        └── com/amazonaws/samples/qdevmovies/movies/
            ├── MovieServiceTest.java         # Service layer tests
            └── MoviesControllerTest.java     # Controller tests with API testing
```

## API Endpoints

### Get All Movies (with Search)
```
GET /movies
```
Returns an HTML page displaying movies with search form and filtering capabilities.

**Query Parameters (optional):**
- `name`: Filter movies by name
- `id`: Find specific movie by ID
- `genre`: Filter movies by genre

**Examples:**
```
http://localhost:8080/movies
http://localhost:8080/movies?name=prison
http://localhost:8080/movies?genre=drama
http://localhost:8080/movies?name=family&genre=crime
```

### Search Movies (REST API)
```
GET /movies/search
```
Returns JSON response with search results and pirate-themed messages.

**Query Parameters (at least one required):**
- `name`: Movie name to search for (partial match)
- `id`: Specific movie ID (exact match)
- `genre`: Genre to filter by (partial match)

### Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

## Testing

Run the comprehensive test suite:

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=MovieServiceTest
mvn test -Dtest=MoviesControllerTest

# Run tests with coverage
mvn test jacoco:report
```

### Test Coverage

The application includes comprehensive unit tests covering:

- **MovieService**: Search functionality, edge cases, validation
- **MoviesController**: Web endpoints, REST API, error handling
- **Integration Tests**: End-to-end testing of search features
- **Pirate Language**: Validation of themed messages and responses

## Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

### Search not working

1. Verify at least one search parameter is provided
2. Check that movie data is loaded correctly
3. Review application logs for pirate-themed error messages

### API returns 400 Bad Request

Common causes:
- No search parameters provided
- Invalid movie ID (must be positive number)
- Empty search strings

## Contributing

This project demonstrates modern Spring Boot development practices. Feel free to:
- Add more movies to the catalog
- Enhance the search functionality
- Improve the pirate-themed UI/UX
- Add new API endpoints
- Extend the filtering capabilities
- Improve the responsive design

### Development Guidelines

- Follow pirate-themed naming in comments and messages
- Maintain comprehensive test coverage
- Use proper error handling with themed messages
- Follow Spring Boot best practices
- Document all new API endpoints

## License

This sample code is licensed under the MIT-0 License. See the LICENSE file.

---

*Arrr! May yer code be bug-free and yer searches swift, ye savvy developer! 🏴‍☠️*

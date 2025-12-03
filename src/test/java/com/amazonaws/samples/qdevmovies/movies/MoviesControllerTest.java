package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MoviesController including search functionality
 * Arrr! These tests be checkin' our movie controller treasure huntin' methods, matey!
 */
public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MovieService mockMovieService;
    private ReviewService mockReviewService;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services with test data
        mockMovieService = new MovieService() {
            private final List<Movie> testMovies = Arrays.asList(
                new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5),
                new Movie(2L, "Action Movie", "Action Director", 2022, "Action", "Action description", 110, 4.0),
                new Movie(3L, "Comedy Movie", "Comedy Director", 2021, "Comedy", "Comedy description", 95, 3.5)
            );
            
            @Override
            public List<Movie> getAllMovies() {
                return testMovies;
            }
            
            @Override
            public Optional<Movie> getMovieById(Long id) {
                return testMovies.stream().filter(m -> m.getId().equals(id)).findFirst();
            }
            
            @Override
            public List<Movie> searchMovies(String name, Long id, String genre) {
                return testMovies.stream()
                    .filter(movie -> {
                        if (id != null && !movie.getId().equals(id)) return false;
                        if (name != null && !name.trim().isEmpty() && 
                            !movie.getMovieName().toLowerCase().contains(name.toLowerCase().trim())) return false;
                        if (genre != null && !genre.trim().isEmpty() && 
                            !movie.getGenre().toLowerCase().contains(genre.toLowerCase().trim())) return false;
                        return true;
                    })
                    .collect(java.util.stream.Collectors.toList());
            }
            
            @Override
            public List<String> getAllGenres() {
                return Arrays.asList("Action", "Comedy", "Drama");
            }
        };
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    @DisplayName("Should return movies view without search parameters")
    public void testGetMovies() {
        String result = moviesController.getMovies(model, null, null, null);
        
        assertNotNull(result);
        assertEquals("movies", result);
        assertEquals(3, ((List<?>) model.getAttribute("movies")).size());
        assertEquals("", model.getAttribute("searchMessage"));
    }

    @Test
    @DisplayName("Should search movies by name and return results")
    public void testGetMovies_SearchByName() {
        String result = moviesController.getMovies(model, "test", null, null);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
        
        String searchMessage = (String) model.getAttribute("searchMessage");
        assertTrue(searchMessage.contains("Found 1 movie"));
        assertTrue(searchMessage.contains("Ahoy!"));
    }

    @Test
    @DisplayName("Should search movies by ID and return results")
    public void testGetMovies_SearchById() {
        String result = moviesController.getMovies(model, null, 2L, null);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Action Movie", movies.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should search movies by genre and return results")
    public void testGetMovies_SearchByGenre() {
        String result = moviesController.getMovies(model, null, null, "comedy");
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Comedy Movie", movies.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should return all movies when search yields no results")
    public void testGetMovies_NoSearchResults() {
        String result = moviesController.getMovies(model, "nonexistent", null, null);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(3, movies.size()); // Should show all movies when no results found
        
        String searchMessage = (String) model.getAttribute("searchMessage");
        assertTrue(searchMessage.contains("No treasure found"));
        assertTrue(searchMessage.contains("Arrr!"));
    }

    @Test
    @DisplayName("Should include search form attributes in model")
    public void testGetMovies_SearchFormAttributes() {
        String result = moviesController.getMovies(model, "test", 1L, "drama");
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        assertEquals("test", model.getAttribute("searchName"));
        assertEquals("1", model.getAttribute("searchId"));
        assertEquals("drama", model.getAttribute("searchGenre"));
        
        @SuppressWarnings("unchecked")
        List<String> genres = (List<String>) model.getAttribute("genres");
        assertNotNull(genres);
        assertTrue(genres.contains("Drama"));
    }

    @Test
    @DisplayName("Should get movie details successfully")
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model);
        
        assertNotNull(result);
        assertEquals("movie-details", result);
    }

    @Test
    @DisplayName("Should return error page for non-existent movie")
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model);
        
        assertNotNull(result);
        assertEquals("error", result);
    }

    // REST API Tests

    @Test
    @DisplayName("REST API: Should return successful search results")
    public void testSearchMoviesAPI_Success() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies("test", null, null);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals(1, body.get("totalResults"));
        
        String message = (String) body.get("message");
        assertTrue(message.contains("Ahoy!"));
        assertTrue(message.contains("Found 1 movie"));
    }

    @Test
    @DisplayName("REST API: Should return empty results with pirate message")
    public void testSearchMoviesAPI_NoResults() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies("nonexistent", null, null);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals(0, body.get("totalResults"));
        
        String message = (String) body.get("message");
        assertTrue(message.contains("Arrr!"));
        assertTrue(message.contains("No treasure found"));
    }

    @Test
    @DisplayName("REST API: Should return bad request for no search criteria")
    public void testSearchMoviesAPI_NoSearchCriteria() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies(null, null, null);
        
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertFalse((Boolean) body.get("success"));
        
        String message = (String) body.get("message");
        assertTrue(message.contains("Arrr!"));
        assertTrue(message.contains("at least one search criterion"));
    }

    @Test
    @DisplayName("REST API: Should return bad request for invalid ID")
    public void testSearchMoviesAPI_InvalidId() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies(null, -1L, null);
        
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertFalse((Boolean) body.get("success"));
        
        String message = (String) body.get("message");
        assertTrue(message.contains("Shiver me timbers!"));
        assertTrue(message.contains("invalid"));
    }

    @Test
    @DisplayName("REST API: Should include search criteria in response")
    public void testSearchMoviesAPI_IncludesSearchCriteria() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies("test", 1L, "drama");
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> searchCriteria = (Map<String, Object>) body.get("searchCriteria");
        assertNotNull(searchCriteria);
        assertEquals("test", searchCriteria.get("name"));
        assertEquals(1L, searchCriteria.get("id"));
        assertEquals("drama", searchCriteria.get("genre"));
    }

    @Test
    @DisplayName("REST API: Should handle empty string parameters correctly")
    public void testSearchMoviesAPI_EmptyStringParameters() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies("", null, "");
        
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertFalse((Boolean) body.get("success"));
    }

    @Test
    @DisplayName("Should integrate with movie service correctly")
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(3, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
        
        List<Movie> searchResults = mockMovieService.searchMovies("action", null, null);
        assertEquals(1, searchResults.size());
        assertEquals("Action Movie", searchResults.get(0).getMovieName());
    }
}

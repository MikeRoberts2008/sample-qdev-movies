package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MovieService search functionality
 * Arrr! These tests be checkin' our movie searchin' treasure huntin' methods, matey!
 */
public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    @Test
    @DisplayName("Should return all movies when no search criteria provided")
    public void testSearchMovies_NoSearchCriteria() {
        List<Movie> results = movieService.searchMovies(null, null, null);
        
        assertNotNull(results);
        assertEquals(movieService.getAllMovies().size(), results.size());
    }

    @Test
    @DisplayName("Should return all movies when empty search criteria provided")
    public void testSearchMovies_EmptySearchCriteria() {
        List<Movie> results = movieService.searchMovies("", null, "");
        
        assertNotNull(results);
        assertEquals(movieService.getAllMovies().size(), results.size());
    }

    @Test
    @DisplayName("Should find movies by exact ID")
    public void testSearchMovies_ByExactId() {
        List<Movie> results = movieService.searchMovies(null, 1L, null);
        
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should return empty list for non-existent ID")
    public void testSearchMovies_ByNonExistentId() {
        List<Movie> results = movieService.searchMovies(null, 999L, null);
        
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should find movies by partial name match (case-insensitive)")
    public void testSearchMovies_ByPartialName() {
        List<Movie> results = movieService.searchMovies("prison", null, null);
        
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should find movies by partial name match with different case")
    public void testSearchMovies_ByPartialNameDifferentCase() {
        List<Movie> results = movieService.searchMovies("PRISON", null, null);
        
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should find movies by genre (case-insensitive)")
    public void testSearchMovies_ByGenre() {
        List<Movie> results = movieService.searchMovies(null, null, "drama");
        
        assertNotNull(results);
        assertTrue(results.size() > 0);
        
        // All results should contain "Drama" in genre
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    @DisplayName("Should find movies by partial genre match")
    public void testSearchMovies_ByPartialGenre() {
        List<Movie> results = movieService.searchMovies(null, null, "crime");
        
        assertNotNull(results);
        assertTrue(results.size() > 0);
        
        // All results should contain "Crime" in genre
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("crime"));
        }
    }

    @Test
    @DisplayName("Should combine multiple search criteria with AND logic")
    public void testSearchMovies_CombinedCriteria() {
        List<Movie> results = movieService.searchMovies("family", null, "crime");
        
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("The Family Boss", results.get(0).getMovieName());
        assertTrue(results.get(0).getGenre().toLowerCase().contains("crime"));
    }

    @Test
    @DisplayName("Should return empty list when combined criteria don't match")
    public void testSearchMovies_CombinedCriteriaNoMatch() {
        List<Movie> results = movieService.searchMovies("prison", null, "comedy");
        
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should handle whitespace in search terms")
    public void testSearchMovies_WithWhitespace() {
        List<Movie> results = movieService.searchMovies("  prison  ", null, "  drama  ");
        
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should return empty list for non-matching name")
    public void testSearchMovies_NonMatchingName() {
        List<Movie> results = movieService.searchMovies("nonexistent", null, null);
        
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list for non-matching genre")
    public void testSearchMovies_NonMatchingGenre() {
        List<Movie> results = movieService.searchMovies(null, null, "nonexistent");
        
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should get all unique genres")
    public void testGetAllGenres() {
        List<String> genres = movieService.getAllGenres();
        
        assertNotNull(genres);
        assertTrue(genres.size() > 0);
        
        // Should contain expected genres
        assertTrue(genres.contains("Drama"));
        assertTrue(genres.contains("Crime/Drama"));
        assertTrue(genres.contains("Action/Crime"));
        
        // Should be sorted
        for (int i = 1; i < genres.size(); i++) {
            assertTrue(genres.get(i-1).compareTo(genres.get(i)) <= 0);
        }
    }

    @Test
    @DisplayName("Should find movies with special characters in search")
    public void testSearchMovies_SpecialCharacters() {
        List<Movie> results = movieService.searchMovies("space wars:", null, null);
        
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Space Wars: The Beginning", results.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should handle search by ID with name and genre filters")
    public void testSearchMovies_IdWithOtherFilters() {
        // Search for ID 1 with matching name and genre
        List<Movie> results = movieService.searchMovies("prison", 1L, "drama");
        
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
        
        // Search for ID 1 with non-matching name - should return empty
        List<Movie> noResults = movieService.searchMovies("family", 1L, "drama");
        assertNotNull(noResults);
        assertTrue(noResults.isEmpty());
    }
}
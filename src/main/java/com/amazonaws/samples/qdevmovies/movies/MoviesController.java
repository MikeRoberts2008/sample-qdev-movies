package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model,
                           @RequestParam(value = "name", required = false) String name,
                           @RequestParam(value = "id", required = false) Long id,
                           @RequestParam(value = "genre", required = false) String genre) {
        logger.info("Ahoy matey! Fetchin' movies with search criteria - name: '{}', id: '{}', genre: '{}'", name, id, genre);
        
        List<Movie> movies;
        String searchMessage = "";
        boolean isSearch = (name != null && !name.trim().isEmpty()) || 
                          (id != null) || 
                          (genre != null && !genre.trim().isEmpty());
        
        if (isSearch) {
            movies = movieService.searchMovies(name, id, genre);
            if (movies.isEmpty()) {
                searchMessage = "Arrr! No treasure found with those search terms, matey! Try different criteria or browse all our fine movies below.";
                movies = movieService.getAllMovies(); // Show all movies when no results found
            } else {
                searchMessage = String.format("Ahoy! Found %d movie%s matching yer search, ye savvy sailor!", 
                    movies.size(), movies.size() == 1 ? "" : "s");
            }
        } else {
            movies = movieService.getAllMovies();
        }
        
        model.addAttribute("movies", movies);
        model.addAttribute("searchMessage", searchMessage);
        model.addAttribute("genres", movieService.getAllGenres());
        model.addAttribute("searchName", name != null ? name : "");
        model.addAttribute("searchId", id != null ? id.toString() : "");
        model.addAttribute("searchGenre", genre != null ? genre : "");
        
        return "movies";
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }

    /**
     * REST API endpoint for movie search - returns JSON response
     * Arrr! This be the treasure map for searchin' movies via REST API, matey!
     * 
     * @param name Movie name to search for (optional)
     * @param id Specific movie ID to find (optional)
     * @param genre Genre to filter by (optional)
     * @return ResponseEntity with search results and pirate-themed messages
     */
    @GetMapping("/movies/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre) {
        
        logger.info("Ahoy! REST API search request - name: '{}', id: '{}', genre: '{}'", name, id, genre);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate that at least one search parameter is provided
            boolean hasSearchCriteria = (name != null && !name.trim().isEmpty()) || 
                                       (id != null) || 
                                       (genre != null && !genre.trim().isEmpty());
            
            if (!hasSearchCriteria) {
                response.put("success", false);
                response.put("message", "Arrr! Ye need to provide at least one search criterion, matey! Use 'name', 'id', or 'genre' parameters.");
                response.put("movies", movieService.getAllMovies());
                return ResponseEntity.badRequest().body(response);
            }
            
            // Validate ID parameter if provided
            if (id != null && id <= 0) {
                response.put("success", false);
                response.put("message", "Shiver me timbers! That ID be invalid, ye scurvy dog! Movie ID must be a positive number.");
                response.put("movies", List.of());
                return ResponseEntity.badRequest().body(response);
            }
            
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            
            response.put("success", true);
            response.put("movies", searchResults);
            response.put("totalResults", searchResults.size());
            
            if (searchResults.isEmpty()) {
                response.put("message", "Arrr! No treasure found with those search terms, matey! The seven seas be vast, but yer search came up empty.");
            } else {
                response.put("message", String.format("Ahoy! Found %d movie%s in our treasure chest, ye savvy sailor!", 
                    searchResults.size(), searchResults.size() == 1 ? "" : "s"));
            }
            
            // Add search criteria to response for reference
            Map<String, Object> searchCriteria = new HashMap<>();
            if (name != null && !name.trim().isEmpty()) searchCriteria.put("name", name.trim());
            if (id != null) searchCriteria.put("id", id);
            if (genre != null && !genre.trim().isEmpty()) searchCriteria.put("genre", genre.trim());
            response.put("searchCriteria", searchCriteria);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Blimey! Error during movie search: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Arrr! Something went wrong while searchin' for movies, matey! Try again later.");
            response.put("movies", List.of());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
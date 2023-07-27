package edu.uci.ics.fabflixmobile.data.model;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Movie {
    private final String name;
    private final String year;
    private final String director;
    private final String stars;
    private final String genres;

    public Movie(String name, String year, String director, String stars, String genres) {
        this.name = name;
        this.year = year;
        this.director = director;
        this.stars = stars;
        this.genres = genres;
    }

    public String getName() {
        return name;
    }

    public String getYear() {
        return year;
    }

    public String getDirector() { return director; }

    public String getStars() { return stars; }

    public String getGenres() { return genres; }
}
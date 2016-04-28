package com.example.rina.movieapplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by RINA on 4/25/2016.
 */
public class Movie implements Serializable {

    private String posterPath;
    private String movieTitle;
    private String overView;
    private String releaseDate;
    private String voteAverage;
    private String id;

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Movie() {
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getMovieTite() {
        return movieTitle;
    }

    public void setMovieTite(String movieTite) {
        this.movieTitle = movieTite;
    }

    public String getOverView() {
        return overView;
    }

    public void setOverView(String overView) {
        this.overView = overView;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public JSONObject toJson () throws JSONException {

        final String POSTER_PATH = "poster_path";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String ORIGINAL_TITLE = "original_title";
        final String VOTE_AVERAGE = "vote_average";
        final String ID = "id";

        JSONObject movie = new JSONObject();

        movie.put(POSTER_PATH, this.posterPath);
        movie.put(OVERVIEW, this.overView);
        movie.put(RELEASE_DATE, this.releaseDate);
        movie.put(ORIGINAL_TITLE, this.movieTitle);
        movie.put(VOTE_AVERAGE, this.voteAverage);
        movie.put(ID, this.id);

        return movie;
    }
}

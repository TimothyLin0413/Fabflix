package edu.uci.ics.fabflixmobile.ui.info;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListViewAdapter;

import java.util.ArrayList;

public class SingleMovieActivity extends AppCompatActivity {

    TextView movieTitle;
    TextView movieYear;
    TextView movieGenres;
    TextView movieDirector;
    TextView movieStars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlemovie);

        Intent intent = getIntent();
        String movieJsonString = intent.getStringExtra("movieInfo");
        try {
            JSONObject movieInfo = new JSONObject(movieJsonString); // array of json objects of movie information
            Log.d("SingleMovie.title", movieInfo.get("title").toString());
            movieTitle = findViewById(R.id.movieTitle);
            movieYear = findViewById(R.id.movieYear);
            movieGenres = findViewById(R.id.movieGenres);
            movieDirector = findViewById(R.id.movieDirector);
            movieStars = findViewById(R.id.movieStars);

            movieTitle.setText(movieInfo.get("title").toString());
            movieYear.setText(movieInfo.get("year").toString());
            movieGenres.setText(movieInfo.get("genres").toString());
            movieDirector.setText(movieInfo.get("director").toString());
            movieStars.setText("Movie Stars: " + movieInfo.get("stars").toString());
        } catch (Exception e) {
            Log.d("Error.status", e.toString());
        }
    }
}

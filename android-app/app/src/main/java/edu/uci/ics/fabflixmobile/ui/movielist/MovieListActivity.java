package edu.uci.ics.fabflixmobile.ui.movielist;

import static java.lang.Math.min;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.info.SingleMovieActivity;
import edu.uci.ics.fabflixmobile.ui.search.TitleSearchActivity;

import java.util.ArrayList;
import java.util.List;

public class MovieListActivity extends AppCompatActivity {

    Button prevButton;
    Button nextButton;
    String moviesString;
    int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);

        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);

        Intent intent = getIntent();
        moviesString = intent.getStringExtra("moviesArray");
        counter = intent.getIntExtra("counter", 0);
        if (counter < 0) {
            counter = 0;
        }
        try {
            JSONArray moviesArray = new JSONArray(moviesString); // array of json objects of movie information
            final ArrayList<Movie> movies = new ArrayList<>();
            if (counter * 20 > moviesArray.length()) {
                counter--;
            }
            for (int i = 0; i < min((moviesArray.length() - (counter * 20)), 20); i++) {
                JSONObject movieInfo = moviesArray.getJSONObject(i + (counter*20));
                String[] listStars = movieInfo.get("stars").toString().split(",");
                String printStars = "";
                if (listStars.length > 3) {
                    printStars = listStars[0] + ", " + listStars[1] + ", " + listStars[2];
                } else {
                    printStars = movieInfo.get("stars").toString();
                }
                Movie movie = new Movie(movieInfo.get("title").toString(), movieInfo.get("year").toString(), movieInfo.get("director").toString(), printStars, movieInfo.get("genres").toString());
                movies.add(movie);
            }
            MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
            ListView listView = findViewById(R.id.list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Movie movie = movies.get(position);
                Log.d("Position.title", movie.getName());
                @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, %s", position, movie.getName(), movie.getYear());
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                finish();
                Intent InfoPage = new Intent(MovieListActivity.this, SingleMovieActivity.class);
                try {
                    InfoPage.putExtra("movieInfo", moviesArray.getJSONObject(position + (counter * 20)).toString());
                } catch (Exception e) {
                    // do not need anything yet
                }
                startActivity(InfoPage);
            });
        } catch (Exception e) {
            Log.d("Error.status", e.toString());
        }

        prevButton.setOnClickListener(view -> previous());
        nextButton.setOnClickListener(view -> next());
    }

    private void previous() {
        counter--;
        Intent MovieListPage = new Intent(MovieListActivity.this, MovieListActivity.class);
        MovieListPage.putExtra("moviesArray", moviesString);
        MovieListPage.putExtra("counter", counter);
        startActivity(MovieListPage);
    }

    private void next() {
        counter++;
        Intent MovieListPage = new Intent(MovieListActivity.this, MovieListActivity.class);
        MovieListPage.putExtra("moviesArray", moviesString);
        MovieListPage.putExtra("counter", counter);
        startActivity(MovieListPage);
    }
}
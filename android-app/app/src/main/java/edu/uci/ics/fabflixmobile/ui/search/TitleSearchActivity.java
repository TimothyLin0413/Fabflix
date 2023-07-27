package edu.uci.ics.fabflixmobile.ui.search;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;

import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;

public class TitleSearchActivity extends AppCompatActivity {

    EditText title;
    Button submit;

    /*
      In Android, localhost is the address of the device or the emulator.
      To connect to your machine, you need to use the below IP address
     */
    private final String host = "ec2-54-147-10-78.compute-1.amazonaws.com";
    private final String port = "8443";
    private final String domain = "cs122b-fall22-project";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);

        // upon creation, inflate and initialize the layout
        title = findViewById(R.id.inputTitle);
        submit = findViewById(R.id.submitButton);

        submit.setOnClickListener(view -> search());
    }

    @SuppressLint("SetTextI18n")
    public void search() {
        Toast.makeText(TitleSearchActivity.this, title.getText().toString(), Toast.LENGTH_SHORT).show();
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        final StringRequest searchRequest = new StringRequest(
                Request.Method.POST,
                baseURL + "/api/auto-complete",
                response -> {
                    Log.d("search.success", response);
                    Log.d("title", title.getText().toString());
                    try {
                        JSONArray movies = new JSONArray(response);
                        Log.d("search.status", "" + movies.length());
                        if (movies.length() > 0) {
                            finish();
                            Intent MovieListPage = new Intent(TitleSearchActivity.this, MovieListActivity.class);
                            MovieListPage.putExtra("moviesArray", movies.toString());
                            MovieListPage.putExtra("counter", 0);
                            startActivity(MovieListPage);
                        }
                    } catch (Exception e) {
                        Log.d("search.fail", response);
                    };
                },
                error -> {
                    // error
                    Log.d("search.error", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                // POST request form data
                final Map<String, String> params = new HashMap<>();
                params.put("query", title.getText().toString());
                return params;
            }
        };
        // important: queue.add is where the login request is actually sent
        queue.add(searchRequest);
    }
}
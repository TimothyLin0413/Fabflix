
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

// server endpoint URL
@WebServlet(name= "AutoCompleteServlet", urlPatterns = "/api/auto-complete")
public class AutoCompleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;


    // Create a dataSource which registered in web.xml
    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private JsonArray generateSuggestions(String keywords) {
        //select id, title from movies where match (title) against ('+lo*' in boolean mode);
        // alter table movies add fulltext(title); to add in sql before
        String query = "SELECT id, title FROM movies WHERE MATCH (title) AGAINST (? IN BOOLEAN MODE) LIMIT 10";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(query);) {
             JsonArray jsonArray = new JsonArray();

             statement.setString(1, keywords);

             ResultSet rs = statement.executeQuery();
             while (rs.next()) {
                 String movieId = rs.getString("id");
                 String movieTitle = rs.getString("title");

                 JsonObject jsonObject = new JsonObject();
                 jsonObject.addProperty("value", movieTitle);

                 JsonObject jsonObject1 = new JsonObject();
                 jsonObject1.addProperty("movieId", movieId);

                 jsonObject.add("data", jsonObject1);
                 jsonArray.add(jsonObject);
            }

            rs.close();
            statement.close();
            return jsonArray;
        } catch (Exception e) {
            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            JsonArray errorArray = new JsonArray();
            jsonObject.addProperty("errorMessage", e.getMessage() + " from generateSuggestions");
            errorArray.add(jsonObject);
            return errorArray;
        }
    }

    private JsonArray generateAndroidSuggestions(String keywords) {
        //select id, title from movies where match (title) against ('+lo*' in boolean mode);
        // alter table movies add fulltext(title); to add in sql before
        String query = "SELECT  m.id, m.title, m.movie_year, m.director, \n" +
                "       starss.star_names as names, genres\n" +
                "       from movies as m JOIN(\n" +
                "        SELECT sm.movieId, \n" +
                "      GROUP_CONCAT(s.name order by s.movieCnt desc, s.name) as star_names, \n" +
                "      GROUP_CONCAT(s.id order by s.movieCnt desc, s.name) as star_ids FROM stars_in_movies AS sm \n" +
                "        INNER JOIN  (\n" +
                "          select s.id, s.name,count(*) as movieCnt \n" +
                "          from stars_in_movies smtemp, stars s \n" +
                "          where smtemp.starId = s.id GROUP by s.id \n" +
                "          ) as s on s.id = sm.starId group by sm.movieId) as starss on starss.movieId=m.id \n" +
                "        INNER JOIN (\n" +
                "          SELECT gm.movieId, GROUP_CONCAT(g.name order by g.name asc) as genres from genres_in_movies gm \n" +
                "          INNER JOIN genres g ON g.id = gm.genreId GROUP BY movieId\n" +
                "        ) as gtemp ON gtemp.movieId = m.id WHERE MATCH (title) AGAINST (? IN BOOLEAN MODE) LIMIT 100;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(query);) {
            JsonArray jsonArray = new JsonArray();

            statement.setString(1, keywords);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String movieId = rs.getString("id");
                String movieTitle = rs.getString("title");
                String movieDirector = rs.getString("director");
                String movieYear = rs.getString("movie_year");
                String stars = rs.getString("names");
                String genres = rs.getString("genres");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", movieId);
                jsonObject.addProperty("title", movieTitle);
                jsonObject.addProperty("director", movieDirector);
                jsonObject.addProperty("year", movieYear);
                jsonObject.addProperty("stars", stars);
                jsonObject.addProperty("genres", genres);

                jsonArray.add(jsonObject);
            }

            rs.close();
            statement.close();
            return jsonArray;
        } catch (Exception e) {
            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            JsonArray errorArray = new JsonArray();
            jsonObject.addProperty("errorMessage", e.getMessage() + " from generateSuggestions");
            errorArray.add(jsonObject);
            return errorArray;
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // setup the response json arrray
            JsonArray jsonArray = new JsonArray();

            // get the query string from parameter
            String query = request.getParameter("query");

            // return the empty json array if query is null or empty
            if (query == null || query.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }

            // search on superheroes and add the results to JSON Array
            // this example only does a substring match
            // TODO: in project 4, you should do full text search with MySQL to find the matches on movies and stars
            String[] inputSplit = query.split(" ");
            String keywords = "";
            for (String s : inputSplit) {
                keywords += "+" + s + "* ";
            }
            keywords = keywords.substring(0, keywords.length()-1);

            System.out.println("before generate function");
            // call the function to create the results
            jsonArray = generateSuggestions(keywords);
            System.out.println(jsonArray);

            response.getWriter().write(jsonArray.toString());
            return;
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // setup the response json arrray
            JsonArray jsonArray = new JsonArray();

            // get the query string from parameter
            String query = request.getParameter("query");

            // return the empty json array if query is null or empty
            if (query == null || query.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }

            // search on superheroes and add the results to JSON Array
            // this example only does a substring match
            // TODO: in project 4, you should do full text search with MySQL to find the matches on movies and stars
            String[] inputSplit = query.split(" ");
            String keywords = "";
            for (String s : inputSplit) {
                keywords += "+" + s + "* ";
            }
            keywords = keywords.substring(0, keywords.length()-1);

            System.out.println("before generate function");
            // call the function to create the results
            jsonArray = generateAndroidSuggestions(keywords);
            System.out.println(jsonArray);

            response.getWriter().write(jsonArray.toString());
            return;
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }
    }
}
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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-movie"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // The single_instance_log1 message can be found in localhost single_instance_log1
        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query = "SELECT m.id, m.title, m.movie_year, m.director, \n" +
                    "      r.rating, starss.star_names as names, genres\n" +
                    "       from movies as m JOIN(\n" +
                    "        SELECT sm.movieId, \n" +
                    "      GROUP_CONCAT(s.name order by s.movieCnt desc, s.name) as star_names, \n" +
                    "      GROUP_CONCAT(s.id order by s.movieCnt desc, s.name) as star_ids FROM stars_in_movies AS sm \n" +
                    "        INNER JOIN  (\n" +
                    "          select s.id, s.name,count(*) as movieCnt \n" +
                    "          from stars_in_movies smtemp, stars s \n" +
                    "          where smtemp.starId = s.id GROUP by s.id \n" +
                    "          ) as s on s.id = sm.starId group by sm.movieId) as starss on starss.movieId=m.id \n" +
                    "\t\tLEFT JOIN ratings r on r.movieId = m.id \n" +
                    "        INNER JOIN (\n" +
                    "          SELECT gm.movieId, GROUP_CONCAT(g.name order by g.name asc) as genres from genres_in_movies gm \n" +
                    "          INNER JOIN genres g ON g.id = gm.genreId GROUP BY movieId\n" +
                    "        ) as gtemp ON gtemp.movieId = m.id " +
                    "WHERE m.title = ?" +
                    "GROUP BY m.id\n" +
                    "ORDER BY r.rating DESC LIMIT 10";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {

//                String starIds = rs.getString("star_ids");
                String starNames = rs.getString("names");
//                String starDob = rs.getString("birthYear");

                String movieId = rs.getString("id");
                String movieTitle = rs.getString("title");
                String movieYear = rs.getString("movie_year");
                String movieDirector = rs.getString("director");
                String genres = rs.getString("genres");
                String rating = rs.getString("rating");

                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
//                jsonObject.addProperty("star_ids", starIds);
                jsonObject.addProperty("star_names", starNames);
//                jsonObject.addProperty("star_dob", starDob);
                jsonObject.addProperty("movie_id", movieId);
                jsonObject.addProperty("movie_title", movieTitle);
                jsonObject.addProperty("movie_year", movieYear);
                jsonObject.addProperty("movie_director", movieDirector);
                jsonObject.addProperty("genres", genres);
                jsonObject.addProperty("rating", rating);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            out.write(jsonArray.toString());
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            request.getServletContext().log("Error:", e);
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("user");
        ArrayList<String> previousItems = user.getCart();
        ArrayList<String> idList = user.getMovieIds();
        String title = request.getParameter("id");
        String movie_id = "";

        synchronized (previousItems) {
            previousItems.add(title);
        }

        JsonObject responseJsonObject = new JsonObject();

        try (Connection conn = dataSource.getConnection()) {
            Statement statement = conn.createStatement();

            String query = "SELECT * FROM movies WHERE title = '" + title + "'"; // not sure why this query does not work with "where user"

            ResultSet rs = statement.executeQuery(query);

            while(rs.next()) {
                movie_id = rs.getString("id");
            }

            synchronized (previousItems) {
                idList.add(movie_id);
            }

            System.out.println(movie_id);

            response.setStatus(200);

            rs.close();
            statement.close();

        } catch (Exception e) {
            responseJsonObject.addProperty("errorMessage", e.getMessage());
            response.setStatus(500);

        } finally {}

        JsonArray previousItemsJsonArray = new JsonArray();
        previousItems.forEach(previousItemsJsonArray::add);
        responseJsonObject.add("previousItems", previousItemsJsonArray);

        response.getWriter().write(responseJsonObject.toString());
    }
}

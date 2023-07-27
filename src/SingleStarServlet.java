import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/single-star")
public class SingleStarServlet extends HttpServlet {
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

    public void doIt() {}

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
            String query = "SELECT s.*, GROUP_CONCAT(m.title order by movie_year DESC, title ASC) as movie_titles\n" +
                    "from stars as s, stars_in_movies as sim, movies as m \n" +
                    "where m.id = sim.movieId and sim.starId = s.id and s.name = ?";
//                    "SELECT m.*, r.rating, GROUP_CONCAT(t.name) AS names, GROUP_CONCAT(DISTINCT e.name) AS genres\n" +
//                    "FROM movies m INNER JOIN ratings r ON r.movieId = m.id\n" +
//                    "INNER JOIN genres_in_movies g ON g.movieId = m.id\n" +
//                    "INNER JOIN genres e ON e.id = g.genreId\n" +
//                    "INNER JOIN stars_in_movies s ON s.movieId = m.id\n" +
//                    "INNER JOIN stars t ON t.id = s.starId\n" +
//                    "WHERE m.id = ?" +
//                    "GROUP BY m.id\n" +
//                    "ORDER BY r.rating DESC LIMIT 20";
//

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

//                String starId = rs.getString("starId");
                String starName = rs.getString("name");
//                String starDob = rs.getString("birthYear");

//                String movieIds = rs.getString("movie_ids");
                String movieTitles = rs.getString("movie_titles");
                String birthYear = rs.getString("birthYear");

//                String movieDirector = rs.getString("director");
//                String genres = rs.getString("genres");
//                String rating = rs.getString("rating");

                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
//                jsonObject.addProperty("star_id", starId);
                jsonObject.addProperty("star_name", starName);
//                jsonObject.addProperty("star_dob", starDob);
//                jsonObject.addProperty("movie_ids", movieIds);
                jsonObject.addProperty("movie_titles", movieTitles);
                jsonObject.addProperty("birth_year", birthYear);
//                jsonObject.addProperty("movie_director", movieDirector);
//                jsonObject.addProperty("genres", genres);
//                jsonObject.addProperty("rating", rating);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost single_instance_log1
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }

}

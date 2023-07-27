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

import java.sql.*;

@WebServlet(name = "InsertMovieServlet", urlPatterns = "/api/insertMovie")
public class InsertMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb-master");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String title = request.getParameter("title");
        String releaseYear = request.getParameter("release_year");
        String director = request.getParameter("director");
        String starName = request.getParameter("star_name");
        String genreName = request.getParameter("genre");

        String query = "{call add_movie(?, ?, ?, ?, ?, ?)}";

        try (Connection conn = dataSource.getConnection();
             CallableStatement statement = conn.prepareCall(query)) {
             JsonObject jsonObject = new JsonObject();

             statement.setString(1, title);
             statement.setInt(2, Integer.parseInt(releaseYear));
             statement.setString(3, director);
             statement.setString(4, starName);
             statement.setString(5, genreName);
             statement.registerOutParameter(6, Types.VARCHAR);


             statement.execute();
             String message = statement.getString(6);
             jsonObject.addProperty("message", message);


             statement.close();
             response.setStatus(200);
             response.getWriter().write(jsonObject.toString());
        } catch (Exception e) {
            // write error message JSON object to output
             JsonObject jsonObject = new JsonObject();
             jsonObject.addProperty("errorMessage", e.getMessage());
             System.out.println(e.getMessage());

            // set response status to 500 (Internal Server Error)
             response.setStatus(500);
             response.getWriter().write(jsonObject.toString());
        }
    }
}
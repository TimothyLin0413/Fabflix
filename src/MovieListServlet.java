import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;


// Declaring a WebServlet called MovieListServlet, which maps to url "/api/stars"
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movielist")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;
    private String context;

    public void init(ServletConfig config) {
        try {
            super.init(config);
        } catch (Exception e) { }
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
            context = getServletContext().getRealPath("/");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public String getQuery(String[] params, String sort, String limit, String offset) {
        String query = "SELECT m.id, m.title, m.movie_year, m.director,\n" +
                "      starss.star_names as names, genres, r.rating\n" +
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
                "        ) as gtemp ON gtemp.movieId = m.id ";

        //String sort = "DESrating";
        //String limit = "10";
        if (params.length == 3) { // browse
            String browse_type = params[1];
            String name = params[2];

            if (browse_type.equals("title")) { // movie title
                query += "WHERE m.title LIKE '" + name + "%' ";
            } else { // genre name
                query += "WHERE genres LIKE '%" + name + "%' ";
            }


        } else if (params.length == 4) { // search
            String title = params[0];
            String year = params[1];
            String director = params[2];
            String star = params[3];
            int count = countNotEmpty(title,year,director,star);

            if (count > 0) {
                query += "WHERE ";
                if (title.equals(" ")) {
                    query += "m.title LIKE '%'";
                } else {
                    query += "m.title LIKE '%" + title + "%'";
                }


                if (!year.equals(" ")) {
                    query += " and m.movie_year = " + year;
                }


                if (director.equals(" ")) {
                    query += " and m.director LIKE '%'";
                } else {
                    query += " and m.director LIKE '%" + director + "%'";
                }


                if (star.equals(" ")) {
                    query += " and star_names LIKE '%'";
                } else {
                    query += " and star_names LIKE '%" + star + "%'";
                }
            }
            else { query += "WHERE m.title LIKE '%' and director LIKE '%' and star_names LIKE '%'"; }
        }
        query += "GROUP BY m.id\n";

        if (sort.equals("DESratingASC")){
            query += "ORDER BY r.rating DESC, m.title ASC";
        }
        else if (sort.equals("DESratingDES")){
            query += "ORDER BY r.rating DESC, m.title DESC";
        }
        else if (sort.equals("ASCratingASC")){
            query += "ORDER BY r.rating ASC, m.title ASC";
        }
        else if (sort.equals("ASCratingDES")){
            query += "ORDER BY r.rating ASC, m.title DESC";
        }
        else if (sort.equals("DEStitleASC")){
            query += "ORDER BY m.title DESC, r.rating ASC";
        }
        else if (sort.equals("DEStitleDES")){
            query += "ORDER BY m.title DESC, r.rating DESC";
        }
        else if (sort.equals("ASCtitleASC")){
            query += "ORDER BY m.title ASC, r.rating ASC";
        }
        else{
            query += "ORDER BY m.title ASC, r.rating DESC";
        }
        query += " LIMIT " + limit;
        Integer o = (Integer.parseInt(limit) * Integer.parseInt(offset));
        query += " OFFSET " + o.toString() + ";";
        return query;
    }

    private int countNotEmpty(String title, String year, String director, String star) {
        int count = 0;
        if (!title.isEmpty()) { count++; }
        if (!year.isEmpty()) { count++; }
        if (!director.isEmpty()) { count++; }
        if (!star.isEmpty()) { count++; }
        return count;
    }

    public void createLog() {
        try {
            String filePath = context + "\\single_instance_log1";
            System.out.println(filePath);
            File file = new File(filePath);
            file.createNewFile();
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        createLog();
        long tsStart, tsEnd, tjStart, tjEnd, ts, tj = 0;
        tsStart = System.nanoTime();

        response.setContentType("application/json"); // Response mime type
        String id = request.getParameter("id"); // change this according to timo
        String[] params = id.split("-");
        String query = "";

        String sort = request.getParameter("sorting");
        String sortnum = request.getParameter("limit");
        String jump = request.getParameter("jump");
        String offset = request.getParameter("offset");

        if (id.isEmpty()) {
            query = "WITH Top20Ratings AS (\n" +
                    "  SELECT\n" +
                    "    movieId,\n" +
                    "    rating\n" +
                    "FROM ratings\n" +
                    "ORDER BY rating DESC\n" +
                    "LIMIT 20)\n" +
                    "SELECT\n" +
                    "    M.id as id,\n" +
                    "    title,\n" +
                    "    movie_year,\n" +
                    "    director,\n" +
                    "    rating,\n" +
                    "    GROUP_CONCAT(DISTINCT S.id SEPARATOR \",\") as star_ids,\n" +
                    "    GROUP_CONCAT(DISTINCT S.name SEPARATOR \",\") as names,\n" +
                    "    GROUP_CONCAT(DISTINCT G.name SEPARATOR \",\") as genres\n" +
                    "FROM\n" +
                    "    Top20Ratings AS R,\n" +
                    "    movies as M,\n" +
                    "   stars as S,\n" +
                    "   stars_in_movies as SM,\n" +
                    "   genres as G,\n" +
                    "    genres_in_movies as GM\n" +
                    "WHERE\n" +
                    "   M.id = R.movieId\n" +
                    "   AND M.id = SM.movieId\n" +
                    "   AND S.id = SM.starId\n" +
                    "   AND GM.movieId = M.id\n" +
                    "   AND GM.genreId = G.id\n" +
                    "GROUP BY M.title \n" +
                    "ORDER BY R.rating DESC \n" +
                    "LIMIT 10;";
        } else {
            query = getQuery(params, sort, sortnum, offset);
        }

        PrintWriter out = response.getWriter();

        tjStart = System.nanoTime();
        try (Connection conn = dataSource.getConnection()) {
            tjEnd = System.nanoTime();
            tj += tjEnd - tjStart;
            System.out.println(tj);

            PreparedStatement statement = conn.prepareStatement(query);

            tjStart = System.nanoTime();
            ResultSet rs = statement.executeQuery(query);
            tjEnd = System.nanoTime();
            tj += tjEnd - tjStart;
            System.out.println(tj);

            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                String mov_id = rs.getString("id");
                String mov_title = rs.getString("title");
                String mov_year = rs.getString("movie_year");
                String mov_dir = rs.getString("director");
                String mov_rat = rs.getString("rating");
                String star_names = rs.getString("names");
                String mov_genres = rs.getString("genres");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("mov_id", mov_id);
                jsonObject.addProperty("mov_title", mov_title);
                jsonObject.addProperty("mov_year", mov_year);
                jsonObject.addProperty("mov_dir", mov_dir);
                jsonObject.addProperty("mov_rat", mov_rat);
                jsonObject.addProperty("star_names", star_names);
                jsonObject.addProperty("mov_genres", mov_genres);
                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }
        tsEnd = System.nanoTime();
        ts = tsEnd - tsStart;
        try {
            FileWriter myWriter = new FileWriter(context + "\\single_instance_log1", true);
            myWriter.write("tj=" + String.valueOf(tj) + "\n");
            myWriter.write("ts=" + String.valueOf(ts) + "\n");
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}

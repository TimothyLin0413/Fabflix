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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 3L; // what does this do

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException { // change this
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("user");
        JsonObject responseJsonObject = new JsonObject();

        ArrayList<String> previousItems = user.getCart();

        JsonArray previousItemsJsonArray = new JsonArray();
        previousItems.forEach(previousItemsJsonArray::add);
        responseJsonObject.add("previousItems", previousItemsJsonArray);

        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String cardnumber = request.getParameter("cardnumber");
        String expiration = request.getParameter("expiration");

        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("user");
        ArrayList<String> previousItems = user.getCart();
        ArrayList<String> movieIds = user.getMovieIds();
        int user_id = user.getId();
        JsonObject responseJsonObject = new JsonObject();

        String f_name = "";
        String l_name = "";
        String card_num = "";
        Date expire = new Date();

        try (Connection conn = dataSource.getConnection()) {
            //Statement statement = conn.createStatement();

            String query = "SELECT * FROM creditcards WHERE id = '" + cardnumber + "'";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery(query);

            while(rs.next()) {
                f_name = rs.getString("firstName");
                l_name = rs.getString("lastName");
                card_num = rs.getString("id");
                expire = rs.getDate("expiration");
            }

            if (!card_num.isEmpty() && f_name.equals(firstname) && l_name.equals(lastname) && card_num.equals(cardnumber) && expire.toString().equals(expiration)) {

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
                java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
                for (int i = 0; i < previousItems.size(); i++) {
                    query = "INSERT INTO sales (customerId, movieId, saleDate) VALUES (" + user_id + ", '" + movieIds.get(i) + "', '" + currentDate + "');";
                    statement.executeUpdate(query);
                }

            } else {
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost single_instance_log1
                request.getServletContext().log("Login failed");
                // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
                responseJsonObject.addProperty("message", "Invalid Information");
            }

            response.setStatus(200);

            rs.close();
            statement.close();

        } catch (Exception e) {
            responseJsonObject.addProperty("errorMessage", e.getMessage());
            response.setStatus(500);

        } finally {}

        response.getWriter().write(responseJsonObject.toString());
    }
}
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet(name = "SaleConfirmationServlet", urlPatterns = "/api/saleconfirmation")
public class SaleConfirmationServlet extends HttpServlet {
    private static final long serialVersionUID = 3L; // what does this do

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb-master");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("user");
        JsonObject responseJsonObject = new JsonObject();

        ArrayList<String> previousItems = user.getCart();
        ArrayList<Integer> sales_id = new ArrayList<Integer>();

        try (Connection conn = dataSource.getConnection()) {

            String query = "SELECT * FROM sales WHERE customerId = " + user.getId();
            PreparedStatement statement = conn.prepareStatement(query);

            ResultSet rs = statement.executeQuery(query);

            while(rs.next()) {
                sales_id.add(rs.getInt("id"));
            }

            response.setStatus(200);

            rs.close();
            statement.close();

        } catch (Exception e) {
            responseJsonObject.addProperty("errorMessage", e.getMessage());
            response.setStatus(500);

        } finally {}

        int numMovies = previousItems.size();
        List<Integer> recentSales = sales_id.subList(sales_id.size()-numMovies, sales_id.size());
        JsonArray previousItemsJsonArray = new JsonArray();
        JsonArray salesJsonArray = new JsonArray();
        previousItems.forEach(previousItemsJsonArray::add);
        recentSales.forEach(salesJsonArray::add);
        responseJsonObject.add("previousItems", previousItemsJsonArray);
        responseJsonObject.add("sales", salesJsonArray);

        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());
    }
}

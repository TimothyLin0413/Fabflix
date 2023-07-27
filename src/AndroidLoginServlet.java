import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jasypt.util.password.StrongPasswordEncryptor;

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
import java.sql.Statement;

@WebServlet(name = "AndroidLoginServlet", urlPatterns = "/api/android-login")
public class AndroidLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 3L; // what does this do

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String user = request.getParameter("username");
        String pass = request.getParameter("password");
        JsonObject responseJsonObject = new JsonObject();

        String query_pass = "";
        int cust_id = 0;

        try (Connection conn = dataSource.getConnection()) {
            //Statement statement = conn.createStatement();

            String query = "SELECT * FROM customers WHERE email = '" + user + "'";

            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery(query);
            boolean success = false;

            while(rs.next()) {
                query_pass = rs.getString("password");
                cust_id = rs.getInt("id");
                success = new StrongPasswordEncryptor().checkPassword(pass, query_pass);
            }

            if (success) {

                request.getSession().setAttribute("user", new User(cust_id));

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

            } else {
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost single_instance_log1
                request.getServletContext().log("Login failed");
                // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
                if (query_pass.isEmpty()) {
                    responseJsonObject.addProperty("message", "Invalid Username");
                } else {
                    responseJsonObject.addProperty("message", "Incorrect Password");
                }
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
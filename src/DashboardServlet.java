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
import java.sql.Statement;

@WebServlet(name = "DashboardServlet", urlPatterns = "/api/dashboard_login")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 3L; // what does this do

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public void verifyRecaptcha(HttpServletRequest request) {

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String pass = request.getParameter("password");
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        JsonObject responseJsonObject = new JsonObject();

        try {
            RecaptchaVerify.verify(gRecaptchaResponse);
        } catch (Exception e) {
            responseJsonObject.addProperty("message", e.getMessage());
            response.getWriter().write(responseJsonObject.toString());
            return;
        }

        String query_email = "";
        String query_pass = "";

        try (Connection conn = dataSource.getConnection()) {


            String query = "SELECT * FROM employees WHERE email = '" + email + "'";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery(query);

            while(rs.next()) {
                query_email = rs.getString("email");
                query_pass = rs.getString("password");
            }

            if (!email.isEmpty() && !pass.isEmpty() && email.equals(query_email) && pass.equals(query_pass)) {

                request.getSession().setAttribute("employee", email);

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

            } else {
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                request.getServletContext().log("Login failed");

                if (query_pass.isEmpty()) {
                    responseJsonObject.addProperty("message", "Invalid Employee Email");
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
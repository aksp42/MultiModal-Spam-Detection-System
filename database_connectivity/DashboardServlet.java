import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/DashboardServlet")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String URL = "jdbc:mysql://localhost:3306/SPAM_DETECTOR";
    private static final String USER = "root";
    private static final String PASS = "Pass1224@";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String query = "SELECT total_scanned, spam_detected, ham_detected, suspicious_cases, avg_confidence FROM dashboard_stats WHERE id = 1";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection con = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement pst = con.prepareStatement(query);
                 ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {
                    int total = rs.getInt("total_scanned");
                    int spam = rs.getInt("spam_detected");
                    int ham = rs.getInt("ham_detected");
                    int suspicious = rs.getInt("suspicious_cases");
                    double avgConf = rs.getDouble("avg_confidence");

                    String jsonResponse = String.format(
                        "{\"total_scanned\": %d, \"spam_detected\": %d, \"ham_detected\": %d, \"suspicious_cases\": %d, \"avg_confidence\": %.2f}", 
                        total, spam, ham, suspicious, avgConf
                    );
                    
                    out.print(jsonResponse);
                } else {
                    out.print("{\"total_scanned\": 0, \"spam_detected\": 0, \"ham_detected\": 0, \"suspicious_cases\": 0, \"avg_confidence\": 0.0}");
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
        } finally {
            out.flush();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
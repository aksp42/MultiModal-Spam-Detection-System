import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/AnalyzeServlet")
@MultipartConfig
public class AnalyzeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. Get Text Input Safely
        String text = "";
        Part textPart = request.getPart("text");
        if (textPart != null) {
            try (Scanner s = new Scanner(textPart.getInputStream()).useDelimiter("\\A")) {
                text = s.hasNext() ? s.next() : "";
            }
        }

        // 2. Prepare Multi-part Request for Flask
        Part filePart = request.getPart("image");
        String boundary = "---" + System.currentTimeMillis();
        StringBuilder flaskResponse = new StringBuilder();

        try {
            // Flask Connection (Running on Port 5001)
            URL url = new URL("http://localhost:5001/predict");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            try (OutputStream out = conn.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true)) {

                // Send Text Part
                writer.println("--" + boundary);
                writer.println("Content-Disposition: form-data; name=\"text\"");
                writer.println();
                writer.println(text);

                // Send Image Part (If exists)
                if (filePart != null && filePart.getSize() > 0) {
                    writer.println("--" + boundary);
                    writer.println("Content-Disposition: form-data; name=\"image\"; filename=\"" + filePart.getSubmittedFileName() + "\"");
                    writer.println("Content-Type: " + filePart.getContentType());
                    writer.println();
                    writer.flush();
                    filePart.getInputStream().transferTo(out);
                    out.flush();
                    writer.println();
                }
                writer.println("--" + boundary + "--");
            }

            // 3. Read Response from Flask
            int responseCode = conn.getResponseCode();
            InputStream is = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();
            
            try (Scanner sc = new Scanner(is).useDelimiter("\\A")) {
                if (sc.hasNext()) flaskResponse.append(sc.next());
            }

            String finalJson = flaskResponse.toString();
            
            // 4. Send JSON back to Frontend (For UI update)
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print(finalJson);

            // 5. AUTOMATION LOGIC: DATABASE UPDATE
            if (responseCode == 200 && !finalJson.isEmpty()) {
                System.out.println("DEBUG: Raw JSON -> " + finalJson);
                try {
                    String decision = "HAM"; 
                    
                    /* PRIORITY LOGIC: 
                       Kyunki SUSPICIOUS ke andar bhi "SPAM" text hota hai, 
                       isliye hum pehle exact "final_decision": "SUSPICIOUS" dhoondenge.
                    */
                    if (finalJson.contains("\"final_decision\": \"SUSPICIOUS\"") || finalJson.contains("\"SUSPICIOUS\"")) {
                        decision = "SUSPICIOUS";
                    } 
                    else if (finalJson.contains("\"final_decision\": \"SPAM\"") || finalJson.contains("\"SPAM\"")) {
                        decision = "SPAM";
                    } 
                    else {
                        decision = "HAM";
                    }

                    // Confidence Parsing (Robust Regex to handle spaces/newlines)
                    double tConf = 0.0;
                    if (finalJson.contains("text_confidence")) {
                        String[] parts = finalJson.split("\"text_confidence\"\\s*:\\s*");
                        if (parts.length > 1) {
                            // Extract number before the next comma or bracket
                            String val = parts[1].split(",")[0].replace("}", "").replace("\"", "").trim();
                            tConf = Double.parseDouble(val);
                        }
                    }

                    // Call DatabaseManager to Update MySQL
                    System.out.println("DEBUG: Final Decision -> " + decision + " | Conf -> " + tConf);
                    DatabaseManager.updateStats(decision, tConf);
                    System.out.println("✅ Database Updated Successfully via Automation!");

                } catch (Exception parseEx) {
                    System.err.println("❌ Automation Error (Parsing): " + parseEx.getMessage());
                }
            }

        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().print("{\"error\":\"Connectivity issue: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
}
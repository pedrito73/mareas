import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class TideTable {
    private static final String API_KEY = "DEMO"; // Replace with your actual API key if needed

    public static void main(String[] args) {
        String response = getTideData();
        String[][] data = parseTideData(response);
        showTable(data);
    }

    private static String getTideData() {
        try {
            String urlString = String.format("https://www.worldtides.info/api/v2?extremes&lat=37.0902&lon=-8.2476&key=%s", API_KEY);
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();
            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String[][] parseTideData(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray extremes = jsonResponse.getJSONArray("extremes");
            int length = extremes.length();
            String[][] data = new String[length / 2][5]; // Assuming two entries per day

            for (int i = 0; i < length; i++) {
                JSONObject tide = extremes.getJSONObject(i);
                String date = tide.getString("date");
                String height = String. valueOf(tide.get("height"));
                String type = tide.getString("type");

                int row = i / 2;
                int col = type.equals("High") ? 1 : 2;
                data[row][0] = date.split("T")[0]; // Extracting the date part
                data[row][col] = date.split("T")[1] + " (" + height + "m)"; // Time and height

                if (col == 2 && data[row][1] == null) {
                    // If there's no high tide entry yet, set it to blank to maintain structure
                    data[row][1] = "";
                }
            }

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return new String[0][0];
        }
    }

    private static void showTable(String[][] data) {
        String[] columnNames = {"Date", "High Tide 1", "Low Tide 1", "High Tide 2", "Low Tide 2"};
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JFrame frame = new JFrame("Tide Table for Olhos de Agua");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(scrollPane);
        frame.setSize(600, 300);
        frame.setVisible(true);
    }
}
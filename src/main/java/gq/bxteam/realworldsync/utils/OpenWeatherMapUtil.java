package gq.bxteam.realworldsync.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.naming.ConfigurationException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

public class OpenWeatherMapUtil {
    public static boolean isRaining = false, isThunder = false;
    public OpenWeatherMapUtil(String API, String lat, String lon) throws IOException, ConfigurationException, ParseException {
        URL url = new URL(String.format("https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s", lat, lon, API));

        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.connect();
        int response = httpURLConnection.getResponseCode();
        if (response > 499) {
            throw new ProtocolException("Server/client error (HTTP error " + response + ")");
        } else if (response > 399) {
            if (response == 401) {
                throw new ConfigurationException("There was a problem getting the weather data: API key is invalid");
            } else {
                throw new ProtocolException("There was a problem getting the weather data: Unknown error...");
            }
        }

        Scanner scanner = new Scanner(url.openStream());
        StringBuilder data = new StringBuilder();
        while (scanner.hasNext()) {
            data.append(scanner.nextLine());
        }
        scanner.close();

        JSONArray conditions = (JSONArray) ((JSONObject) new JSONParser().parse(data.toString())).get("weather");

        for (Object rawCondition : conditions) {
            int id = Integer.parseInt(String.valueOf(((JSONObject) rawCondition).get("id")));

            while (id >= 10)
                id /= 10;

            if (!isRaining) {
                isRaining = id == 2 || id == 3 || id == 5 || id == 6;
            } if (!isThunder) {
                isThunder = id == 2;
            }
        }
    }

    public static boolean isRaining() {
        return isRaining;
    }

    public static boolean isThunder() {
        return isThunder;
    }
}

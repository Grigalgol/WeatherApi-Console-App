package org.example;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Application uses wetherstack api
 */
public class App {

    private static HttpURLConnection connection;

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the city name: ");

        String cityName = bufferedReader.readLine().trim();
        bufferedReader.close();
        String urlString = "http://api.weatherstack.com/current?access_key=ae31e1c978c11fc8ce6102bb0239afbd&query=";

        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();
        try {
            URL url = new URL(urlString+cityName);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();
            //System.out.println(status);

            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            }

            System.out.println(parse(responseContent.toString()));

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            connection.disconnect();
        }


    }

    public static String parse(String responseBody) {
        JSONObject obj = new JSONObject(responseBody);
        try {
            String name = obj.getJSONObject("location").getString("name");
            String country = obj.getJSONObject("location").getString("country");
            int temperature = obj.getJSONObject("current").getInt("temperature");
            int windSpeed = obj.getJSONObject("current").getInt("wind_speed") * 5 / 18;
            int temperatureFeelslLike = obj.getJSONObject("current").getInt("feelslike");

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder
                    .append("City name: ")
                    .append(name + ", " + country).append("\n")
                    .append("Current temperature: ")
                    .append(temperature +"°С")
                    .append(" (Feels like " + temperatureFeelslLike + "°С)")
                    .append("\n")
                    .append("WindSpeed: ")
                    .append(windSpeed)
                    .append(" m/s");
            return stringBuilder.toString();
        }
        catch (JSONException e) {
            return "Sorry, this city is not exists";
        }


    }

}

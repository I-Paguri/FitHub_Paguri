package it.uniba.dib.sms232417.fithub.athleteFragments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ManageJson {
        private static final String API_KEY = "97f69511113f9b69bdba905e6f9c3315e9c6eec0e678136469e2c057d793cd9c";

        public CompletableFuture<JSONObject> performMapsQuery(Map<String, String> parameters) {
                return CompletableFuture.supplyAsync(() -> {
                        StringBuilder urlBuilder = new StringBuilder("https://serpapi.com/search.json?");
                        for (Map.Entry<String, String> entry : parameters.entrySet())
                                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                        String url = urlBuilder.toString();

                        StringBuilder response = new StringBuilder();
                        try {
                                URL apiUrl = new URL(url);
                                HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
                                connection.setRequestMethod("GET");

                                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                                        String line;
                                        while ((line = reader.readLine()) != null) {
                                                response.append(line);
                                        }
                                }
                                connection.disconnect();

                                return new JSONObject(response.toString());
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                        return new JSONObject();
                });
        }

        //metodo che serve per gestire la risposta json e restituire una stringa

        public String handleJsonResponse(JSONObject response) {
                StringBuilder results = new StringBuilder();
                try {
                        // Handle local results
                        JSONArray localResults = response.getJSONArray("local_results");
                        for (int i = 0; i < localResults.length(); i++) {
                                JSONObject localResult = localResults.getJSONObject(i);
                                String title = localResult.getString("title");
                                results.append(title).append("\n");
                                // Continue extracting other fields of interest...
                        }

                        // Handle place results
                        JSONObject placeResults = response.getJSONObject("place_results");
                        String title = placeResults.getString("title");
                        results.append(title).append("\n");
                        // Continue extracting other fields of interest...

                } catch (JSONException e) {
                        e.printStackTrace();
                }
                String resultString = results.toString();
                System.out.println("Results: " + resultString); // Debug print
                return resultString;
        }


}
package com.example.imagedemo.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

@Component
public class addressUsingKey {
    @Value("${google.api.key}")
    private String googleApiKey;

    public String getAddressFromPin(String pinCode) {
        try {
            String baseUrl = "https://maps.googleapis.com/maps/api/geocode/json";

            String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .queryParam("address", pinCode + ", India")
                    .queryParam("key", googleApiKey)
                    .toUriString();

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            System.out.println("Google maps api response " + response);
            JSONObject json = new JSONObject(response);
            JSONArray results = json.getJSONArray("results");

            if (results.length() > 0) {
                return results.getJSONObject(0).getString("formatted_address");
            } else {
                return "No address found for this pin";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching address";
        }
    }

}

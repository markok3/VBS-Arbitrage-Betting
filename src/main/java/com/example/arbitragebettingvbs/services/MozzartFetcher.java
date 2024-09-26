package com.example.arbitragebettingvbs.services;

import com.example.arbitragebettingvbs.abstractClasses.BettingSitesFetcher;
import com.example.arbitragebettingvbs.entities.Match;
import com.example.arbitragebettingvbs.mappers.MozzartMapper;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;


@Service
@AllArgsConstructor
public class MozzartFetcher extends BettingSitesFetcher {

    private static final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final MozzartMapper mozzartMapper;
    @Override
    public List<Match> getMatches() throws URISyntaxException, InterruptedException, IOException {
        // Updated request body
        List<LinkedTreeMap<String, Object>> allItems = new ArrayList<>();
        int currentPage = 0;
        boolean hasMoreItems = true;

        // you can use hasItems to fetch while there are matches
        while (hasMoreItems) {
            // Updated request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("date", "all_days");
            requestBody.put("sort", "bycompetition");
            requestBody.put("currentPage", currentPage);
            requestBody.put("pageSize", 50);
            requestBody.put("sportId", 1);
            requestBody.put("competitionIds", new ArrayList<>());  // Empty list
            requestBody.put("search", "");
            requestBody.put("matchTypeId", 0);

            String jsonBody = gson.toJson(requestBody);
            String url = "https://www.mozzartbetonline.mk/betting/matches";

            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("accept", "application/json, text/plain, */*")
                    .header("accept-language", "mk,en-US;q=0.9,en;q=0.8")
                    .header("content-type", "application/json")
                    .header("cookie", "SERVERID=RHN2; i18next=mk")
                    .header("medium", "WEB")
                    .header("origin", "https://www.mozzartbetonline.mk")
                    .header("priority", "u=1, i")
                    .header("referer", "https://www.mozzartbetonline.mk/mk/betting/sport/1?date=today")
                    .header("sec-ch-ua", "\"Google Chrome\";v=\"129\", \"Not=A?Brand\";v=\"8\", \"Chromium\";v=\"129\"")
                    .header("sec-ch-ua-mobile", "?0")
                    .header("sec-ch-ua-platform", "\"Windows\"")
                    .header("sec-fetch-dest", "empty")
                    .header("sec-fetch-mode", "cors")
                    .header("sec-fetch-site", "same-origin")
                    .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Map<String, Object> responseData = gson.fromJson(response.body(), Map.class);
                List<LinkedTreeMap<String, Object>> items = (List<LinkedTreeMap<String, Object>>) responseData.get("items");

                if (items != null && !items.isEmpty()) {
                    allItems.addAll(items);
                    currentPage++;
                    System.out.println("Fetched page " + currentPage + " with " + items.size() + " items");
                } else {
                    hasMoreItems = false;
                    System.out.println("No more items to fetch. Total items: " + allItems.size());
                }
            } else {
                System.out.println("Unexpected status code: " + response.statusCode());
                System.out.println("Response body: " + response.body());
                hasMoreItems = false;
            }

            // Add a small delay to avoid overwhelming the server
            Thread.sleep(500);
        }

        return mozzartMapper.mapResponseToMatches((ArrayList<LinkedTreeMap<String, Object>>) allItems);
    }

    @Override
    public String serializeData(Object data) {
        return "";
    }
}

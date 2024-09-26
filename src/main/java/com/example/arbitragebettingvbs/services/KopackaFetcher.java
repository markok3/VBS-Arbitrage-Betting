package com.example.arbitragebettingvbs.services;

import com.example.arbitragebettingvbs.abstractClasses.BettingSitesFetcher;
import com.example.arbitragebettingvbs.entities.Match;
import com.example.arbitragebettingvbs.mappers.KopackaMapper;
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
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor

public class KopackaFetcher extends BettingSitesFetcher {

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final KopackaMapper mapper;

    @Override
    public List<Match> getMatches() throws URISyntaxException, IOException, InterruptedException {
        String url = "https://livebetapi.zlatnakopacka.mk/api/prematch";


        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(new URI(url))
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json, text/plain, */*")
                .header("Accept-Language", "mk,en-US;q=0.9,en;q=0.8")
                .header("Cache-Control", "no-cache")
                .header("Origin", "https://betting.zlatnakopacka.mk")
                .header("Pragma", "no-cache")
                .header("Priority", "u=1, i")
                .header("Referer", "https://betting.zlatnakopacka.mk/")
                .header("Sec-CH-UA", "\"Google Chrome\";v=\"129\", \"Not=A?Brand\";v=\"8\", \"Chromium\";v=\"129\"")
                .header("Sec-CH-UA-Mobile", "?0")
                .header("Sec-CH-UA-Platform", "\"Windows\"")
                .header("Sec-Fetch-Dest", "empty")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Site", "same-site")
                .header("UniqueID", "1cfe5cb6-159a-4cdb-8e86-b400ae1fa615")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36")
                .GET() // Adjust this based on your actual request body
                .build();

        HttpResponse<String> response = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        ArrayList<LinkedTreeMap<String, Object>> responseData = gson.fromJson(response.body(), ArrayList.class);

        return mapper.mapResponseToMatches(responseData);
    }



    @Override
    public String serializeData(Object data) {
        return "";
    }
}

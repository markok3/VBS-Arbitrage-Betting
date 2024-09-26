package com.example.arbitragebettingvbs.services;

import com.example.arbitragebettingvbs.abstractClasses.BettingSitesFetcher;
import com.example.arbitragebettingvbs.entities.Match;
import com.example.arbitragebettingvbs.mappers.SportLifeMapper;
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
public class SportLifeFetcher extends BettingSitesFetcher {

    private final SportLifeMapper mapper;
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @Override
    public List<Match> getMatches() throws URISyntaxException, InterruptedException, IOException {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("LigaID", new int[]{
                453, 454, 2489, 1626, 3253, 1, 326, 327, 328, 957, 958, 336, 337, 1791, 373, 374,
                432, 375, 376, 729, 1080, 350, 351, 352, 348, 349, 345, 338, 339, 2785, 1109, 342,
                419, 343, 346, 347, 361, 360, 388, 385, 439, 3190, 3822, 3093, 3270, 456, 2152,
                1802, 340, 586, 418, 1090, 677, 1008, 354, 355, 746, 402, 696, 386, 396, 397, 3168,
                400, 395, 444, 682, 401, 704, 449, 673, 648, 727, 382, 436, 857, 430, 370, 371, 1637,
                392, 570, 717, 367, 3359, 2025, 426, 362, 865, 672, 1077, 377, 335, 810, 747, 715, 358,
                899, 391, 389, 750, 1349, 398, 446, 1331, 670, 855, 378, 363, 364, 405, 394, 435, 381,
                621, 719, 334, 408, 399, 823, 434, 379, 1821, 716, 330, 331, 332, 333, 437, 383, 438,
                384, 577, 634, 683, 365, 366, 356, 357, 603, 833, 745, 393, 2496, 390, 666, 580, 853,
                344, 2239, 1811, 1787, 1779, 1806, 1347, 1805, 660
        });
        requestBody.put("filter", "0");
        requestBody.put("parId", 0);


        // Convert the map to a JSON string using Gson
        String jsonBody = gson.toJson(requestBody);


        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(new URI("https://sportlife.com.mk/Oblozuvanje.aspx/GetLiga"))
                .header("accept", "application/json, text/javascript, */*; q=0.01")
                .header("accept-language", "mk,en-US;q=0.9,en;q=0.8")
                .header("cache-control", "no-cache")
                .header("content-type", "application/json; charset=UTF-8")
                .header("cookie", "ASP.NET_SessionId=ny5ilgn13ti3dqj2wqqepir0; style=null; CookieAccept=foo")
                .header("origin", "https://sportlife.com.mk")
                .header("pragma", "no-cache")
                .header("priority", "u=1, i")
                .header("referer", "https://sportlife.com.mk/Oblozuvanje")
                .header("sec-ch-ua", "\"Google Chrome\";v=\"129\", \"Not=A?Brand\";v=\"8\", \"Chromium\";v=\"129\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-ch-ua-platform", "\"Windows\"")
                .header("sec-fetch-dest", "empty")
                .header("sec-fetch-mode", "cors")
                .header("sec-fetch-site", "same-origin")
                .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36")
                .header("x-requested-with", "XMLHttpRequest")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
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

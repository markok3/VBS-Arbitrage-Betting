package com.example.arbitragebettingvbs.mappers;

import com.example.arbitragebettingvbs.entities.Match;
import com.example.arbitragebettingvbs.entities.Odds;
import com.google.gson.internal.LinkedTreeMap;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class KopackaMapper {
    public List<Match> mapResponseToMatches(ArrayList<LinkedTreeMap<String, Object>> responseData) {

        List<LinkedTreeMap<String, Object>> filtered = responseData.stream()
                .filter(map -> map.containsKey("k1") && map.containsKey("k2") && map.containsKey("kx"))
                .filter(map -> (Double) map.get("sh_sport_id") == 1.0)
                .toList();

        return filtered.stream().map(this::getMatch).toList();
    }

    private Match getMatch(LinkedTreeMap<String, Object> match) {
        Match matchObject = new Match();

        matchObject.setHomeTeam((String) match.get("tim1"));
        matchObject.setAwayTeam((String) match.get("tim2"));
        setOdds(matchObject, match);
        matchObject.setMatchDateAndTime(getMatchDateAndTime((String) match.get("datum_vreme")));
        matchObject.setBettingCompany("KOPACKA");

        return matchObject;
    }

    private void setOdds(Match matchObject, LinkedTreeMap<String, Object> match) {
        Odds odds = new Odds();
        odds.setHomeWin((Double) match.get("k1"));
        odds.setDraw((Double) match.get("kx"));
        odds.setAwayWin((Double) match.get("k2"));
        matchObject.setOdds(odds);
    }

    public LocalDateTime getMatchDateAndTime(String dateTimeString) {
        // Define the format of the input string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return LocalDateTime.parse(dateTimeString, formatter);
    }

}

package com.example.arbitragebettingvbs.mappers;

import com.example.arbitragebettingvbs.entities.Match;
import com.example.arbitragebettingvbs.entities.Odds;
import com.google.gson.internal.LinkedTreeMap;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class SportLifeMapper {
    public List<Match> mapResponseToMatches(ArrayList<LinkedTreeMap<String, Object>> responseData) {
        List<Match> matches = new ArrayList<>();
        for (LinkedTreeMap<String, Object> map : responseData) {
            ArrayList<LinkedTreeMap<String, Object>> matchesList = (ArrayList<LinkedTreeMap<String, Object>>) map.get("P");
            for (LinkedTreeMap<String, Object> match : matchesList) {
                matches.add(this.getMatch(match));
            }
        }

        return matches;
    }

    private Match getMatch(LinkedTreeMap<String, Object> match) {
        ArrayList<LinkedTreeMap<String, Object>> bettingOptions = (ArrayList<LinkedTreeMap<String, Object>>) match.get("T");

        Match matchObject = new Match();

        setTeamNames(match, matchObject);
        setOdds(bettingOptions, matchObject);

        matchObject.setBettingCompany("SPORTLIFE");
        matchObject.setMatchDateAndTime(getMatchDateAndTime((String) match.get("DI")));

        return matchObject;
    }

    public LocalDateTime getMatchDateAndTime(String dateTimeString) {
        // Define the format of the input string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSXXX");

        // Parse the string to ZonedDateTime
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTimeString, formatter);

        // Convert ZonedDateTime to LocalDateTime
        return zonedDateTime.toLocalDateTime();
    }

    private void setOdds(ArrayList<LinkedTreeMap<String, Object>> bettingOptions, Match matchObject) {
        Odds odds = new Odds();
        Double homeWin = (Double) bettingOptions.get(0).get("K");
        Double draw = (Double) bettingOptions.get(1).get("K");
        Double awayWin = (Double) bettingOptions.get(2).get("K");

        odds.setHomeWin(homeWin);
        odds.setDraw(draw);
        odds.setAwayWin(awayWin);

        matchObject.setOdds(odds);
    }

    private void setTeamNames(LinkedTreeMap<String, Object> match, Match matchObject) {
        String matchName = (String) match.get("PN");

        if (matchName != null && matchName.contains(" : ")) {
            String[] teams = matchName.split(" : ");
            matchObject.setHomeTeam(teams[0].trim());
            matchObject.setAwayTeam(teams[1].trim());
        }
    }

}

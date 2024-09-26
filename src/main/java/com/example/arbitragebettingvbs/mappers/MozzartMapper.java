package com.example.arbitragebettingvbs.mappers;

import com.example.arbitragebettingvbs.entities.Match;
import com.example.arbitragebettingvbs.entities.Odds;
import com.google.gson.internal.LinkedTreeMap;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class MozzartMapper {
    public List<Match> mapResponseToMatches(ArrayList<LinkedTreeMap<String, Object>> responseData) {

        return responseData.stream().map(this::getMatch).toList();
    }

    private Match getMatch(LinkedTreeMap<String, Object> match) {
        Match matchObject = new Match();

        matchObject.setHomeTeam(getHomeName(match));
        matchObject.setAwayTeam(getAwayName(match));
        setOdds(matchObject, match);
        matchObject.setMatchDateAndTime(getMatchDateAndTime((Double) match.get("startTime")));
        matchObject.setBettingCompany("MOZZART");

        return matchObject;
    }

    private String getHomeName(LinkedTreeMap<String, Object> match){
        return (String) ((LinkedTreeMap<String,Object>)match.get("home")).get("name");
    }

    private String getAwayName(LinkedTreeMap<String, Object> match){
        return (String) ((LinkedTreeMap<String,Object>)match.get("visitor")).get("name");
    }

    private void setOdds(Match matchObject, LinkedTreeMap<String, Object> match) {
        Odds oddsObject = new Odds();
        ArrayList<LinkedTreeMap<String, Object>> odds = (ArrayList<LinkedTreeMap<String, Object>>) match.get("odds");

        Double homeWin = (Double) odds.get(0).get("value");
        Double draw = (Double) odds.get(1).get("value");
        Double awayWin = (Double) odds.get(2).get("value");

        oddsObject.setHomeWin(homeWin);
        oddsObject.setDraw(draw);
        oddsObject.setAwayWin(awayWin);

        matchObject.setOdds(oddsObject);
    }

    public LocalDateTime getMatchDateAndTime(Double time) {
        Instant instant = Instant.ofEpochSecond(time.longValue());
        // Convert the Instant to LocalDateTime in the system's default time zone
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}

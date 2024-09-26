package com.example.arbitragebettingvbs.services;

import com.example.arbitragebettingvbs.entities.BestMatchBetOffer;
import com.example.arbitragebettingvbs.entities.HighestBetOffering;
import com.example.arbitragebettingvbs.entities.Match;
import com.example.arbitragebettingvbs.enums.Bet;
import com.example.arbitragebettingvbs.services.interfaces.ArbitrageService;
import lombok.AllArgsConstructor;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Function;

@Service
@AllArgsConstructor
public class ArbitrageServiceImpl implements ArbitrageService {
    private final MozzartFetcher mozzartFetcher;
    private final KopackaFetcher kopackaFetcher;
    private final SportLifeFetcher sportLifeFetcher;

    @Override
    public List<BestMatchBetOffer> getHighestBetOfferings() throws URISyntaxException, IOException, InterruptedException {
        List<Match> mozzartMatches = mozzartFetcher.getMatches();
        List<Match> kopackaMatches = kopackaFetcher.getMatches();
        List<Match> sportLifeMatches = sportLifeFetcher.getMatches();

        List<Match> foundInKopacka = new ArrayList<>();
        List<Match> foundInSportLife = new ArrayList<>();

        List<BestMatchBetOffer> bestMatchBetOffers = new ArrayList<>();

        mozzartMatches.forEach(r -> {
            Match kopackaMatch = findMatchingMatchInList(r, kopackaMatches);
            Match sportLifeMatch = findMatchingMatchInList(r, sportLifeMatches);

            HighestBetOffering highestBetOfferingHome = getHighestBet(Bet.HOME_WIN, r, kopackaMatch, sportLifeMatch);
            HighestBetOffering highestBetOfferingDraw = getHighestBet(Bet.DRAW, r, kopackaMatch, sportLifeMatch);
            HighestBetOffering highestBetOfferingAway = getHighestBet(Bet.AWAY_WIN, r, kopackaMatch, sportLifeMatch);

            BestMatchBetOffer bestMatchBetOffer = new BestMatchBetOffer(
                    r.getHomeTeam(),
                    r.getAwayTeam(),
                    highestBetOfferingHome,
                    highestBetOfferingDraw,
                    highestBetOfferingAway
            );

            bestMatchBetOffers.add(bestMatchBetOffer);

        });


        bestMatchBetOffers.sort(Comparator.comparing(BestMatchBetOffer::returnPercentage));
        return bestMatchBetOffers;

    }

    private HighestBetOffering getHighestBet(Bet betType, Match mozzart, Match kopacka, Match sportLife) {
            List<Match> matches = Arrays.asList(mozzart, kopacka, sportLife);

        Function<Match, Double> oddsGetter = switch (betType) {
            case HOME_WIN -> match -> match.getOdds().getHomeWin();
            case DRAW -> match -> match.getOdds().getDraw();
            case AWAY_WIN -> match -> match.getOdds().getAwayWin();
        };

        Match bestOddsMatch = matches.stream()
                .max(Comparator.comparing(oddsGetter))
                .orElse(mozzart);

        HighestBetOffering highestBetOffering = new HighestBetOffering();
        highestBetOffering.setBet(betType);
        highestBetOffering.setBettingCompany(bestOddsMatch.getBettingCompany());
        highestBetOffering.setValue(oddsGetter.apply(bestOddsMatch));
        return  highestBetOffering;
    }


    private Match findMatchingMatchInList(Match match, List<Match> matches) {
        LevenshteinDistance levenshtein = new LevenshteinDistance();
        String inputCombinedNames = match.getCombinedNames();

        Match mostSimilarMatch = null;
        int smallestDistance = Integer.MAX_VALUE;

        for (Match m : matches) {
            String currentCombinedNames = m.getCombinedNames();
            int distance = levenshtein.apply(inputCombinedNames, currentCombinedNames);

            if (distance < smallestDistance) {
                smallestDistance = distance;
                mostSimilarMatch = m;
            }
        }

        int maxAllowedDistance = 4;  // Adjust this value based on your needs
        if (smallestDistance <= maxAllowedDistance) {
            return mostSimilarMatch;
        } else {
            return match;
        }
    }
}

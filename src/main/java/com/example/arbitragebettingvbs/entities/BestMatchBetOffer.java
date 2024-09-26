package com.example.arbitragebettingvbs.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;

@Data
public class BestMatchBetOffer {

    String homeTeam;
    String awayTeam;
    HighestBetOffering homeWin;
    HighestBetOffering draw;
    HighestBetOffering awayWin;
    Double returnPercentage;

    public BestMatchBetOffer(String homeTeam, String awayTeam, HighestBetOffering homeWin, HighestBetOffering draw, HighestBetOffering awayWin) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeWin = homeWin;
        this.draw = draw;
        this.awayWin = awayWin;
        this.returnPercentage = 1 / homeWin.value + 1 / awayWin.value + 1 / draw.value;
    }

    public Double returnPercentage() {
        return 1 / homeWin.value + 1 / awayWin.value + 1 / draw.value;
    }

    public static Comparator<BestMatchBetOffer> compareByPercentage() {
        return Comparator.comparing(BestMatchBetOffer::returnPercentage);
    }

}



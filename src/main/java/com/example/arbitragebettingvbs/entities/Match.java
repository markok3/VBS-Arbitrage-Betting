package com.example.arbitragebettingvbs.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Match {
    String homeTeam;
    String awayTeam;
    Odds odds;
    LocalDateTime matchDateAndTime;
    String bettingCompany;

    public String getCombinedNames() {
        return homeTeam.toLowerCase() + awayTeam.toLowerCase();
    }
}

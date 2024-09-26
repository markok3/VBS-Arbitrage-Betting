package com.example.arbitragebettingvbs.entities;

import com.example.arbitragebettingvbs.enums.Bet;
import lombok.Data;

@Data
public class HighestBetOffering {
    Bet bet;
    Double value;
    String bettingCompany;
}

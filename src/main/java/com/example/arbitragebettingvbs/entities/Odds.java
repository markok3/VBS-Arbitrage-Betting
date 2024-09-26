package com.example.arbitragebettingvbs.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Odds {
    Double homeWin;
    Double draw;
    Double awayWin;
}

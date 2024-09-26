package com.example.arbitragebettingvbs.services.interfaces;

import com.example.arbitragebettingvbs.entities.BestMatchBetOffer;
import com.example.arbitragebettingvbs.entities.HighestBetOffering;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface ArbitrageService {
    List<BestMatchBetOffer> getHighestBetOfferings() throws URISyntaxException, IOException, InterruptedException;
}

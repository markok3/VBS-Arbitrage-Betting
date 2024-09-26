package com.example.arbitragebettingvbs;

import com.example.arbitragebettingvbs.services.KopackaFetcher;
import com.example.arbitragebettingvbs.services.MozzartFetcher;
import com.example.arbitragebettingvbs.services.SportLifeFetcher;
import com.example.arbitragebettingvbs.services.interfaces.ArbitrageService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class Bootstrap implements CommandLineRunner {


    private final ArbitrageService arbitrageService;

    @Override
    public void run(String... args) throws Exception {
//        arbitrageService.getHighestBetOfferings();
    }
}
package com.example.arbitragebettingvbs.abstractClasses;

import com.example.arbitragebettingvbs.entities.Match;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public abstract class BettingSitesFetcher {
    public abstract List<Match> getMatches() throws URISyntaxException, InterruptedException, IOException;

    public abstract String serializeData(Object data);

    public String defaultSerialize(Object data) {
        // Simple JSON-like serialization for demo purposes
        return "Serialized Data: " + data.toString();
    }
}

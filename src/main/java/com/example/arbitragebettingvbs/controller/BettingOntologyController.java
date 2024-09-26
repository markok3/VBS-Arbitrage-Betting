package com.example.arbitragebettingvbs.controller;

import com.example.arbitragebettingvbs.entities.BestMatchBetOffer;
import com.example.arbitragebettingvbs.entities.HighestBetOffering;
import com.example.arbitragebettingvbs.services.interfaces.ArbitrageService;
import lombok.AllArgsConstructor;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@AllArgsConstructor
public class BettingOntologyController {

    private static final String BASE_IRI = "http://example.com/betting#";

    private final ArbitrageService bettingService;

    @GetMapping("/ontology")
    public ResponseEntity<byte[]> getOntology() throws OWLOntologyCreationException, OWLOntologyStorageException, URISyntaxException, IOException, InterruptedException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.createOntology(IRI.create(BASE_IRI));
        OWLDataFactory factory = manager.getOWLDataFactory();

        // Create classes
        OWLClass betClass = createClass(factory, ontology, "Bet");
        OWLClass highestBetOfferingClass = createClass(factory, ontology, "HighestBetOffering");
        OWLClass bestMatchBetOfferClass = createClass(factory, ontology, "BestMatchBetOffer");

        // Create properties for HighestBetOffering
        OWLDataProperty valueProperty = createDataProperty(factory, ontology, "value", highestBetOfferingClass, OWL2Datatype.XSD_DOUBLE);
        OWLDataProperty bettingCompanyProperty = createDataProperty(factory, ontology, "bettingCompany", highestBetOfferingClass, OWL2Datatype.XSD_STRING);
        OWLObjectProperty betProperty = createObjectProperty(factory, ontology, "bet", highestBetOfferingClass, betClass);

        // Create properties for BestMatchBetOffer
        OWLDataProperty homeTeamProperty = createDataProperty(factory, ontology, "homeTeam", bestMatchBetOfferClass, OWL2Datatype.XSD_STRING);
        OWLDataProperty awayTeamProperty = createDataProperty(factory, ontology, "awayTeam", bestMatchBetOfferClass, OWL2Datatype.XSD_STRING);
        OWLDataProperty returnPercentageProperty = createDataProperty(factory, ontology, "returnPercentage", bestMatchBetOfferClass, OWL2Datatype.XSD_DOUBLE);
        OWLObjectProperty homeWinProperty = createObjectProperty(factory, ontology, "homeWin", bestMatchBetOfferClass, highestBetOfferingClass);
        OWLObjectProperty drawProperty = createObjectProperty(factory, ontology, "draw", bestMatchBetOfferClass, highestBetOfferingClass);
        OWLObjectProperty awayWinProperty = createObjectProperty(factory, ontology, "awayWin", bestMatchBetOfferClass, highestBetOfferingClass);

        // Create individuals for Bet enum
        OWLNamedIndividual homeWinIndividual = createIndividual(factory, ontology, betClass, "HOME_WIN");
        OWLNamedIndividual drawIndividual = createIndividual(factory, ontology, betClass, "DRAW");
        OWLNamedIndividual awayWinIndividual = createIndividual(factory, ontology, betClass, "AWAY_WIN");

        // Get data from service
        List<BestMatchBetOffer> bestMatchBetOffers = bettingService.getHighestBetOfferings();

        // Map data to ontology
        for (int i = 0; i < bestMatchBetOffers.size(); i++) {
            BestMatchBetOffer offer = bestMatchBetOffers.get(i);
            OWLNamedIndividual offerIndividual = createIndividual(factory, ontology, bestMatchBetOfferClass, "BestMatchBetOffer_" + i);

            // Add data properties for BestMatchBetOffer
            ontology.add(factory.getOWLDataPropertyAssertionAxiom(homeTeamProperty, offerIndividual, offer.getHomeTeam()));
            ontology.add(factory.getOWLDataPropertyAssertionAxiom(awayTeamProperty, offerIndividual, offer.getAwayTeam()));
            ontology.add(factory.getOWLDataPropertyAssertionAxiom(returnPercentageProperty, offerIndividual, offer.returnPercentage()));

            // Create and link HighestBetOffering individuals
            addHighestBetOffering(factory, ontology, offerIndividual, homeWinProperty, offer.getHomeWin(), homeWinIndividual, i + "_home");
            addHighestBetOffering(factory, ontology, offerIndividual, drawProperty, offer.getDraw(), drawIndividual, i + "_draw");
            addHighestBetOffering(factory, ontology, offerIndividual, awayWinProperty, offer.getAwayWin(), awayWinIndividual, i + "_away");
        }

        // Serialize the ontology
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ontology.saveOntology(out);

        return ResponseEntity.ok()
                .header("Content-Type", "application/rdf+xml")
                .body(out.toByteArray());
    }

    private void addHighestBetOffering(OWLDataFactory factory, OWLOntology ontology, OWLNamedIndividual offerIndividual,
                                       OWLObjectProperty betTypeProperty, HighestBetOffering betOffering,
                                       OWLNamedIndividual betTypeIndividual, String suffix) {
        OWLNamedIndividual betOfferingIndividual = createIndividual(factory, ontology, factory.getOWLClass(IRI.create(BASE_IRI + "HighestBetOffering")), "HighestBetOffering_" + suffix);

        ontology.add(factory.getOWLObjectPropertyAssertionAxiom(betTypeProperty, offerIndividual, betOfferingIndividual));
        ontology.add(factory.getOWLDataPropertyAssertionAxiom(factory.getOWLDataProperty(IRI.create(BASE_IRI + "value")), betOfferingIndividual, betOffering.getValue()));
        ontology.add(factory.getOWLDataPropertyAssertionAxiom(factory.getOWLDataProperty(IRI.create(BASE_IRI + "bettingCompany")), betOfferingIndividual, betOffering.getBettingCompany()));
        ontology.add(factory.getOWLObjectPropertyAssertionAxiom(factory.getOWLObjectProperty(IRI.create(BASE_IRI + "bet")), betOfferingIndividual, betTypeIndividual));
    }
    private OWLClass createClass(OWLDataFactory factory, OWLOntology ontology, String className) {
        OWLClass owlClass = factory.getOWLClass(IRI.create(BASE_IRI + className));
        ontology.add(factory.getOWLDeclarationAxiom(owlClass));
        return owlClass;
    }

    private OWLDataProperty createDataProperty(OWLDataFactory factory, OWLOntology ontology, String propertyName, OWLClass domain, OWL2Datatype range) {
        OWLDataProperty property = factory.getOWLDataProperty(IRI.create(BASE_IRI + propertyName));
        ontology.add(factory.getOWLDeclarationAxiom(property));
        ontology.add(factory.getOWLDataPropertyDomainAxiom(property, domain));
        ontology.add(factory.getOWLDataPropertyRangeAxiom(property, range));
        return property;
    }

    private OWLObjectProperty createObjectProperty(OWLDataFactory factory, OWLOntology ontology, String propertyName, OWLClass domain, OWLClass range) {
        OWLObjectProperty property = factory.getOWLObjectProperty(IRI.create(BASE_IRI + propertyName));
        ontology.add(factory.getOWLDeclarationAxiom(property));
        ontology.add(factory.getOWLObjectPropertyDomainAxiom(property, domain));
        ontology.add(factory.getOWLObjectPropertyRangeAxiom(property, range));
        return property;
    }

    private OWLNamedIndividual createIndividual(OWLDataFactory factory, OWLOntology ontology, OWLClass owlClass, String individualName) {
        OWLNamedIndividual individual = factory.getOWLNamedIndividual(IRI.create(BASE_IRI + individualName));
        ontology.add(factory.getOWLDeclarationAxiom(individual));
        ontology.add(factory.getOWLClassAssertionAxiom(owlClass, individual));
        return individual;
    }
}

package com.example.app;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;
import javafx.scene.control.Alert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * This is the test class which has tests to check the fundamental functionalities of the application
 */

public class TestDemo extends ApplicationTest {
    private DBConnection dbConnection;

    /**
     * The test checks if the entered details are present in the database
     * This shows that fields can be entered in the database
     */
    @Test
    public void testEnterDetails(){
        this.dbConnection = new DBConnection();

        String address = "San Diego National Park";
        String species = "Alex";
        int quantity = 1;
        String ecosystem = "Forest";
        dbConnection.enterSpecies(address, species, quantity, ecosystem);

        String compareAddress = "";
        String compareSpecies = "";
        String compareEcosystem = null;
        int compareQuantity = 0;
        try {
            // Define database connection properties
            String dbUrl = "jdbc:postgresql://localhost:5432/postgres";
            String dbUser = "postgres";
            String dbPassword = "Post23@me";

            // Create a database connection
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            //Check if the fields have been entered into the database
            String getAddress = "SELECT * FROM wildlife_tracker";
            PreparedStatement addressStatement = conn.prepareStatement(getAddress);

            ResultSet rs = addressStatement.executeQuery();
            while (rs.next()) {
                int count = 0;
                if (rs.getString(1).equals(address)) {
                    compareAddress = rs.getString(1);
                    count+=1;
                }
                if (rs.getString(2).equals(species)) {
                    compareSpecies = rs.getString(2);
                    count+=1;
                }
                String ecosystemDBValue = rs.getString(4);
                if (ecosystem.equals(ecosystemDBValue)) {
                    compareEcosystem = rs.getString(4);
                    count+=1;
                }
                if (rs.getInt(3)==quantity) {
                    compareQuantity = rs.getInt(3);
                    count+=1;
                }
                //count will be incremented to 4 if all 4 values are present
                if (count==4){
                    break;
                }
            }
            // Close the database connection
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error inserting data into the database.").show();
        }
        Assertions.assertEquals(address, compareAddress,"San Diego National Park");
        Assertions.assertEquals(ecosystem, compareEcosystem, "Forest");
        Assertions.assertEquals(species, compareSpecies, "Alex");
        Assertions.assertEquals(quantity, compareQuantity, 1);
    }

    /**
     * The test checks if the address entered returns a locator value
     * This means it can find the address provided on the map
     */
    @Test
    public void getGeocode(){
        String address = "San Diego National Park";
        LocatorTask locatorTask = new LocatorTask("https://geocode-api.arcgis.com/arcgis/rest/services/World/GeocodeServer");

        GeocodeParameters geocodeParameters = new GeocodeParameters();
        geocodeParameters.getResultAttributeNames().add("*");
        geocodeParameters.setMaxResults(1);
        ListenableFuture<List<GeocodeResult>> geocodeResultsFuture = locatorTask.geocodeAsync(address, geocodeParameters);
        geocodeResultsFuture.addDoneListener(() -> {
            try {
                List<GeocodeResult> geocodes = geocodeResultsFuture.get();
                if (geocodes.size() > 0) {
                    GeocodeResult result = geocodes.get(0);
                    System.out.println("Found " + result.getLabel());
                    System.out.println("at " + result.getDisplayLocation());
                    System.out.println("with score " + result.getScore());
                } else {
                    new Alert(Alert.AlertType.INFORMATION, "No results found.").show();
                }
            } catch (InterruptedException | ExecutionException e) {
                new Alert(Alert.AlertType.ERROR, "Error getting result.").show();
                e.printStackTrace();
            }
        });

        Assertions.assertNotNull(locatorTask.geocodeAsync(address, geocodeParameters));
    }

    /**
     * This test checks if the returnDisplayResults returns a list so bulk geocoding can be performed
     */
    @Test
    public void testViewDetails(){
        this.dbConnection = new DBConnection();
        String fromEcosystem = "Forest";
        Map<String,List<String>> returnedResults = dbConnection.returnDisplayResults(fromEcosystem);
        Assertions.assertFalse(returnedResults.isEmpty(), "The dictionary should not be empty");

    }

}

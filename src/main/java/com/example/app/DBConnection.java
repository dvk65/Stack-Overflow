package com.example.app;

import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * For this class to work, create a postgresql account and database with the following details:
 * username: postgres
 * password: Post23@me
 * port: 5432
 * table name: wildlife_tracker
 * column names: Address(VarChar), Species(VarChar), Quantity(Integer), Ecosystem(VarChar)
 */

public class DBConnection {
    /**
     * This method makes sure that data inputted by the user is stored in this postgresql database
     * @param address where the species was spotted
     * @param species which species was spotted
     * @param quantity how many of the species were there
     * @param ecosystem in which ecosystem was the specie spotted
     */
    public void enterSpecies(String address, String species, int quantity, String ecosystem){
        try {
            //Define database connection properties
            String dbUrl = "jdbc:postgresql://localhost:5432/postgres";
            String dbUser = "postgres";
            String dbPassword = "Post23@me";

            //Create a database connection
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            //Prepare an SQL INSERT statement
            String insertSQL = "INSERT INTO wildlife_tracker (\"Address\", \"Species\", \"Quantity\", \"Ecosystem\") VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertSQL);
            preparedStatement.setString(1, address);
            preparedStatement.setString(2, species);
            preparedStatement.setInt(3, quantity);
            preparedStatement.setString(4, ecosystem);

            //Execute the INSERT statement
            preparedStatement.executeUpdate();

            //Close the database connection
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error inserting data into the database.").show();
        }
    }

    /**
     * This method is used for displaying data from the database on the map
     * @param checkBoxValue user selected ecosystems are given as input to the method
     * @return list of all addresses in that ecosystem are returned as a list
     */
    public Map<String, List<String>> returnDisplayResults(String checkBoxValue){
        Map<String, List<String>> Locations = new HashMap<>();
        try {
            //Define database connection properties
            String dbUrl = "jdbc:postgresql://localhost:5432/postgres";
            String dbUser = "postgres";
            String dbPassword = "Post23@me";

            //Create a database connection
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            //Execute the SELECT statement based on the ecosystem selected
            if (checkBoxValue.equals("Forest")){
                String receiveAddressSQL = "SELECT \"Address\",\"Species\",\"Quantity\" FROM wildlife_tracker WHERE \"Ecosystem\"='Forest'";

                PreparedStatement preparedStatementAddress = conn.prepareStatement(receiveAddressSQL);

                ResultSet rs = preparedStatementAddress.executeQuery();

                while(rs.next()){
                    String addresses = rs.getString("Address");
                    String species = rs.getString("Species");
                    Integer quantityNum = rs.getInt("Quantity");
                    String quantity = String.valueOf(quantityNum);
                    Locations.put(addresses,new ArrayList<>());
                    String key = addresses;
                    List<String> valueList = Locations.get(key);
                    valueList.add(species);
                    valueList.add(quantity);
                }
            }
            else if (checkBoxValue.equals("Wetland")){
                String receiveSQL = "SELECT \"Address\",\"Species\",\"Quantity\" FROM wildlife_tracker WHERE \"Ecosystem\"='Wetland'";
                PreparedStatement preparedStatement = conn.prepareStatement(receiveSQL);

                ResultSet rs = preparedStatement.executeQuery();
                while(rs.next()){
                    String addresses = rs.getString("Address");
                    String species = rs.getString("Species");
                    Integer quantityNum = rs.getInt("Quantity");
                    String quantity = String.valueOf(quantityNum);
                    Locations.put(addresses,new ArrayList<>());
                    String key = addresses;
                    List<String> valueList = Locations.get(key);
                    valueList.add(species);
                    valueList.add(quantity);
                }
            }
            else if (checkBoxValue.equals("Grassland")){
                String receiveSQL = "SELECT \"Address\",\"Species\",\"Quantity\" FROM wildlife_tracker WHERE \"Ecosystem\"='Grassland'";
                PreparedStatement preparedStatement = conn.prepareStatement(receiveSQL);

                ResultSet rs = preparedStatement.executeQuery();
                while(rs.next()){
                    String addresses = rs.getString("Address");
                    String species = rs.getString("Species");
                    Integer quantityNum = rs.getInt("Quantity");
                    String quantity = String.valueOf(quantityNum);
                    Locations.put(addresses,new ArrayList<>());
                    String key = addresses;
                    List<String> valueList = Locations.get(key);
                    valueList.add(species);
                    valueList.add(quantity);
                }
            }
            else if (checkBoxValue.isEmpty()){
                String receiveSQL = "SELECT * FROM wildlife_tracker";
                PreparedStatement preparedStatement = conn.prepareStatement(receiveSQL);
                ResultSet rs = preparedStatement.executeQuery();
                while(rs.next()){
                    String addresses = rs.getString("Address");
                    String species = rs.getString("Species");
                    Integer quantityNum = rs.getInt("Quantity");
                    String quantity = String.valueOf(quantityNum);
                    Locations.put(addresses,new ArrayList<>());
                    String key = addresses;
                    List<String> valueList = Locations.get(key);
                    valueList.add(species);
                    valueList.add(quantity);
                }
            }

            //Close the database connection
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error receiving data from database.").show();
        }

        //Return the list of addresses received from the SELECT statement for bulk geocoding
        return Locations;
    }
}

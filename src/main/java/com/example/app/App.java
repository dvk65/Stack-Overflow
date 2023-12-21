package com.example.app;

/**
 * Final project submission
 * 12/11/2023
 * Dipti Kulkarni
 *
 * This project makes use of the following design patterns:
 * Data Access Object Pattern: This pattern is responsible for separating database interactions from the rest of the code
 * Facade Pattern: In this pattern the App.java class is a high-level interface that simplifies the use of multiple subsystems
 * Observer Pattern: The choices made by users influence the data received from database which follows the rules of the observer pattern
 *
 * The system takes the user address, the species spotted, the ecosystem where it is spotted and quantity of the species, and stores it in a postgresql database.
 * There is also an option of viewing the chosen ecosystem value entries which are selected from the database
 * When the view or enter options are selected, there is also an option of going back to the globe screen displayed at the beginning
 */

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.mapping.*;
import com.esri.arcgisruntime.mapping.view.*;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class App extends Application {

    private MapView mapView;
    private SceneView sceneView;
    private GeocodeParameters geocodeParameters;
    GraphicsOverlay graphicsOverlay;
    private LocatorTask locatorTask;
    private TextField searchBox;
    private TextField speciesBox;
    private TextField quantityBox;
    private Button buttonSearch;
    private Button buttonDetails;
    private Text details;
    private Text choose;
    private RadioButton wetland;
    private RadioButton grassland;
    private RadioButton forest;
    private RadioButton view;
    private RadioButton enter;
    private Button startButton;
    private Text options;
    public DBConnection dbConnection;
    private RadioButton viewForest;
    private RadioButton viewWetland;
    private RadioButton viewGrassland;
    private RadioButton all;
    private Text selectView;
    private Button viewButton;
    private Button globeButton;
    private Text seeGlobe;
    String species;

    /**
     * The main class that starts the Application
     * @param args
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    /**
     * The start function is where all the above-mentioned functionality of the code takes place
     * @param stage the primary stage for this application, on which the application scene can be set
     */
    @Override
    public void start(Stage stage) {
        // Constructing the class DBConnection to call its methods
        this.dbConnection = new DBConnection();

        // set the title and size of the stage and show it
        stage.setTitle("Add a layer");
        stage.setWidth(1000);
        stage.setHeight(1000);
        stage.show();

        // create a JavaFX scene with a stack pane as the root node, and add it to the globe (entry) scene
        StackPane stackPaneGlobe = new StackPane();
        Scene sceneGlobe = new Scene(stackPaneGlobe);
        stage.setScene(sceneGlobe);

        // set the API key to access the arcgis API
        String yourApiKey = "AAPK007a59f6ee684049bdbc2a58aab97fcbsIh1jGoB34ip1s8Y52fGpY71OkqTmRaD6GCdDMODdtMT2Eo2GBKNFjqFc9fAIAYX";
        ArcGISRuntimeEnvironment.setApiKey(yourApiKey);

        // create a globe view to display on running the code and add it to the stack pane
        sceneView = new SceneView();
        stackPaneGlobe.getChildren().add(sceneView);

        ArcGISTiledLayer arcGISTiledLayer = new ArcGISTiledLayer("https://tiles.arcgis.com/tiles/nGt4QxSblgDfeJn9/arcgis/rest/services/VintageShadedRelief/MapServer");
        ArcGISScene gisScene = new ArcGISScene(new Basemap(arcGISTiledLayer));

        sceneView.setArcGISScene(gisScene);
        sceneView.setViewpointCamera(new Camera(20.7579, -63.7715, 25512548, 0, 0.1, 0));

        // set the labels and dimensions of input text fields, the buttons as well as the radio buttons
        setupTextField();

        // use separate stackPanes to add multiple elements to the globe and map GUI
        stackPaneGlobe.getChildren().add(enter);
        stackPaneGlobe.getChildren().add(view);
        stackPaneGlobe.getChildren().add(options);
        stackPaneGlobe.getChildren().add(startButton);
        StackPane.setAlignment(options, Pos.CENTER);

        // setting the alignment of the elements added to the stackPane
        StackPane.setMargin(options, new Insets(10, 10, 0, 0));
        StackPane.setAlignment(enter, Pos.CENTER);
        StackPane.setMargin(enter, new Insets(80, 150, 0, 0));
        StackPane.setAlignment(view, Pos.CENTER);
        StackPane.setMargin(view, new Insets(80, 0, 0, 100));
        StackPane.setAlignment(startButton, Pos.CENTER);
        StackPane.setMargin(startButton, new Insets(160,200,0,0));

        StackPane.setAlignment(searchBox, Pos.TOP_LEFT);
        StackPane.setMargin(searchBox, new Insets(10, 0, 0, 10));
        StackPane.setAlignment(speciesBox, Pos.TOP_LEFT);
        StackPane.setMargin(speciesBox, new Insets(45, 0, 0, 10));
        StackPane.setAlignment(quantityBox, Pos.TOP_LEFT);
        StackPane.setMargin(quantityBox, new Insets(80, 0, 0, 10));
        StackPane.setAlignment(buttonSearch, Pos.TOP_LEFT);
        StackPane.setMargin(buttonSearch, new Insets(200, 0, 0, 10));
        StackPane.setAlignment(buttonDetails, Pos.TOP_LEFT);
        StackPane.setMargin(buttonDetails, new Insets(300, 0, 0, 10));
        StackPane.setAlignment(details, Pos.TOP_LEFT);
        StackPane.setMargin(details, new Insets(330, 0, 0, 20));
        StackPane.setAlignment(choose,Pos.TOP_LEFT);
        StackPane.setMargin(choose, new Insets(115,0,0,10));
        StackPane.setAlignment(wetland, Pos.TOP_LEFT);
        StackPane.setMargin(wetland, new Insets(150, 0, 0, 10));
        StackPane.setAlignment(grassland, Pos.TOP_LEFT);
        StackPane.setMargin(grassland, new Insets(150, 0, 0, 110));
        StackPane.setAlignment(forest, Pos.TOP_LEFT);
        StackPane.setMargin(forest, new Insets(150, 0, 0, 210));

        StackPane.setAlignment(selectView, Pos.BOTTOM_LEFT);
        StackPane.setMargin(selectView, new Insets(0,10,250,70));
        StackPane.setAlignment(viewForest, Pos.BOTTOM_LEFT);
        StackPane.setMargin(viewForest, new Insets(0,10,210,70));
        StackPane.setAlignment(viewWetland, Pos.BOTTOM_LEFT);
        StackPane.setMargin(viewWetland, new Insets(0,10,170,70));
        StackPane.setAlignment(viewGrassland, Pos.BOTTOM_LEFT);
        StackPane.setMargin(viewGrassland, new Insets(0,10,130,70));
        StackPane.setAlignment(all, Pos.BOTTOM_LEFT);
        StackPane.setMargin(all, new Insets(0,10,90,70));
        StackPane.setAlignment(viewButton, Pos.BOTTOM_LEFT);
        StackPane.setMargin(viewButton, new Insets(0,10,50,70));

        StackPane.setAlignment(globeButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(globeButton, new Insets(0,70,130,10));
        StackPane.setAlignment(seeGlobe,Pos.BOTTOM_RIGHT);
        StackPane.setMargin(seeGlobe,new Insets(0,70,170,10));

        //The visibility of fields that are not functional on the globe scene are set to false
        buttonDetails.setVisible(false);
        details.setVisible(false);
        searchBox.setVisible(false);
        speciesBox.setVisible(false);
        quantityBox.setVisible(false);
        buttonSearch.setVisible(false);
        wetland.setVisible(false);
        grassland.setVisible(false);
        forest.setVisible(false);
        choose.setVisible(false);

        viewButton.setVisible(false);
        viewForest.setVisible(false);
        viewGrassland.setVisible(false);
        viewWetland.setVisible(false);
        selectView.setVisible(false);
        all.setVisible(false);

        globeButton.setVisible(false);
        seeGlobe.setVisible(false);

        startButton.setOnAction(event -> {
            sceneView.dispose();
            /**
             * On selecting either view or enter, and clicking the startButton the globe scene is disposed off
             * A new map scene is created
             */
            StackPane stackPaneMap = new StackPane();
            Scene sceneMap = new Scene(stackPaneMap);
            stage.setScene(sceneMap);

            // set the map on the mapView
            mapView = new MapView();
            stackPaneMap.getChildren().add(mapView);

            //All elements on the map are added to the mapView's stackPane named stackPaneMap
            //Elements if enter option is selected on the globe scene
            stackPaneMap.getChildren().add(searchBox);
            stackPaneMap.getChildren().add(speciesBox);
            stackPaneMap.getChildren().add(quantityBox);
            stackPaneMap.getChildren().add(buttonSearch);
            stackPaneMap.getChildren().add(buttonDetails);
            stackPaneMap.getChildren().add(details);
            stackPaneMap.getChildren().add(choose);
            stackPaneMap.getChildren().add(wetland);
            stackPaneMap.getChildren().add(grassland);
            stackPaneMap.getChildren().add(forest);

            //Elements if view option is selected on the globe scene
            stackPaneMap.getChildren().add(viewButton);
            stackPaneMap.getChildren().add(viewForest);
            stackPaneMap.getChildren().add(viewGrassland);
            stackPaneMap.getChildren().add(viewWetland);
            stackPaneMap.getChildren().add(selectView);
            stackPaneMap.getChildren().add(all);

            //Elements to go back to the globe screen
            stackPaneMap.getChildren().add(globeButton);
            stackPaneMap.getChildren().add(seeGlobe);

            //Selecting the type of base map to display
            ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_NAVIGATION);
            mapView.setMap(map);

            //Temporarily store dynamic sets of graphical attributes formed on the map
            graphicsOverlay = new GraphicsOverlay();
            mapView.getGraphicsOverlays().add(graphicsOverlay);

            //Calling the createLocatorTaskDefaultParameters class to set parameters for geocoding
            createLocatorTaskDefaultParameters();
                if (enter.isSelected()) {
                    //As enter is selected on the screen, setting all the fields required for entering to true
                    searchBox.setVisible(true);
                    speciesBox.setVisible(true);
                    quantityBox.setVisible(true);
                    buttonSearch.setVisible(true);
                    wetland.setVisible(true);
                    grassland.setVisible(true);
                    forest.setVisible(true);
                    choose.setVisible(true);
                    wetland.setOnAction(radioEvent -> handleRadioButton());
                    grassland.setOnAction(radioEvent -> handleRadioButton());
                    forest.setOnAction(radioEvent -> handleRadioButton());

                /**
                 * On clicking search button after entering the three fields and selecting the ecosystem:
                 * the enterFields method is called,
                 * any details displayed are set to false and
                 * any previously marked places on the map are cleared
                 */
                buttonSearch.setOnAction(enterEvent -> {
                     enterFields();
                     details.setVisible(false);
                     graphicsOverlay.getGraphics().clear();
                 });
                }

        //As view is selected on the screen, setting all the fields required for viewing to true
        if(view.isSelected()){
            viewButton.setVisible(true);
            viewForest.setVisible(true);
            viewForest.setOnAction(vradioEvent->handleRadioButton());
            viewGrassland.setVisible(true);
            viewGrassland.setOnAction(vradioEvent->handleRadioButton());
            viewWetland.setVisible(true);
            viewWetland.setOnAction(vradioEvent->handleRadioButton());
            all.setVisible(true);
            all.setOnAction(vradioEvent->handleRadioButton());
            selectView.setVisible(true);

            /**
             * On clicking view button after selecting the ecosystem:
             * the viewFields method is called,
             * the buttonDetails button and any details displayed are set to false and
             * any previously marked places on the map are cleared
             */
            viewButton.setOnAction(viewEvent -> {
                viewFields();
                buttonDetails.setVisible(false);
                details.setVisible(false);
                graphicsOverlay.getGraphics().clear();
            });
        }
        });

        //The button to view the globe screen again is set to true
        globeButton.setVisible(true);
        seeGlobe.setVisible(true);

        //If the button is clicked, all the elements of the map scene are set to false and the mapView is disposed off
        //The sceneGlobe is set to stage and the visibility of elements on that scene is set to true
        globeButton.setOnAction(globeEvent -> {
            searchBox.setVisible(false);
            speciesBox.setVisible(false);
            quantityBox.setVisible(false);
            buttonSearch.setVisible(false);
            wetland.setVisible(false);
            grassland.setVisible(false);
            forest.setVisible(false);
            choose.setVisible(false);
            buttonDetails.setVisible(false);
            selectView.setVisible(false);
            viewForest.setVisible(false);
            viewGrassland.setVisible(false);
            viewWetland.setVisible(false);
            all.setVisible(false);
            viewButton.setVisible(false);
            mapView.dispose();
            stage.setScene(sceneGlobe);
            options.setVisible(true);
            enter.setVisible(true);
            view.setVisible(true);
            startButton.setVisible(true);
            enter.setSelected(false);
            view.setSelected(false);
            details.setVisible(false);
        });

    }

    /**
     * RadioButtons should only let the user select one value
     * handleRadioButton makes sure this happens in sets of Radio buttons used in the view and enter sections
     */
    void handleRadioButton() {
        if (viewForest.isSelected()){
            viewGrassland.setSelected(false);
            viewWetland.setSelected(false);
            all.setSelected(false);
        }
        if (viewGrassland.isSelected()){
            viewForest.setSelected(false);
            viewWetland.setSelected(false);
            all.setSelected(false);
        }
        if (viewWetland.isSelected()){
            viewGrassland.setSelected(false);
            viewForest.setSelected(false);
            all.setSelected(false);
        }
        if (all.isSelected()){
            viewGrassland.setSelected(false);
            viewWetland.setSelected(false);
            viewForest.setSelected(false);
        }
        if(wetland.isSelected()){
            grassland.setSelected(false);
            forest.setSelected(false);
        }
        if(grassland.isSelected()){
            wetland.setSelected(false);
            forest.setSelected(false);
        }
        if(forest.isSelected()){
            grassland.setSelected(false);
            wetland.setSelected(false);
        }
    }

    /**
     * The enterFields method is responsible for:
     * Set the map to a zoomed in view,
     * Checking if values are entered in all three fields and an ecosystem value is selected,
     * Geocoding based on the address value using performGeocode method,
     * Calling enterSpecies method to establish database connection,
     * Executing queries for entering address, species, ecosystem and quantity values into the database via the enterSpecies method,
     * Once the correct latitudes and longitudes are located on map, the details button is made visible,
     * The view fields made visible if the user wants to view data after entering it
     * A button to take user back to home page, with the globe, is made visible
     */

    void enterFields(){
        mapView.setViewpoint(new Viewpoint(34.02700, -118.80543, 144447.638572));
        wetland.setOnAction(radioEvent -> handleRadioButton());
        grassland.setOnAction(radioEvent -> handleRadioButton());
        forest.setOnAction(radioEvent -> handleRadioButton());
        String address = searchBox.getText();
        species = speciesBox.getText();
        int quantity = Integer.parseInt(quantityBox.getText());
        String ecosystem = "";
        if (wetland.isSelected()){
            ecosystem="Wetland";
            wetland.setSelected(false);
        }
        if (grassland.isSelected()){
            ecosystem="Grassland";
            grassland.setSelected(false);
        }
        if (forest.isSelected()){
            ecosystem="Forest";
            forest.setSelected(false);
        }

        if (!address.isBlank() & !species.isBlank() & quantity >0 & !ecosystem.isEmpty()){

            performGeocode(address, species, String.valueOf(quantity));
            dbConnection.enterSpecies(address, species, quantity, ecosystem);
            // Clear input fields after successful insertion
            searchBox.clear();
            speciesBox.clear();
            quantityBox.clear();

            buttonDetails.setVisible(true);
            selectView.setVisible(true);
            viewForest.setVisible(true);
            viewGrassland.setVisible(true);
            viewWetland.setVisible(true);
            all.setVisible(true);
            viewButton.setVisible(true);

            viewButton.setOnAction(viewEvent -> {
                viewFields();
                details.setVisible(false);
                buttonDetails.setVisible(false);
                graphicsOverlay.getGraphics().clear();
            });

            details.setText("Species: "+ species + "\n" + "Quantity: "+ quantity+ "\n" +"Ecosystem: "+ecosystem);
            buttonDetails.setOnAction(eventDetails -> {
                details.setVisible(true);
                System.out.println("Details!");
            });

        }else {
            new Alert(Alert.AlertType.INFORMATION, "Please enter all data.").show();
        }
    }

    /**
     * The viewFields method is responsible for:
     * Set the map to a zoomed out complete map view,
     * Checking if an ecosystem value is selected,
     * Batch or bulk geocoding based on which ecosystem is selected using bulkMark method,
     * Calling returnDisplayResults method to establish database connection,
     * Executing queries for viewing entered locations from the database via the returnDisplayResults method,
     * The fields for entering data made visible if user wants to enter data after viewing it,
     * A button to take user back to home page, with the globe, is made visible
     */
    void viewFields(){
        mapView.setViewpoint(new Viewpoint(-150.80543, 34.02700, 140000000.638572));
        viewForest.setOnAction(vradioEvent->handleRadioButton());
        viewWetland.setOnAction(vradioEvent->handleRadioButton());
        viewGrassland.setOnAction(vradioEvent->handleRadioButton());
        all.setOnAction(vradioEvent->handleRadioButton());
        if(viewForest.isSelected()){
            bulkMark(dbConnection.returnDisplayResults("Forest"));
            viewForest.setSelected(false);
        }
        else if(viewGrassland.isSelected()){
            bulkMark(dbConnection.returnDisplayResults("Grassland"));
            viewGrassland.setSelected(false);
        }
        else if(viewWetland.isSelected()){
            bulkMark(dbConnection.returnDisplayResults("Wetland"));
            viewWetland.setSelected(false);
        }
        else if(all.isSelected()){
            bulkMark(dbConnection.returnDisplayResults(""));
            all.setSelected(false);
        }
        searchBox.setVisible(true);
        speciesBox.setVisible(true);
        quantityBox.setVisible(true);
        buttonSearch.setVisible(true);
        wetland.setVisible(true);
        grassland.setVisible(true);
        forest.setVisible(true);
        choose.setVisible(true);
        buttonSearch.setOnAction(enterEvent -> {
            enterFields();
            details.setVisible(false);
            graphicsOverlay.getGraphics().clear();
        });
    }

    /**
     * Bulk geocoding or batch geocoding is performed on a list of addresses by repeatedly calling the performGeocode method
     * @param Locations list of addresses is received from the postgres database using the returnDisplayResults method in the viewFields method
     */
    void bulkMark(Map<String, List<String>> Locations){
        for (Map.Entry<String, List<String>> entry : Locations.entrySet()) {
            List<String> values = entry.getValue();
            System.out.println(values.get(1));
            performGeocode(entry.getKey(), values.get(0), values.get(1));
        }
    }

    /**
     * All instructions for navigating the interface, and interactive objects of the GUI are styled using this method
     */
    void setupTextField() {
        options = new Text("What would you want to do?");
        options.setFont(Font.font(22));
        enter = new RadioButton("Enter");
        enter.setFont(Font.font(20));
        view = new RadioButton("View");
        view.setFont(Font.font(20));
        startButton = new Button("Go!");
        startButton.setFont(Font.font(22));
        searchBox = new TextField();
        searchBox.setMaxWidth(400);
        searchBox.setPromptText("Search for an address");
        speciesBox = new TextField();
        speciesBox.setMaxWidth(400);
        speciesBox.setPromptText("Enter species");
        quantityBox = new TextField();
        quantityBox.setMaxWidth(400);
        quantityBox.setPromptText("Enter quantity");
        buttonSearch = new Button("Enter");
        buttonSearch.setMaxWidth(100);
        buttonDetails = new Button("Details");
        buttonDetails.setMaxWidth(100);
        details = new Text();
        wetland = new RadioButton("Wetland");
        forest = new RadioButton("Forest");
        grassland = new RadioButton("Grassland");
        choose = new Text("Choose which ecosystem:");

        viewWetland = new RadioButton("Wetland");
        viewGrassland = new RadioButton("Grassland");
        viewForest = new RadioButton("Forest");
        all = new RadioButton("All Entries");
        selectView = new Text("What would you like to view?");
        selectView.setFont(Font.font(15));
        viewButton = new Button("Go!!");
        viewButton.setMaxWidth(100);

        globeButton = new Button("Geography!");
        seeGlobe = new Text("Do you want to see the globe again?");
        seeGlobe.setFont(Font.font(15));
        }

    /**
     * Calling the geocode API by ArcGIS,
     * Setting parameters to be used while geocoding addresses like:
     * Getting the complete address from provided address,
     * Getting a single best score based on scores of all matching locations, and
     * Setting spatial reference for finding where the features are located throughout the world/map
     */
    void createLocatorTaskDefaultParameters() {
        locatorTask = new LocatorTask("https://geocode-api.arcgis.com/arcgis/rest/services/World/GeocodeServer");
        geocodeParameters = new GeocodeParameters();
        geocodeParameters.getResultAttributeNames().add("*");
        geocodeParameters.setMaxResults(1);
        geocodeParameters.setOutputSpatialReference(mapView.getSpatialReference());
    }

    /**
     * Coordinates of the provided address are found along with the complete address and score
     * The geocodeParameters that were set are passed along with the locatorTask
     * @param address inputted by user when making an entry for a species
     */
    void performGeocode(String address, String getSpecies, String getQuantity) {
        ListenableFuture<List<GeocodeResult>> geocodeResultsFuture = locatorTask.geocodeAsync(address, geocodeParameters);
        geocodeResultsFuture.addDoneListener(() -> {
            try {
                List<GeocodeResult> geocodes = geocodeResultsFuture.get();
                    GeocodeResult result = geocodes.get(0);
                    System.out.println("Found " + result.getLabel());
                    System.out.println("animal " + getSpecies);
                    System.out.println("quantity " + getQuantity);
                    System.out.println("at " + result.getDisplayLocation());
                    System.out.println("with score " + result.getScore());

                    displayResult(result, getSpecies, getQuantity);
            } catch (InterruptedException | ExecutionException e) {
                new Alert(Alert.AlertType.ERROR, "Error getting result.").show();
                e.printStackTrace();
            }
        });
    }

    /**
     * The result from performGeocode is displayed on the map
     * The text and marker styling is specified
     * @param geocodeResult coordinates returned by geocoding the address using performGeocode method
     */
    private void displayResult(GeocodeResult geocodeResult, String getSpecies, String getQuantity) {
        // create a graphic to display the address text
        String label = geocodeResult.getLabel();
        TextSymbol textSymbol = new TextSymbol(18, label, Color.BLACK, TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.BOTTOM);
        TextSymbol textSymbol1 = new TextSymbol(15, getSpecies + ": " + getQuantity, Color.BLACK, TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.TOP);
        Graphic textGraphic = new Graphic(geocodeResult.getDisplayLocation(), textSymbol);
        Graphic textGraphic1 = new Graphic(geocodeResult.getDisplayLocation(), textSymbol1);
        graphicsOverlay.getGraphics().add(textGraphic);
        graphicsOverlay.getGraphics().add(textGraphic1);

        // create a graphic to display the location as a red square
        SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.SQUARE, Color.RED, 12.0f);
        Graphic markerGraphic = new Graphic(geocodeResult.getDisplayLocation(), geocodeResult.getAttributes(), markerSymbol);
        graphicsOverlay.getGraphics().add(markerGraphic);

        mapView.setViewpointCenterAsync(geocodeResult.getDisplayLocation());
    }

    /**
     * Stops and releases all resources used in application.
     */
    @Override
    public void stop() {
        if (mapView != null) {
            mapView.dispose();
        }
    }
}
/**
 * References:
 * Joyati Samar(samar.j@northeastern.edu) helped me with a few resources to get started. She has experience with GIS in Python
 * It definitely helped me get started with the implementation.
 * She is not in our PDP class but is a computer science align student.
 *
 * Aishwarya Ravichandran (ravichandran.ai@northeastern.edu) discussed with me if I could manage my code better.
 * She was a Java developer before her masters and has been very helpful and patient with our doubts.
 *
 * https://docs.geotools.org/stable/userguide/tutorial/quickstart/intellij.html
 * Though I did not use geotools, I spent an entire week on their website trying to figure things out
 *
 * https://developers.arcgis.com/java/maps-2d/tutorials/display-a-map/
 * ArcGIS was the API Prof. Dym had mentioned when I submitted my project idea.
 * Even though I had to figure some things out, it has amazing resources.
 *
 * https://www.geeksforgeeks.org/introduction-to-jdbc/
 * https://www.postgresqltutorial.com/postgresql-jdbc/insert/
 * https://www.w3schools.com/sql/sql_where.asp
 *
 * https://docs.oracle.com/javafx/2/ui_controls/text-field.htm
 *https://docs.oracle.com/javafx/2/ui_controls/radio-button.htm
 *
 *https://developers.arcgis.com/documentation/glossary/graphics-overlay/
 *
 * https://developers.arcgis.com/documentation/mapping-apis-and-services/geocoding/batch-geocoding/
 *
 * https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
 */

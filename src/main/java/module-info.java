/*
 COPYRIGHT 1995-2022 ESRI
 TRADE SECRETS: ESRI PROPRIETARY AND CONFIDENTIAL
 Unpublished material - all rights reserved under the
 Copyright Laws of the United States.
 For additional information, contact:
 Environmental Systems Research Institute, Inc.
 Attn: Contracts Dept
 380 New York Street
 Redlands, California, USA 92373
 email: contracts@esri.com
 */
/**
 * Declare the required modules for main program.
 */

module com.example.app {
    requires com.esri.arcgisruntime;

    requires javafx.graphics;

    requires org.slf4j.nop;
    requires java.sql;

    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.app to javafx.fxml;

    exports com.example.app;
}
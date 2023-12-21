This Gradle project extensively uses JavaDocs (because of my professor) which makes it easy to follow. But here is a README to get a basic idea of what it does anyways.<br>
This is a Wildlife Habitat Mapper made using the ArcGIS API with Java (Why?!)<br>
An ideal use case would be for people to keep track of the fauna they see during their travels.<br>
The project allows the user to Enter and View values. While entering the location (or address), species and quantity of the seen species is asked along with the ecosystem it was spotted at. The entered address is geocoded and displayed on the map.<br>
While viewing values, the user is asked to choose the ecosystem they would want to view. Data from these ecosystems is gathered and bulk geocoding takes place to display it on the map.<br>
There are a total of two scenes. One can be viewed right after running the project and the second when the first option to view or enter is selected.<br>
The database connection is done using JDBC. The table name is wildlife_tracker with Address, Species, Quantity and Ecosystem columns.<br>
There are JUnit testcases as well for the fundamental classes in the project.<br>

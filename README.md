To run the project:<br>
1. Pull the repository to your local machine
2. Find the path to your repository
3. Use the command "./gradlew build" to get the project setup
4. For execution, use "./gradlew run"
5. "./gradlew test" can be used to check if all the tests work

This project was part of my Programming Designs and Paradigms course.<br>
I already had two tests in place as it was required for the project and added one more as part of this assignment. The tests are JUnit tests and can be found under the /src/main/test directory.<br>
The workflow is in the gradle.yml file inside the /.github/workflows directory.<br>
Dependency management is covered by Gradle. The gradle-wrapper.properties file inside the /gradle/wrapper directory helps with making the run compatible on most machines.

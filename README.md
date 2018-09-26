# Collective-Kafka

###Compile and Build:

./gradlew clean build

###Generate Avro Generator Java files
./gradlew clean build
 
 -- Generates java files for all the avsc files located at *src/main/avro/*
onto *build/generated-main-avro-java*

** *Note:* ** Intially when you clone this project, there will be compile time errors because of Logline class, simply build the project using ./gradlew clean build,
which will generate Logline java file and it resolves compile time issues.

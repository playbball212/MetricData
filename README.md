
-------------------Build Gradle Project - which will run tests------------------------------------

    gradle clean build ( There are tests at the service level and controller level which 
    comprise the unit level tests. There are also some integration tests that will test the real apis

--------------------TO BUILD AND RUN Containerized App---------------------------------
    
    
    docker build --build-arg JAR_FILE=build/libs/*.jar -t metricapiimage:latest .
    docker run --publish 8080:8080 metricapiimage:latest
    
    



------------------ SWAGGER DOCUMENTATION --------------------------------------------------
   
   
    http://localhost:8080/api/swagger-ui.html
    
    Hashmap put and get operation time complexity is O(1) with assumption that key-value pairs are distributed across 
    buckets. I have used the metric name as as the hash key and rest api enforces that each metric has a unique name.
    This will guarantee O(1) operations. Therefore insert and delete will also be O(1) therefore these apis will be 
    O(1) 

        
        

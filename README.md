
-------------------Build Gradle Project - which will run tests------------------------------------

    gradle clean build ( There are tests at the service level and controller level which 
    comprise the unit level tests. There are also some integration tests that will test the real apis

--------------------TO BUILD AND RUN Containerized App---------------------------------
    
    
    docker build --build-arg JAR_FILE=build/libs/*.jar -t metricapiimage:latest .
    docker run --publish 8080:8080 metricapiimage:latest
    
    



------------------ SWAGGER DOCUMENTATION --------------------------------------------------
   
   
    http://localhost:8080/api/swagger-ui.html
    
------------------ API RUN TIME ANALYSIS ----------------------------------------------------

        
            
            Let M = # OF Metrics Existing  
            Let N = # OF Metrics submitted 
            Let V[i] = Number of DataPoints for each metric 
        
        1.) Create a Metric - 
        
            
            Time Complexity will be O(N) because were peforming N put operations which are constant time. As well as 
            N Arraylist.add which takes O(1). 
            
            Space Complexity will O(N+M) because we need space for existing metrics as well as submitted metrics 
         
        
        2.) Update a Metric - 
        
            Time Complexity will be O(N) because we are performing N lookups and each lookup in our hashmap will take
            constant time as well as updating the HashMap. Each ArrayList.add will take constant time as well. 
            
            Space Complexity will be O(N+M)
            
        3.) Get Summary Statistics - The Operation will iterate through all the Values of each metric
        
            Time Complexity will be O( N * MAX(V[i]) - When we get the Summary Statistics we are peforming N lookups
            to retrieve the lists of each metric. We are then iterating through each list to aggregate the statistics 
            to perform some addition constant time operations. So we are performing at most N * MAX(V[i]) constant 
            time operations.  
            
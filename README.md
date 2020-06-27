
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
            Let V[M] = # of DataPoints for each metric 
        
        1.) Create a Metric - 
        
            
            Time Complexity will be O(N) because were peforming N Puts on a HashMap which will take constant time. We
            are also performing N adds into an arraylist which will also be constant time. 
            
            Space Complexity will O(N+M) because we need space for existing metrics as well as submitted metrics 
         
        
        2.) Update a Metric - 
        
            Time Complexity will be O(N) as N  (HashMap put / get) constant time. 
            
            Space Complexity will be O(N+M)
            
        3.) Get Summary Statistics - The Operation will iterate through all the Values of each metric
        
            Time Complexity will be O( N * MAX(V[M]) ) - When we get the Summary Statistics we are peforming N lookups
            to retrieve the lists of each metric. We are then iterating through each list to aggregate the statistics 
            to perform some arithmetic constant time operations. So we are performing at most N * MAX(V[N]) constant 
            time operations.  
            
            The Space Complexity will be O(N+M) 
            
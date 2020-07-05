Your Web API should support the following actions:
1. Create a Metric – the API should allow the user to create metrics.
2. Post Values to a Metric - the API should allow the user to post a decimal value to a created
metric. Please note that user should be able to create multiple metrics so you should be able to
specify the metric in this request.
3. Retrieve Statistics - the API should allow the user to retrieve summary statistics on ametric.
Specifically:
a. Arithmetic Mean of a values posted to metric
b. Median of a values posted to metric
c. Min value for metric
d. Max value for metric
Please note that user should be able to create multiple metrics so you should be able to specify
the metric in this request.

All data maintained by the app is ephemeral i.e., it is acceptable if all data stored by app is restarted the
application should clear any data stored in memory by the app.


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
            Let EV = MAX(M) - Metrics with most values 
            Let N = # OF Metrics submitted 
            Let V[M] = # of DataPoints for each metric 
        
        **1.) Create a Metric -** 
        
            
            _Time Complexity will be O(N * log(EV) )_ because we are peforming N Puts on a HashMap which will take constant time
            given the equal distribution of keys . We are also performing N adds into an arraylist which will also be
            constant time. Unless There is a capacity increase but the amortized cost of adding is O(1). We are also
            adding elements to a min heap data structure which will take log(EV). The Heap is maintaing order 
            
         
        
        **2.) Update a Metric -** 
        
            _Time Complexity will be O(N)_ as N  (HashMap put / get) constant time. 
            
        
            
        **3.) Get Summary Statistics -**
        
            _Time Complexity will be O( N * (MAX(V[M]) )_ - When we get the Summary Statistics we are 
            peforming N lookups to retrieve the lists of each metric. Retrieving the min , max , and average will be 
            constant time operations because we are  tracking min/max/mean with each new value added. Finding the median
            will be equivalent to the input size given we are iterating through the priority queue 
            ********PriorityQueue********** 
            
            
          
          
            

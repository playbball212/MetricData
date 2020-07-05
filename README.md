This  Web API supports the following actions:
1. Create a Metric – the API should allow the user to create metrics. Each metric will have a string qualifier and double value. 
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
            Let N = # OF Metrics submitted 
            Let V[M] = # of DataPoints for each metric 
        
        **1.) Create a Metric -** 
        
            
            _Time Complexity will be O(N * log(MAX(V[M]) )_  We are peforming N Puts on a HashMap which will take
             constant time given the equal distribution of keys . We are also performing N adds into an arraylist which 
             will also be constant time unless there is a capacity increase but given the rarity of that happening the 
             amortized cost is O(1). ( As we insert elements  , we double capacity when the size of the array is a power
             of 2. So after X elements , we double the capacity at array sizes 1 , 2 , 4 , 6 , 8. What then is the sum of
             of X + X/2 + X/4 + X/8 + 1 ( Roughly 2X) 
             We are also adding elements to a min heap data structure which will take log(MAX(V[M]). 
            
         
        
        **2.) Update a Metric -** 
        
            _Time Complexity will be O(N * log(MAX(V[M])_ as N  (HashMap put / get) constant time. Same  Arguement  from
            above 
            
        
            
        **3.) Get Summary Statistics -**
        
            _Time Complexity will be O( N * (MAX(V[M]) )_ - When we get the Summary Statistics we are 
            peforming N lookups to retrieve the lists of each metric. Retrieving the min , max , and average will be 
            constant time operations because we are  tracking min/max/mean with each new value added. Finding the median
            will be equivalent to the input size given we are iterating through the priority queue 
            ********PriorityQueue********** 
            
            
          
          
            

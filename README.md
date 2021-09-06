# CODE CHALLENGE
### Requirements 
###### (extract from the document provided without any modification)
Develop SpringBoot application and test a HTTP REST API in that fronts the OpenWeatherMap service: OpenWeatherMap name service guide: http://openweathermap.org/current#name . (Example: http://samples.openweathermap.org/data/2.5/weather?q=London,uk) 
Your service should: 
1.	Enforce API Key scheme. An API Key is rate limited to 5 weather reports an hour. After that your service should respond in a way which communicates that the hourly limit has been exceeded. Create 5 API Keys. Pick a convention for handling them that you like; using simple string constants is fine. This is NOT an exercise about generating and distributing API Keys. Assume that the user of your service knows about them.
2.	Have a URL that accepts both a city name and country name. Based upon these inputs, and the API Key, your service should decide whether or not to call the OpenWeatherMap name service. If it does, the only weather data you need to return to the client is the description field from the weather JSON result. Whether it does or does not, it should respond appropriately to the client. Cancel changes
3.	Reject requests with invalid input or missing API Keys.
4.	Store the data from openweathermap.org into H2 DB.
5.	The API will query the data from H2
6.	Clear Spring Layers are needed.
7.	Follow Rest API convention.

#Design
Current design based on restful api design which.
- Rest Service
  WeatherController which handle the user request. It also responsible for passing
  correct http status code to client 
- Service Layer
  Basically act as service/logic layer and also interact with other services.
  ApiKeyService - dealing with api key related logic including key schema and 
  WeatherService - core application logic around the weather service
  OpenWeatherApiService - communicate with external Open Weather Api and provide data 
    
- Data Access Layer
  perforce database related operation
  ApiKeyRepository - store for api key
  WeatherRepository - dealing with cache weather information.  
  
####Enforce API Key scheme
This is not really clear in order to address that. Assuming we have to come up with logic which can enclose some useful data in the token(however it says its not necessary).
use dot notation to devide field into hold following information.
<base 64 encoded use email>.<api issue time>.<exire time>.<hash of first 3 and salt including>
whole string then been encode as base64. using hash with salt make key tamper protected.

###Initial Data Loading (5 keys)
- they are saved as 5 sql statement in data.sql and getting loaded when application
started and display on the console. 
 
###Running Test
```
export  APPLICATION_WEATHERMAPAPIKEY="openweather api key" or equlent windows command 
# instead of export you can update application.yml in test/resources with key.

./mvnw test 
```
###How to Run
```
 ./mvnw  spring-boot:run -Dspring-boot.run.jvmArguments="-Dapplication.weatherMapApiKey=<your api key>"

```
or  you can update application.yml and run without -Dspring-boot.run.jvmArguments
####NOTES

- relighting is done in really simple way and wont accurate when have lot of multiple request at time
  need to use distribute in memory couple with with atomic counter type of implementation.  
- remove the validation of country code to accommodate sample request (uk is not standard ISO code)
- include api_key as part of the parameter instead of header variable for simplicity


# TDD
Like to get everyonce comments around sample this TDD following points structure / simplicity of the code.
### Requirements 

1.An API Key is rate limited to 5 weather reports an hour. After that your service should respond in a way which communicates that the hourly limit has been exceeded. Create 5 API Keys. Pick a convention for handling them that you like; using simple string constants is fine. 

2.	Have a URL that accepts both a city name and country name. Based upon these inputs, and the API Key, your service should decide whether or not to call the OpenWeatherMap name service. return only weather data you need to return to the client is the description field from the weather JSON result. 
3.	Reject requests with invalid input or missing API Keys.
4.	Store the data from openweathermap.org into H2 DB.
5.	The API will query the data from H2 if exits and have TTL 5 hours. 


# Design
Current design based on restful api design which will comprise of following layers:
- Rest Layer (transport / http and handlers)
    WeatherController which handle the user request and is also responsible for passing
    correct http status code to client 
- Service Layer
    It acts as service/logic layer and also interact with other services, such as;
    ApiKeyService - dealing with api key related logic including key schema and throttling
    WeatherService - core application logic around the weather service.
    also implement TTL for weather report cache in the db.
    OpenWeatherApiService - communicate with external Open Weather Api and provide data 
- Data Access Layer
    perforce database related operation
    ApiKeyRepository - store for api key and throttle counters
    WeatherRepository - dealing with cache weather information.  
  
#### API Key scheme
This is the current implementation:
1. use dot notation to divide field into hold following information.
2 key schema:
```
<base 64 encoded email>.<api key issue time in epoch>.<expire time in  epoch>.<hash of first 3 portions including dots. Add salt for sucrity before hashing>
whole string then been encode as base64. using hash with salt make key tamper protected.
expire time 0 mean never expire.
example: 

```
### Initial Data Loading (5 keys)
- They are saved as 5 sql statement in data.sql and getting loaded when application
started and display on the console. 
 
### Running Test
```
export  APPLICATION_WEATHERMAPAPIKEY="openweather api key" or equlent windows command 
# instead of export you can update application.yml in test/resources with key.

./mvnw test 
```
### How to Run
```
 # -DINIT_MODE=always  // allow load keys in data.sql (for the first time). then need to chanege to "never" in spring.sql.init.mode property  to avoid duplicate key viloation
 # -DAPPLICATION_WEATHERMAPAPIKEY=<apiid>  //contain valid apiid for openweather service ( need to obtain from the service)
 # you can get rid of the  -Dspring-boot.run.jvmArguments and add correct values to the application.yml file
 ./mvnw  spring-boot:run -Dspring-boot.run.jvmArguments="-DAPPLICATION_WEATHERMAPAPIKEY=<your api key> -DINIT_MODE=always"

```

Request for testing (postman or browser)
###### Five Valid api keys will be print on to the console
```

http://localhost:8080/api/weather/current?country=au&city=melbourne&api_key=ZFhObGNqSkFkMlZoZEdobGNpMWxlR0Z0Y0d4bExtTnZiUzVoZFE9PS4xNjMwOTE0OTc1NTQ2LjAuMUFBOUJBNjA0QzY1QTkzOUVDODY3NkFGQjNFMTc1OUY=

```
#### NOTES

- Throttling is done in really simple way and won't be accurate when having multiple requests at time.
  Need to use distribute in memory couple with with atomic counter type of implementation for accuracy.  
- Include api_key as part of the parameter instead of header variable for simplicity
- db will be persisted in ./h2-db folder (change the path in application.yml or pass as environment variable)



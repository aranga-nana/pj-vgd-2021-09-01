CREATE TABLE IF NOT EXISTS api_key (
    id   INTEGER      NOT NULL AUTO_INCREMENT,
    api_key VARCHAR(256) NOT NULL ,
    invocations INTEGER default (0),
    updated TIMESTAMP ,
    PRIMARY KEY (id),
    UNIQUE KEY api_key (api_key)

);
CREATE TABLE IF NOT EXISTS weather (
    id   INTEGER      NOT NULL AUTO_INCREMENT,
    country VARCHAR(25) NOT NULL ,
    city VARCHAR(25) NOT NULL ,
    updated TIMESTAMP,
    description VARCHAR(256) ,
    PRIMARY KEY (id) ,
    UNIQUE KEY country_city (country,city)

);

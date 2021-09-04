CREATE TABLE IF NOT EXISTS api_key (
    id   INTEGER      NOT NULL AUTO_INCREMENT,
    api_key VARCHAR(256) NOT NULL ,
    invocations INTEGER default (0),
    updated DATE ,
    PRIMARY KEY (id)

);

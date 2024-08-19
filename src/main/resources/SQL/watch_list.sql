CREATE TABLE watch_list (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    type VARCHAR(255),
    date_created TIMESTAMP
);


ALTER TABLE watch_list add movement VARCHAR(50);

ALTER TABLE watch_list add price NUMERIC (10,2) DEFAULT 0;

ALTER TABLE watch_list add price_change NUMERIC (10,2) DEFAULT 0;

ALTER TABLE watch_list add volume NUMERIC (10,2) DEFAULT 0;

ALTER TABLE watch_list add volume_change NUMERIC (10,2) DEFAULT 0;

ALTER TABLE watch_list
ALTER COLUMN price TYPE DOUBLE PRECISION,
ALTER COLUMN price_change TYPE DOUBLE PRECISION,
ALTER COLUMN volume TYPE DOUBLE PRECISION,
ALTER COLUMN volume_change TYPE DOUBLE PRECISION;

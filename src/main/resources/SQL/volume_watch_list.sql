CREATE TABLE volume_watchlist  (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    type VARCHAR(255),
    date_created TIMESTAMP,
    volume INTEGER
);


ALTER TABLE volume_watchlist
ADD COLUMN daily_increase DOUBLE PRECISION,
ADD COLUMN weekly_increase DOUBLE PRECISION;


ALTER TABLE volume_watchlist
ADD COLUMN price DOUBLE PRECISION;

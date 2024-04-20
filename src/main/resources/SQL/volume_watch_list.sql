CREATE TABLE volume_watchlist  (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    type VARCHAR(255),
    date_created TIMESTAMP,
    volume INTEGER
);

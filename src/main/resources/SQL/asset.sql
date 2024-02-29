CREATE TABLE ASSETS (
    name VARCHAR(255),
    marketPrice NUMERIC,
    macd NUMERIC,
    priceRateOfChange NUMERIC,
    relativeStrengthIndex NUMERIC,
    stochasticOscillator NUMERIC,
    indicatorScores JSONB,
    signal VARCHAR(255),
    date TIMESTAMP,
    type VARCHAR(255)
);

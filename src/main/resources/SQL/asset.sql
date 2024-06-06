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





CREATE TABLE assets (
    name character varying(255),
    market_price numeric,
    macd numeric,
    price_rate_of_change numeric,
    relative_strength_index numeric,
    stochastic_oscillator numeric,
    indicator_scores jsonb,
    signal character varying(255),
    date timestamp without time zone,
    type character varying(255),
    id uuid NOT NULL
);



###Refactored ASSETS
CREATE TABLE ASSETS (
    asset_id uuid  PRIMARY KEY,
    name character varying(255),
    type character varying(255),
    date_created timestamp without time zone
);
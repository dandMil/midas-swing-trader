CREATE TABLE ASSET_SIGNALS (
    id UUID PRIMARY KEY,
    asset_id UUID,
    market_price NUMERIC,
    macd NUMERIC,
    price_rate_of_change NUMERIC,
    relative_strength_index NUMERIC,
    stochastic_oscillator NUMERIC,
    indicator_scores JSONB,
    signal VARCHAR(255),
    date_created TIMESTAMP WITHOUT TIME ZONE,
    FOREIGN KEY (asset_id) REFERENCES ASSETS(asset_id)
);

CREATE TABLE trade_recommendation (
    id UUID PRIMARY KEY,
    asset_id UUID,
    entry_price DOUBLE PRECISION NOT NULL,
    stop_loss DOUBLE PRECISION NOT NULL,
    take_profit DOUBLE PRECISION NOT NULL,
    recommendation_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    strategy VARCHAR(50),
    FOREIGN KEY (asset_id) REFERENCES ASSETS(asset_id)
);

GRANT ALL PRIVILEGES ON TABLE trade_recommendation TO midas;

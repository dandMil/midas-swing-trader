CREATE TABLE asset_volume (
    id UUID PRIMARY KEY,
    asset_id UUID,
    volume INTEGER,
    daily_increase DOUBLE PRECISION,
    weekly_increase DOUBLE PRECISION,
    price DOUBLE PRECISION,
    date_created timestamp without time zone,
    FOREIGN KEY (asset_id) REFERENCES ASSETS(asset_id)
);

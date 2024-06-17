CREATE TABLE PORTFOLIO (
    id UUID PRIMARY KEY,
    name character varying(255),
    type character varying(255),
    shares INTEGER,
    price DOUBLE PRECISION,
    date_created timestamp without time zone
);

GRANT ALL PRIVILEGES ON TABLE PORTFOLIO TO midas;

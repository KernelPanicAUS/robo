create table security
(
    id           UUID PRIMARY KEY,
    isin         TEXT                     NOT NULL,
    symbol       TEXT                     NOT NULL,
    company_name TEXT                     NOT NULL,
    country      TEXT                     NOT NULL,
    exchange     TEXT                     NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    UNIQUE (isin, symbol)
);
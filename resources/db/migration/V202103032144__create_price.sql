create table price
(
    id          UUID PRIMARY KEY,
    security_id UUID                     NOT NULL,
    amount      NUMERIC(5, 2)            NOT NULL,
    date        DATE                     NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    UNIQUE (security_id, date, amount),
--     PRIMARY KEY (id),
    CONSTRAINT fk_security
        FOREIGN KEY (security_id)
            REFERENCES security (id)
);
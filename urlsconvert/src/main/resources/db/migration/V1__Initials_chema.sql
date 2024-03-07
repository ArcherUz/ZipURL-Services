CREATE TABLE IF NOT EXISTS url_long_to_short(
    id BIGSERIAL PRIMARY KEY,
    long_url TEXT NOT NULL,
    short_url VARCHAR(255) NOT NULL,
    creation_date TIMESTAMP NOT NULL,
    title VARCHAR(255),
    avatar VARCHAR(255)
);


CREATE TABLE IF NOT EXISTS customer (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS customer_urls(
    customer_id INTEGER NOT NULL,
    url_id BIGINT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(id),
    FOREIGN KEY (url_id) REFERENCES url_long_to_short(id),
    PRIMARY key (customer_id, url_id)
);
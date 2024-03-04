CREATE TABLE IF NOT EXISTS customer (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS url_long_to_short(
    id BIGSERIAL PRIMARY KEY,
    long_url TEXT NOT NULL,
    short_url VARCHAR(255) UNIQUE NOT NULL,
    creation_date TIMESTAMP NOT NULL,
    title VARCHAR(255),
    avatar VARCHAR(255),
    customer_id INTEGER,
    FOREIGN KEY (customer_id) REFERENCES customer (id)
);

CREATE TABLE IF NOT EXISTS url_short_to_long(
    id BIGSERIAL PRIMARY KEY,
    short_url VARCHAR(255) UNIQUE NOT NULL,
    long_url TEXT NOT NULL,
    FOREIGN KEY (short_url) REFERENCES url_long_to_short(short_url)
);
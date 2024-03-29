DROP TABLE IF EXISTS hits;
DROP TABLE IF EXISTS apps;

CREATE TABLE IF NOT EXISTS apps(
    app_id BIGINT generated by default as identity primary key,
    app_name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS hits(
    hit_id BIGINT generated by default as identity primary key,
    app_id BIGINT REFERENCES apps(app_id) ON DELETE CASCADE NOT NULL,
    uri VARCHAR(200) NOT NULL,
    ip VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL
);
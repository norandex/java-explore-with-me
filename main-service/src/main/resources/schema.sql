CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS locations
(
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    lat FLOAT NOT NULL,
    lon FLOAT NOT NULL
);

CREATE TABLE IF NOT EXISTS categories
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);


CREATE TABLE IF NOT EXISTS events
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    annotation VARCHAR(2000),
    category_id BIGINT,
    confirmed_requests BIGINT,
    description TEXT,
    event_date TIMESTAMP WITHOUT TIME ZONE,
    created_on TIMESTAMP WITHOUT TIME ZONE,
    initiator_id BIGINT,
    location_id BIGINT,
    paid BOOLEAN,
    participant_limit BIGINT,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN,
    state VARCHAR(120),
    title VARCHAR(120),
    views BIGINT,
    CONSTRAINT fk_events_to_categories FOREIGN KEY (category_id) REFERENCES categories (ID) ON DELETE CASCADE,
    CONSTRAINT fk_events_to_locations FOREIGN KEY (location_id) REFERENCES locations (ID) ON DELETE CASCADE,
    CONSTRAINT fk_events_to_users FOREIGN KEY (initiator_id) REFERENCES users (ID) ON DELETE CASCADE
    );


CREATE TABLE IF NOT EXISTS requests
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    created TIMESTAMP WITHOUT TIME ZONE,
    event BIGINT NOT NULL,
    requester BIGINT NOT NULL,
    status VARCHAR(127) NOT NULL,
    CONSTRAINT fk_requests_to_events FOREIGN KEY (event) REFERENCES events (id) ON DELETE CASCADE,
    CONSTRAINT fk_requests_to_user FOREIGN KEY (requester) REFERENCES users (ID) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS compilations
(
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    pinned BOOLEAN,
    title  VARCHAR(550) UNIQUE
    );

CREATE TABLE IF NOT EXISTS compilation_events
(
    event_id       BIGINT,
    compilation_id BIGINT,
    CONSTRAINT fk_compilations FOREIGN KEY (compilation_id) REFERENCES compilations (id),
    CONSTRAINT fk_events FOREIGN KEY (event_id) REFERENCES events (id)
    );

CREATE TABLE IF NOT EXISTS likes(
    user_id INTEGER NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    event_id INTEGER NOT NULL REFERENCES events (id) ON DELETE CASCADE,
    is_like BOOLEAN,
    created_on TIMESTAMP WITHOUT TIME ZONE,
    primary key (user_id, event_id)
);






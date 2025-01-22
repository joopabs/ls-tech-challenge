CREATE TABLE speech
(
    id               BIGSERIAL PRIMARY KEY,
    content          TEXT         NOT NULL,
    author           VARCHAR(255) NOT NULL,
    speech_date      TIMESTAMPTZ  NOT NULL,
    create_date_time TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date_time TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE speech_keyword
(
    speech_id BIGINT       NOT NULL REFERENCES speech (id) ON DELETE CASCADE,
    keyword   VARCHAR(255) NOT NULL,
    PRIMARY KEY (speech_id, keyword)
);

-- Insert seed data into the speech table
INSERT INTO speech (content, author, speech_date)
VALUES ('Equality and justice for all', 'John Doe', '2023-01-01T10:00:00Z'),
       ('Human rights are non-negotiable', 'Jane Smith', '2023-02-15T15:30:00Z'),
       ('Economic stability is key to peace', 'John Doe', '2023-03-10T12:00:00Z');

-- Insert seed data into the speech_keyword table
INSERT INTO speech_keyword (speech_id, keyword)
VALUES (1, 'equality'),
       (1, 'justice'),
       (2, 'rights'),
       (2, 'freedom'),
       (3, 'economy'),
       (3, 'peace');
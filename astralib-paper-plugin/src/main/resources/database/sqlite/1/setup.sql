CREATE TABLE IF NOT EXISTS language
(
    player_uuid VARCHAR(36) DEFAULT (UUID()),
    lang VARCHAR(100)
);
CREATE TABLE IF NOT EXISTS player_names
(
    player_uuid VARCHAR(36) PRIMARY KEY,
    name VARCHAR(16)
);

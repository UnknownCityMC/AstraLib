CREATE TABLE IF NOT EXISTS language
(
    player_uuid VARCHAR(36) DEFAULT (UUID()),
    lang VARCHAR(100)
)
CREATE TABLE cards (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    set_name VARCHAR(255) NOT NULL,
    rarity VARCHAR(100),
    image_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE price_snapshots (
    id BIGSERIAL PRIMARY KEY,
    card_id VARCHAR(64) NOT NULL REFERENCES cards(id) ON DELETE CASCADE,
    marketplace VARCHAR(50) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    recorded_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_price_snapshots_card_recorded
    ON price_snapshots(card_id, recorded_at DESC);
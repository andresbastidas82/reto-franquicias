CREATE TABLE IF NOT EXISTS franchise (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS branch (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    franchise_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    CONSTRAINT fk_branch_franchise FOREIGN KEY (franchise_id) REFERENCES franchise(id) ON DELETE CASCADE,
    CONSTRAINT uk_branch_name_franchise UNIQUE (name, franchise_id)
);

CREATE TABLE IF NOT EXISTS product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    branch_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    CONSTRAINT fk_product_branch FOREIGN KEY (branch_id) REFERENCES branch(id) ON DELETE CASCADE,
    CONSTRAINT uk_product_name_branch UNIQUE (name, branch_id)
);
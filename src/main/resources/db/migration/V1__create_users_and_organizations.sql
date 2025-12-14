-- Create organizations table
CREATE TABLE organizations (
    clerk_organization_id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255),
    logo_url TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create users table
CREATE TABLE users (
    clerk_user_id VARCHAR(255) PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    profile_image_url TEXT,
    microsoft_access_token TEXT,
    microsoft_refresh_token TEXT,
    microsoft_token_expiry TIMESTAMP,
    organization_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_organization FOREIGN KEY (organization_id) REFERENCES organizations(clerk_organization_id) ON DELETE SET NULL
);

-- Create indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_organization ON users(organization_id);
CREATE INDEX idx_organizations_slug ON organizations(slug);

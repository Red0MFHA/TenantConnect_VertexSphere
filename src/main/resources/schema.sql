
-- Users table (for both Owners and Tenants)
CREATE TABLE users (
                       user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       full_name VARCHAR(100) NOT NULL,
                       user_type VARCHAR(10) CHECK(user_type IN ('owner', 'tenant')) NOT NULL,
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                       is_active BOOLEAN DEFAULT 1
);


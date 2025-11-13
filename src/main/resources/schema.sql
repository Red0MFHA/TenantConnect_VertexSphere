
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

-- Properties table
CREATE TABLE properties (
                        property_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        owner_id INTEGER NOT NULL,
                        property_name VARCHAR(255) NOT NULL,
                        address TEXT NOT NULL,
                        city VARCHAR(100) NOT NULL,
                        state VARCHAR(100),
                        zip_code VARCHAR(20),
                        property_type VARCHAR(50), -- Apartment, House, etc.
                        rent_amount DECIMAL(10,2) NOT NULL,
                        security_deposit DECIMAL(10,2),
                        status VARCHAR(20) CHECK(status IN ('vacant', 'occupied')) DEFAULT 'vacant' NOT NULL,
                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (owner_id) REFERENCES users(user_id) ON DELETE CASCADE
);


-- Making Contracts | so the need for Property Assignment is no longer; all things added to property Controller
-- Contracts table
CREATE TABLE contracts (
                           contract_id INTEGER PRIMARY KEY AUTOINCREMENT,
                           property_id INTEGER NOT NULL,
                           tenant_id INTEGER NOT NULL,
                           start_date DATE NOT NULL,
                           end_date DATE NOT NULL,
                           monthly_rent DECIMAL(10,2) NOT NULL,
                           security_deposit DECIMAL(10,2),
                           contract_status VARCHAR(20) CHECK(contract_status IN ('pending', 'active', 'terminated', 'rejected')) DEFAULT 'pending',
                           created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (property_id) REFERENCES properties(property_id) ON DELETE CASCADE,
                           FOREIGN KEY (tenant_id) REFERENCES users(user_id) ON DELETE CASCADE
);

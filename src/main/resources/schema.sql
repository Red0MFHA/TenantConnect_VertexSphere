
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

--Property Assignment
CREATE TABLE property_assignments (
                                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                                      owner_id INTEGER NOT NULL,
                                      property_id INTEGER NOT NULL,
                                      property_name TEXT NOT NULL,
                                      tenant_id INTEGER,
                                      contract_id INTEGER,

                                      FOREIGN KEY (owner_id) REFERENCES users(user_id) ON DELETE CASCADE,
                                      FOREIGN KEY (property_id) REFERENCES properties(property_id) ON DELETE CASCADE,
                                      FOREIGN KEY (tenant_id) REFERENCES users(user_id) ON DELETE SET NULL,
                                      FOREIGN KEY (contract_id) REFERENCES contracts(contract_id) ON DELETE CASCADE
);


-- Rent payments table
CREATE TABLE payments (
                               payment_id INTEGER PRIMARY KEY AUTOINCREMENT,
                               contract_id INTEGER NOT NULL,
                               payment_date DATE,
                               due_date DATE NOT NULL,
                               amount_due DECIMAL(10,2) NOT NULL,
                               amount_paid DECIMAL(10,2),
                               payment_status VARCHAR(20) CHECK(payment_status IN ('pending', 'paid', 'overdue', 'extension_requested')) DEFAULT 'pending',
                               paid_date DATE,
                               created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (contract_id) REFERENCES contracts(contract_id) ON DELETE CASCADE
);

-- Payment extension requests table
CREATE TABLE payment_extensions (
                                    extension_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    payment_id INTEGER NOT NULL,
                                    tenant_id INTEGER NOT NULL,
                                    current_due_date DATE NOT NULL,
                                    requested_due_date DATE NOT NULL,
                                    reason TEXT,
                                    status VARCHAR(20) CHECK(status IN ('pending', 'approved', 'rejected')) DEFAULT 'pending',
                                    responded_at DATETIME,
                                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                    FOREIGN KEY (payment_id) REFERENCES payments(payment_id) ON DELETE CASCADE,
                                    FOREIGN KEY (tenant_id) REFERENCES users(user_id) ON DELETE CASCADE
);

DROP TABLE payment_extensions;
-- Complaints table
CREATE TABLE complaints (
                            complaint_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            tenant_id INTEGER NOT NULL,
                            property_id INTEGER NOT NULL,
                            title VARCHAR(255) NOT NULL,
                            description TEXT NOT NULL,
                            category VARCHAR(50), -- Maintenance, Urgent, General, etc.
                            status VARCHAR(20) CHECK(status IN ('open', 'in_progress', 'resolved', 'rejected')) DEFAULT 'open',
                            priority VARCHAR(20) CHECK(priority IN ('low', 'medium', 'high', 'urgent')) DEFAULT 'medium',
                            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                            resolved_at DATETIME,
                            owner_notes TEXT,
                            FOREIGN KEY (tenant_id) REFERENCES users(user_id) ON DELETE CASCADE,
                            FOREIGN KEY (property_id) REFERENCES properties(property_id) ON DELETE CASCADE
);

-- Notifications table
CREATE TABLE notifications (
                               notification_id INTEGER PRIMARY KEY AUTOINCREMENT,
                               user_id INTEGER NOT NULL,
                               title VARCHAR(255) NOT NULL,
                               message TEXT NOT NULL,
                               notification_type VARCHAR(50), -- payment_reminder, complaint_update, contract_invitation, etc.
                               is_read BOOLEAN DEFAULT 0,
                               related_entity_type VARCHAR(50), -- payment, complaint, contract, etc.
                               related_entity_id INTEGER,
                               created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Notification settings table
CREATE TABLE notification_settings (
                                       setting_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                       owner_id INTEGER NOT NULL,
                                       notification_type VARCHAR(50) NOT NULL,
                                       is_enabled BOOLEAN DEFAULT 1,
                                       frequency_hours INTEGER DEFAULT 24, -- How often to send reminders
                                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                       updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                       FOREIGN KEY (owner_id) REFERENCES users(user_id) ON DELETE CASCADE,
                                       UNIQUE(owner_id, notification_type)
);



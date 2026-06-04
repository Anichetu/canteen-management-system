-- ============================================
-- Canteen Management System - Seed Data
-- DB: canteen_data
-- Run AFTER Spring Boot starts (tables auto-created)
-- ============================================

USE canteen_data;

-- Default Admin / Staff Users
-- Password stored as plain text (replace with hashed in production)
INSERT INTO users (username, password, full_name, role, active) VALUES
('admin',   'admin123',   'Admin Owner',     'ADMIN',           true),
('waiter1', 'waiter123',  'Suresh Waiter',   'WAITER',          true),
('waiter2', 'waiter123',  'Priya Waiter',    'WAITER',          true),
('kitchen', 'kitchen123', 'Ramesh Kitchen',  'KITCHEN_MANAGER', true)
ON DUPLICATE KEY UPDATE username = username;

-- Sample Menu Items
INSERT INTO menu_items (name, category, price, available, description) VALUES
('Paneer Masala',    'Main Course', 180.00, true, 'Rich creamy paneer in spiced gravy'),
('Paneer Handi',     'Main Course', 200.00, true, 'Slow-cooked paneer in handi style'),
('Paneer Bhurji',    'Main Course', 160.00, true, 'Scrambled paneer with spices'),
('Chicken Masala',   'Main Course', 240.00, true, 'Spiced chicken curry'),
('Chicken Handi',    'Main Course', 260.00, true, 'Handi style chicken'),
('Veg Pulav',        'Rice',        140.00, true, 'Fragrant vegetable rice'),
('Chicken Biryani',  'Rice',        220.00, true, 'Dum cooked biryani'),
('Chapati',          'Bread',        20.00, true, 'Fresh wheat chapati'),
('Butter Roti',      'Bread',        25.00, true, 'Butter coated roti'),
('Naan',             'Bread',        35.00, true, 'Tandoor baked naan'),
('Dal Tadka',        'Main Course', 120.00, true, 'Yellow lentil with tadka'),
('Veg Soup',         'Soup',         80.00, true, 'Fresh garden vegetable soup'),
('Tomato Soup',      'Soup',         75.00, true, 'Classic tomato cream soup'),
('Oreo Shake',       'Drinks',      120.00, true, 'Thick Oreo milkshake'),
('Mango Lassi',      'Drinks',       90.00, true, 'Sweet mango yogurt drink'),
('Cold Coffee',      'Drinks',      100.00, true, 'Creamy cold coffee'),
('Fresh Lime Soda',  'Drinks',       60.00, true, 'Sweet / salted lime soda'),
('Masala Papad',     'Starters',     50.00, true, 'Crispy papad with masala'),
('Veg Manchurian',   'Starters',    130.00, true, 'Crispy veg balls in sauce'),
('French Fries',     'Starters',     90.00, true, 'Golden crispy fries')
ON DUPLICATE KEY UPDATE name = name;

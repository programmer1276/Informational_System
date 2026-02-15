INSERT INTO roles (code, name) VALUES
                                   ('ADMIN','Administrator'),
                                   ('MODERATOR','Moderator'),
                                   ('USER','User')
ON CONFLICT (code) DO NOTHING@@

-- bcrypt("password")
INSERT INTO users (email,password_hash,name,room,notif_email_on,notif_push_on)
VALUES
    ('alice@example.com','$2a$10$9pQvG7yYlXl7u6Qy1cG3uO8t7o9xC0P9oGzQqE9b6GmY7G3tqfQK2','Alice','101', TRUE, TRUE),
    ('bob@example.com','$2a$10$9pQvG7yYlXl7u6Qy1cG3uO8t7o9xC0P9oGzQqE9b6GmY7G3tqfQK2','Bob','102', TRUE, FALSE),
    ('vlad@gmail.com','$2a$10$9pQvG7yYlXl7u6Qy1cG3uO8t7o9xC0P9oGzQqE9b6GmY7G3tqfQK2','Vlad','201', TRUE, TRUE)
ON CONFLICT (email) DO NOTHING@@

INSERT INTO user_roles (user_id, role_id)
SELECT u.user_id, r.role_id
FROM users u
JOIN roles r ON r.code = 'ADMIN'
WHERE u.email IN ('alice@example.com', 'vlad@gmail.com')
ON CONFLICT DO NOTHING@@

INSERT INTO user_roles (user_id, role_id)
SELECT u.user_id, r.role_id
FROM users u
JOIN roles r ON r.code = 'USER'
WHERE u.email = 'bob@example.com'
ON CONFLICT DO NOTHING@@

INSERT INTO user_roles (user_id, role_id)
SELECT u.user_id, r.role_id
FROM users u
JOIN roles r ON r.code = 'MODERATOR'
WHERE u.email = 'bob@example.com'
ON CONFLICT DO NOTHING@@

INSERT INTO fridges (name, location, invite_required, owner_id)
SELECT 'Kitchen Fridge', 'Apartment 1, Kitchen', FALSE,
       (SELECT user_id FROM users WHERE email = 'alice@example.com' LIMIT 1)
WHERE NOT EXISTS (
    SELECT 1 FROM fridges WHERE name = 'Kitchen Fridge' AND location = 'Apartment 1, Kitchen'
)@@

SELECT fn_add_fridge_member(
           (SELECT fridge_id FROM fridges WHERE name = 'Kitchen Fridge' AND location = 'Apartment 1, Kitchen' LIMIT 1),
           (SELECT user_id FROM users WHERE email = 'alice@example.com' LIMIT 1),
           TRUE
       )
WHERE NOT EXISTS (
    SELECT 1 FROM fridge_memberships fm
    JOIN users u ON u.user_id = fm.user_id
    JOIN fridges f ON f.fridge_id = fm.fridge_id
    WHERE u.email = 'alice@example.com' AND f.name = 'Kitchen Fridge' AND fm.left_at IS NULL
)@@

SELECT fn_add_fridge_member(
           (SELECT fridge_id FROM fridges WHERE name = 'Kitchen Fridge' AND location = 'Apartment 1, Kitchen' LIMIT 1),
           (SELECT user_id FROM users WHERE email = 'bob@example.com' LIMIT 1),
           FALSE
       )
WHERE NOT EXISTS (
    SELECT 1 FROM fridge_memberships fm
    JOIN users u ON u.user_id = fm.user_id
    JOIN fridges f ON f.fridge_id = fm.fridge_id
    WHERE u.email = 'bob@example.com' AND f.name = 'Kitchen Fridge' AND fm.left_at IS NULL
)@@

INSERT INTO zones (fridge_id, name, capacity_units, capacity_volume_l, is_active, sort_order)
SELECT f.fridge_id, 'Top Shelf', 20, 50.0, TRUE, 1
FROM fridges f
WHERE f.name = 'Kitchen Fridge' AND f.location = 'Apartment 1, Kitchen'
  AND NOT EXISTS (
      SELECT 1 FROM zones z WHERE z.fridge_id = f.fridge_id AND z.name = 'Top Shelf'
  )@@

INSERT INTO zones (fridge_id, name, capacity_units, capacity_volume_l, is_active, sort_order)
SELECT f.fridge_id, 'Bottom Drawer', 10, 30.0, TRUE, 2
FROM fridges f
WHERE f.name = 'Kitchen Fridge' AND f.location = 'Apartment 1, Kitchen'
  AND NOT EXISTS (
      SELECT 1 FROM zones z WHERE z.fridge_id = f.fridge_id AND z.name = 'Bottom Drawer'
  )@@

INSERT INTO categories (name, perishable_days_default)
VALUES
    ('Dairy', 7),
    ('Veggies', 10),
    ('Canned', NULL)
ON CONFLICT (name) DO NOTHING@@

SELECT fn_add_product(
           (SELECT user_id FROM users WHERE email = 'alice@example.com' LIMIT 1),
           (SELECT z.zone_id FROM zones z
            JOIN fridges f ON f.fridge_id = z.fridge_id
            WHERE z.name = 'Top Shelf' AND f.name = 'Kitchen Fridge' LIMIT 1),
           (SELECT category_id FROM categories WHERE name = 'Dairy' LIMIT 1),
           'Strawberry Yogurt 150g',
           '0123456789012',
           NULL,
           NULL,
           FALSE,
           'ACTIVE'
       )
WHERE NOT EXISTS (
    SELECT 1 FROM products p
    WHERE p.name = 'Strawberry Yogurt 150g'
)@@

SELECT fn_add_product(
           (SELECT user_id FROM users WHERE email = 'bob@example.com' LIMIT 1),
           (SELECT z.zone_id FROM zones z
            JOIN fridges f ON f.fridge_id = z.fridge_id
            WHERE z.name = 'Bottom Drawer' AND f.name = 'Kitchen Fridge' LIMIT 1),
           (SELECT category_id FROM categories WHERE name = 'Veggies' LIMIT 1),
           'Carrots (1 kg)',
           NULL,
           now() + interval '8 days',
           NULL,
           FALSE,
           'ACTIVE'
       )
WHERE NOT EXISTS (
    SELECT 1 FROM products p
    WHERE p.name = 'Carrots (1 kg)'
)@@

SELECT fn_add_product(
           (SELECT user_id FROM users WHERE email = 'bob@example.com' LIMIT 1),
           (SELECT z.zone_id FROM zones z
            JOIN fridges f ON f.fridge_id = z.fridge_id
            WHERE z.name = 'Top Shelf' AND f.name = 'Kitchen Fridge' LIMIT 1),
           (SELECT category_id FROM categories WHERE name = 'Canned' LIMIT 1),
           'Canned Beans 400g',
           NULL,
           NULL,
           NULL,
           FALSE,
           'ACTIVE'
       )
WHERE NOT EXISTS (
    SELECT 1 FROM products p
    WHERE p.name = 'Canned Beans 400g'
)@@

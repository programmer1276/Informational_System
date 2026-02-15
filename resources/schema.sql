-- ===================== ENUM TYPES =====================
DO $$ BEGIN
    CREATE TYPE status_enum AS ENUM ('ACTIVE','EATEN','TAKEN','DISPOSED','EXPIRED');
EXCEPTION WHEN duplicate_object THEN NULL;
END $$@@

DO $$ BEGIN
    CREATE TYPE event_type_enum AS ENUM ('MOVE','EXTEND','STATUS','EDIT');
EXCEPTION WHEN duplicate_object THEN NULL;
END $$@@

DO $$ BEGIN
    CREATE TYPE channel_enum AS ENUM ('EMAIL','PUSH');
EXCEPTION WHEN duplicate_object THEN NULL;
END $$@@

DO $$ BEGIN
    CREATE TYPE template_enum AS ENUM ('TTL_T3','TTL_T1','TTL_0','INCIDENT','OTHER');
EXCEPTION WHEN duplicate_object THEN NULL;
END $$@@

DO $$ BEGIN
    CREATE TYPE notif_status_enum AS ENUM ('SENT','FAILED');
EXCEPTION WHEN duplicate_object THEN NULL;
END $$@@


-- ======================= TABLES ========================
CREATE TABLE IF NOT EXISTS roles (
                                     role_id BIGSERIAL PRIMARY KEY,
                                     code TEXT UNIQUE NOT NULL CHECK (code IN ('ADMIN','MODERATOR','USER')),
                                     name TEXT NOT NULL
)@@

CREATE TABLE IF NOT EXISTS users (
                                     user_id BIGSERIAL PRIMARY KEY,
                                     email TEXT UNIQUE NOT NULL,
                                     password_hash TEXT NOT NULL,
                                     name TEXT NOT NULL,
                                     room TEXT,
                                     notif_email_on BOOLEAN NOT NULL DEFAULT TRUE,
                                     notif_push_on BOOLEAN NOT NULL DEFAULT TRUE,
                                     created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                     blocked_at TIMESTAMPTZ
)@@

DO $$
DECLARE
    email_type text;
BEGIN
    SELECT format_type(a.atttypid, a.atttypmod)
    INTO email_type
    FROM pg_attribute a
    JOIN pg_class c ON c.oid = a.attrelid
    WHERE c.relname = 'users'
      AND a.attname = 'email'
      AND a.attnum > 0
      AND NOT a.attisdropped;

    IF email_type = 'bytea' THEN
        EXECUTE 'ALTER TABLE users ALTER COLUMN email TYPE text USING convert_from(email, ''UTF8'')';
    ELSE
        EXECUTE 'ALTER TABLE users ALTER COLUMN email TYPE text USING email::text';
    END IF;
EXCEPTION
    WHEN others THEN
        -- Ignore if column already converted or conversion not needed
        NULL;
END $$@@

-- Define user_roles without inline REFERENCES to avoid FK creation order issues
CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id BIGINT NOT NULL,
                                          role_id BIGINT NOT NULL,
                                          PRIMARY KEY (user_id, role_id)
)@@

CREATE TABLE IF NOT EXISTS fridges (
                                       fridge_id BIGSERIAL PRIMARY KEY,
                                       name TEXT NOT NULL,
                                       location TEXT,
                                       created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                       invite_required BOOLEAN NOT NULL DEFAULT FALSE,
                                       owner_id BIGINT REFERENCES users(user_id) ON DELETE SET NULL
)@@

ALTER TABLE fridges
    ADD COLUMN IF NOT EXISTS owner_id BIGINT REFERENCES users(user_id) ON DELETE SET NULL@@

CREATE TABLE IF NOT EXISTS fridge_memberships (
                                                  fridge_id BIGINT NOT NULL REFERENCES fridges(fridge_id) ON DELETE CASCADE,
                                                  user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
                                                  is_moderator BOOLEAN NOT NULL DEFAULT FALSE,
                                                  joined_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                                  left_at TIMESTAMPTZ,
                                                  PRIMARY KEY (fridge_id, user_id),
                                                  CONSTRAINT chk_fridge_membership_dates CHECK (left_at IS NULL OR left_at >= joined_at)
)@@

CREATE TABLE IF NOT EXISTS zones (
                                     zone_id BIGSERIAL PRIMARY KEY,
                                     fridge_id BIGINT NOT NULL REFERENCES fridges(fridge_id) ON DELETE CASCADE,
                                     name TEXT NOT NULL,
                                     capacity_units INT NOT NULL CHECK (capacity_units >= 0),
                                     capacity_volume_l NUMERIC(10,2),
                                     is_active BOOLEAN NOT NULL DEFAULT TRUE,
                                     sort_order INT NOT NULL DEFAULT 0
)@@

CREATE TABLE IF NOT EXISTS categories (
                                          category_id BIGSERIAL PRIMARY KEY,
                                          name TEXT UNIQUE NOT NULL,
                                          perishable_days_default INT CHECK (perishable_days_default IS NULL OR perishable_days_default >= 0)
)@@

CREATE TABLE IF NOT EXISTS products (
                                        product_id BIGSERIAL PRIMARY KEY,
                                        owner_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
                                        zone_id BIGINT NOT NULL REFERENCES zones(zone_id) ON DELETE CASCADE,
                                        category_id BIGINT REFERENCES categories(category_id) ON DELETE SET NULL,
                                        name TEXT NOT NULL,
                                        barcode TEXT,
                                        expires_at TIMESTAMPTZ,
                                        placed_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                        locked BOOLEAN NOT NULL DEFAULT FALSE,
                                        status status_enum NOT NULL DEFAULT 'ACTIVE'
)@@

CREATE TABLE IF NOT EXISTS product_history (
                                               history_id BIGSERIAL PRIMARY KEY,
                                               product_id BIGINT NOT NULL REFERENCES products(product_id) ON DELETE CASCADE,
                                               event_type event_type_enum NOT NULL,
                                               from_zone_id BIGINT REFERENCES zones(zone_id) ON DELETE SET NULL,
                                               to_zone_id BIGINT REFERENCES zones(zone_id) ON DELETE SET NULL,
                                               old_expires_at TIMESTAMPTZ,
                                               new_expires_at TIMESTAMPTZ,
                                               comment TEXT,
                                               actor_id BIGINT REFERENCES users(user_id) ON DELETE SET NULL,
                                               created_at TIMESTAMPTZ NOT NULL DEFAULT now()
)@@

CREATE TABLE IF NOT EXISTS notifications (
                                             notification_id BIGSERIAL PRIMARY KEY,
                                             user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
                                             product_id BIGINT REFERENCES products(product_id) ON DELETE SET NULL,
                                             channel channel_enum NOT NULL,
                                             template template_enum NOT NULL,
                                             sent_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                             status notif_status_enum NOT NULL,
                                             error_msg TEXT
)@@

CREATE TABLE IF NOT EXISTS audit_logs (
                                          audit_id BIGSERIAL PRIMARY KEY,
                                          actor_email TEXT,
                                          action TEXT NOT NULL,
                                          entity_type TEXT NOT NULL,
                                          entity_id TEXT,
                                          details TEXT,
                                          created_at TIMESTAMPTZ NOT NULL DEFAULT now()
)@@

DO $$
DECLARE
    actor_type text;
BEGIN
    SELECT format_type(a.atttypid, a.atttypmod)
    INTO actor_type
    FROM pg_attribute a
    JOIN pg_class c ON c.oid = a.attrelid
    WHERE c.relname = 'audit_logs'
      AND a.attname = 'actor_email'
      AND a.attnum > 0
      AND NOT a.attisdropped;

    IF actor_type = 'bytea' THEN
        EXECUTE 'ALTER TABLE audit_logs ALTER COLUMN actor_email TYPE text USING convert_from(actor_email, ''UTF8'')';
    ELSE
        EXECUTE 'ALTER TABLE audit_logs ALTER COLUMN actor_email TYPE text USING actor_email::text';
    END IF;
EXCEPTION
    WHEN others THEN
        NULL;
END $$@@


-- ======================= INDEXES ========================
CREATE INDEX IF NOT EXISTS idx_products_expires_at ON products (expires_at)@@
CREATE INDEX IF NOT EXISTS idx_products_status ON products (status)@@
CREATE INDEX IF NOT EXISTS idx_products_owner_status ON products (owner_id, status)@@
CREATE INDEX IF NOT EXISTS idx_products_zone_status ON products (zone_id, status)@@
CREATE INDEX IF NOT EXISTS idx_notifications_user_sentat ON notifications (user_id, sent_at DESC)@@
CREATE INDEX IF NOT EXISTS idx_product_history_product_created_at ON product_history (product_id, created_at DESC)@@
CREATE INDEX IF NOT EXISTS idx_zones_fridge ON zones(fridge_id)@@
CREATE INDEX IF NOT EXISTS idx_fridge_memberships_user ON fridge_memberships(user_id)@@
CREATE INDEX IF NOT EXISTS idx_audit_logs_created_at ON audit_logs(created_at)@@

-- Add FK constraints for user_roles after all tables are created (idempotent check)
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_user_roles_user') THEN
        ALTER TABLE user_roles
            ADD CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_user_roles_role') THEN
        ALTER TABLE user_roles
            ADD CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE;
    END IF;
END $$@@

-- ======================= TRIGGERS ========================
CREATE OR REPLACE FUNCTION products_before_insert_update()
    RETURNS TRIGGER AS $$
DECLARE
    cat_days INT;
    zone_fridge_id BIGINT;
    membership_exists BOOLEAN;
BEGIN
    IF NEW.placed_at IS NULL THEN
        NEW.placed_at := now();
    END IF;

    IF NEW.expires_at IS NULL AND NEW.category_id IS NOT NULL THEN
        SELECT perishable_days_default INTO cat_days
        FROM categories
        WHERE category_id = NEW.category_id;

        IF cat_days IS NOT NULL THEN
            NEW.expires_at := NEW.placed_at + (cat_days || ' days')::interval;
        END IF;
    END IF;

    IF NEW.status IS NULL THEN
        NEW.status := 'ACTIVE';
    END IF;

    SELECT fridge_id INTO zone_fridge_id FROM zones WHERE zone_id = NEW.zone_id;
    IF zone_fridge_id IS NULL THEN
        RAISE EXCEPTION 'Zone % does not exist', NEW.zone_id;
    END IF;

    SELECT EXISTS (
        SELECT 1 FROM fridge_memberships
        WHERE fridge_id = zone_fridge_id
          AND user_id = NEW.owner_id
          AND left_at IS NULL
    ) INTO membership_exists;

    IF NOT membership_exists THEN
        RAISE EXCEPTION 'Owner (user_id=%) is not an active member of the fridge owning zone %',
            NEW.owner_id, NEW.zone_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql@@

DROP TRIGGER IF EXISTS trg_products_before_ins_upd ON products@@
CREATE TRIGGER trg_products_before_ins_upd
    BEFORE INSERT OR UPDATE ON products
    FOR EACH ROW EXECUTE FUNCTION products_before_insert_update()@@


CREATE OR REPLACE FUNCTION product_history_after_insert()
    RETURNS TRIGGER AS $$
BEGIN
    IF NEW.event_type = 'MOVE' THEN
        IF NEW.to_zone_id IS NOT NULL THEN
            UPDATE products SET zone_id = NEW.to_zone_id WHERE product_id = NEW.product_id;
        END IF;

    ELSIF NEW.event_type = 'EXTEND' THEN
        IF NEW.new_expires_at IS NOT NULL THEN
            UPDATE products
            SET expires_at = NEW.new_expires_at,
                status = CASE
                             WHEN status = 'EXPIRED' AND NEW.new_expires_at > now() THEN 'ACTIVE'
                             WHEN NEW.new_expires_at <= now() AND status = 'ACTIVE' THEN 'EXPIRED'
                             ELSE status
                    END
            WHERE product_id = NEW.product_id;
        END IF;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql@@

DROP TRIGGER IF EXISTS trg_product_history_after_ins ON product_history@@
CREATE TRIGGER trg_product_history_after_ins
    AFTER INSERT ON product_history
    FOR EACH ROW EXECUTE FUNCTION product_history_after_insert()@@


-- ======================= FUNCTIONS ========================
CREATE OR REPLACE FUNCTION fn_add_fridge_member(
    p_fridge_id BIGINT,
    p_user_id BIGINT,
    p_is_moderator BOOLEAN DEFAULT FALSE
)
    RETURNS VOID
    LANGUAGE plpgsql AS $$
BEGIN
    PERFORM 1 FROM fridges WHERE fridge_id = p_fridge_id;
    IF NOT FOUND THEN RAISE EXCEPTION 'Fridge % not found', p_fridge_id; END IF;

    PERFORM 1 FROM users WHERE user_id = p_user_id;
    IF NOT FOUND THEN RAISE EXCEPTION 'User % not found', p_user_id; END IF;

    INSERT INTO fridge_memberships(fridge_id, user_id, is_moderator, joined_at, left_at)
    VALUES (p_fridge_id, p_user_id, p_is_moderator, now(), NULL)
    ON CONFLICT (fridge_id, user_id) DO UPDATE
        SET is_moderator = EXCLUDED.is_moderator,
            joined_at = COALESCE(fridge_memberships.joined_at, EXCLUDED.joined_at),
            left_at = NULL;
END;
$$@@

CREATE OR REPLACE FUNCTION fn_remove_fridge_member(p_fridge_id BIGINT, p_user_id BIGINT)
    RETURNS VOID
    LANGUAGE plpgsql AS $$
BEGIN
    UPDATE fridge_memberships
    SET left_at = now()
    WHERE fridge_id = p_fridge_id AND user_id = p_user_id AND left_at IS NULL;
END;
$$@@

-- ВАЖНО: никаких DEFAULT "в середине"
CREATE OR REPLACE FUNCTION fn_add_product(
    p_owner_id BIGINT,
    p_zone_id  BIGINT,
    p_category_id BIGINT,
    p_name TEXT,
    p_barcode TEXT DEFAULT NULL,
    p_expires_at TIMESTAMPTZ DEFAULT NULL,
    p_placed_at TIMESTAMPTZ DEFAULT NULL,
    p_locked BOOLEAN DEFAULT FALSE,
    p_status status_enum DEFAULT 'ACTIVE'
)
    RETURNS BIGINT
    LANGUAGE plpgsql AS $$
DECLARE v_product_id BIGINT;
BEGIN
    INSERT INTO products(owner_id, zone_id, category_id, name, barcode, expires_at, placed_at, locked, status)
    VALUES (p_owner_id, p_zone_id, p_category_id, p_name, p_barcode, p_expires_at, p_placed_at, p_locked, p_status)
    RETURNING product_id INTO v_product_id;

    RETURN v_product_id;
END;
$$@@

CREATE OR REPLACE FUNCTION fn_get_products_expiring_within(p_fridge_id BIGINT, p_days INT)
    RETURNS TABLE (
                      product_id BIGINT,
                      name TEXT,
                      expires_at TIMESTAMPTZ,
                      owner_id BIGINT,
                      zone_id BIGINT,
                      fridge_id BIGINT
                  )
    LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
        SELECT p.product_id, p.name, p.expires_at, p.owner_id, p.zone_id, z.fridge_id
        FROM products p
                 JOIN zones z ON z.zone_id = p.zone_id
        WHERE z.fridge_id = p_fridge_id
          AND p.expires_at IS NOT NULL
          AND p.expires_at <= now() + (p_days || ' days')::interval
          AND p.status = 'ACTIVE';
END;
$$@@

CREATE OR REPLACE FUNCTION fn_get_user_products(p_user_id BIGINT)
    RETURNS TABLE (
                      product_id BIGINT,
                      name TEXT,
                      expires_at TIMESTAMPTZ,
                      status status_enum,
                      zone_id BIGINT,
                      fridge_id BIGINT
                  )
    LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
        SELECT p.product_id, p.name, p.expires_at, p.status, p.zone_id, z.fridge_id
        FROM products p
                 JOIN zones z ON z.zone_id = p.zone_id
        WHERE p.owner_id = p_user_id
        ORDER BY p.expires_at NULLS LAST, p.name;
END;
$$@@

CREATE OR REPLACE FUNCTION fn_create_notification(
    p_user_id BIGINT,
    p_product_id BIGINT,
    p_channel channel_enum,
    p_template template_enum,
    p_status notif_status_enum DEFAULT 'SENT',
    p_error_msg TEXT DEFAULT NULL
)
    RETURNS BIGINT
    LANGUAGE plpgsql AS $$
DECLARE v_id BIGINT;
BEGIN
    INSERT INTO notifications(user_id, product_id, channel, template, sent_at, status, error_msg)
    VALUES (p_user_id, p_product_id, p_channel, p_template, now(), p_status, p_error_msg)
    RETURNING notification_id INTO v_id;

    RETURN v_id;
END;
$$@@

CREATE OR REPLACE FUNCTION fn_move_product(p_product_id BIGINT, p_to_zone_id BIGINT, p_actor_id BIGINT, p_comment TEXT DEFAULT NULL)
    RETURNS VOID
    LANGUAGE plpgsql AS $$
DECLARE v_from_zone BIGINT;
BEGIN
    SELECT zone_id INTO v_from_zone FROM products WHERE product_id = p_product_id;
    IF v_from_zone IS NULL THEN RAISE EXCEPTION 'Product % not found', p_product_id; END IF;

    INSERT INTO product_history(product_id, event_type, from_zone_id, to_zone_id, actor_id, comment)
    VALUES (p_product_id, 'MOVE', v_from_zone, p_to_zone_id, p_actor_id, p_comment);
END;
$$@@

CREATE OR REPLACE FUNCTION fn_extend_product(p_product_id BIGINT, p_new_expires_at TIMESTAMPTZ, p_actor_id BIGINT, p_comment TEXT DEFAULT NULL)
    RETURNS VOID
    LANGUAGE plpgsql AS $$
DECLARE v_old TIMESTAMPTZ;
BEGIN
    SELECT expires_at INTO v_old FROM products WHERE product_id = p_product_id;
    IF NOT FOUND THEN RAISE EXCEPTION 'Product % not found', p_product_id; END IF;

    INSERT INTO product_history(product_id, event_type, old_expires_at, new_expires_at, actor_id, comment)
    VALUES (p_product_id, 'EXTEND', v_old, p_new_expires_at, p_actor_id, p_comment);
END;
$$@@

CREATE OR REPLACE FUNCTION fn_set_product_status(p_product_id BIGINT, p_status status_enum, p_actor_id BIGINT, p_comment TEXT DEFAULT NULL)
    RETURNS VOID
    LANGUAGE plpgsql AS $$
BEGIN
    UPDATE products SET status = p_status WHERE product_id = p_product_id;
    IF NOT FOUND THEN RAISE EXCEPTION 'Product % not found', p_product_id; END IF;

    INSERT INTO product_history(product_id, event_type, actor_id, comment)
    VALUES (p_product_id, 'STATUS', p_actor_id, p_comment);
END;
$$@@

CREATE OR REPLACE FUNCTION fn_get_fridge_map(p_fridge_id BIGINT)
    RETURNS TABLE(
                     zone_id BIGINT,
                     zone_name TEXT,
                     capacity_units INT,
                     sort_order INT,
                     active_count BIGINT,
                     expired_count BIGINT
                 )
    LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
        SELECT z.zone_id,
               z.name,
               z.capacity_units,
               z.sort_order,
               (SELECT COUNT(*) FROM products p WHERE p.zone_id = z.zone_id AND p.status = 'ACTIVE') AS active_count,
               (SELECT COUNT(*) FROM products p WHERE p.zone_id = z.zone_id AND p.status = 'EXPIRED') AS expired_count
        FROM zones z
        WHERE z.fridge_id = p_fridge_id
          AND z.is_active = TRUE
        ORDER BY z.sort_order, z.zone_id;
END;
$$@@

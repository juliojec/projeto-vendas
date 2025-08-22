CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

SET timezone = 'America/Sao_Paulo';

DO $$
BEGIN
    RAISE NOTICE 'PostgreSQL database initialized successfully for Vendas API';
    RAISE NOTICE 'Database: vendas';
    RAISE NOTICE 'User: vendas_user';
    RAISE NOTICE 'Timezone: %', current_setting('timezone');
END $$;

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

COMMENT ON DATABASE vendas IS 'Database para Sistema de Vendas API - 123 Vendas';
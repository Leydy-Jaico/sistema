IF DB_ID('gestion_clientes') IS NULL
BEGIN
    CREATE DATABASE gestion_clientes;
END;
GO

USE gestion_clientes;
GO

IF OBJECT_ID('dbo.clientes', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.clientes (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        dni VARCHAR(8) NOT NULL UNIQUE,
        nombre VARCHAR(100) NOT NULL,
        apellido VARCHAR(100) NOT NULL,
        email VARCHAR(150) NOT NULL,
        telefono VARCHAR(15) NULL
    );
END;
GO

USE master;
GO

IF NOT EXISTS (SELECT 1 FROM sys.sql_logins WHERE name = 'usuario_clientes')
BEGIN
    CREATE LOGIN usuario_clientes
    WITH PASSWORD = 'Cliente123*',
         CHECK_POLICY = ON,
         CHECK_EXPIRATION = OFF;
END;
GO

USE gestion_clientes;
GO

IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = 'usuario_clientes')
BEGIN
    CREATE USER usuario_clientes FOR LOGIN usuario_clientes;
END;
GO

ALTER ROLE db_owner ADD MEMBER usuario_clientes;
GO

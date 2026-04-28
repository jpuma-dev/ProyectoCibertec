CREATE DATABASE bd_mercado

ALTER LOGIN sa ENABLE;
GO
ALTER LOGIN sa WITH PASSWORD = '12345678';
GO

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'bd_mercado')
    CREATE DATABASE bd_mercado;
GO
-- Insertar activos base para pruebas de inversiones
INSERT INTO activos (simbolo, mercado, precio_mercado, sector) VALUES 
('AAPL', 'NASDAQ', 185.5000, 'Tecnología'),
('MSFT', 'NASDAQ', 420.2500, 'Tecnología'),
('TSLA', 'NASDAQ', 175.0000, 'Automotriz'),
('SPY', 'NYSE', 510.3000, 'ETF'),
('BTC', 'CRYPTO', 65000.0000, 'Criptomonedas'),
('ETH', 'CRYPTO', 3500.0000, 'Criptomonedas');

-- Pruebas de Inversiones
-- POST /api/finanzas/portafolios
-- {
--     "nombre": "Mi Portafolio de Pruebas",
--     "balanceTotal": 100000.00,
--     "riesgoPerfil": "MEDIO"
-- }
-- POST /api/finanzas/inversiones
-- {
--     "cantidad": 2,
--     "precio": 185.5000,
--     "tipoOperacion": "COMPRA",
--     "estado": "ABIERTA",
--     "portafolioId": 1,
--     "activoId": 1
-- }


-- Dodanie kolumny dyskryminatora dla strategii SINGLE_TABLE
ALTER TABLE zegarki
    ADD COLUMN typ_zegarka VARCHAR(31) NOT NULL DEFAULT 'NIEZNANY'; -- Wartosc domyslna dla istniejacych rekordow, jesli sa

-- Dodanie kolumn specyficznych dla ZegarekAnalogowy
ALTER TABLE zegarki
    ADD COLUMN typ_tarczy VARCHAR(255);

-- Dodanie kolumn specyficznych dla ZegarekCyfrowy
ALTER TABLE zegarki
    ADD COLUMN czy_podswietlenie BOOLEAN;
ALTER TABLE zegarki
    ADD COLUMN dodatkowe_funkcje VARCHAR(100);

-- Dodanie kolumn specyficznych dla Smartwatch
ALTER TABLE zegarki
    ADD COLUMN system_operacyjny VARCHAR(50);
ALTER TABLE zegarki
    ADD COLUMN czy_nfc BOOLEAN;

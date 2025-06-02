CREATE TABLE IF NOT EXISTS zegarki (
                         id_zegarka BIGSERIAL PRIMARY KEY,
                         marka VARCHAR(100) NOT NULL,
                         model VARCHAR(100) NOT NULL,
                         cena DECIMAL(10, 2) NOT NULL,
                         opis TEXT,
                         ilosc_na_stanie INTEGER NOT NULL,
                         typ_mechanizmu VARCHAR(50),
                         material_paska VARCHAR(50),
                         wodoodpornosc VARCHAR(50)
);
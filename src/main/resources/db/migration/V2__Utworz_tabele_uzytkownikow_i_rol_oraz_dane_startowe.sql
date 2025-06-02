CREATE TABLE IF NOT EXISTS role (
                      id_roli SERIAL PRIMARY KEY,
                      nazwa VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS uzytkownicy (
                             id_uzytkownika BIGSERIAL PRIMARY KEY,
                             login VARCHAR(100) NOT NULL UNIQUE,
                             haslo VARCHAR(255) NOT NULL,
                             email VARCHAR(100) NOT NULL UNIQUE,
                             czy_aktywny BOOLEAN NOT NULL DEFAULT TRUE
);

--Tworzenie tabeli laczacej 'uzytkownik_role'
CREATE TABLE IF NOT EXISTS uzytkownik_role (
                                 id_uzytkownika BIGINT NOT NULL,
                                 id_roli INTEGER NOT NULL,
                                 PRIMARY KEY (id_uzytkownika, id_roli),
                                 FOREIGN KEY (id_uzytkownika) REFERENCES uzytkownicy(id_uzytkownika) ON DELETE CASCADE,
                                 FOREIGN KEY (id_roli) REFERENCES role(id_roli) ON DELETE CASCADE
);
--podstawowe reole
INSERT INTO role (nazwa) VALUES ('ROLE_USER');
INSERT INTO role (nazwa) VALUES ('ROLE_ADMINISTRATOR');

--testowe inserty sample userow
INSERT INTO uzytkownicy (login, haslo, email, czy_aktywny) VALUES
    ('user', '$2a$12$HvIdZ9W64Orn6npODV.vPevMVCPUH6KhbEI9A4LJfrAcmKSeF4tL6', 'asd@asd.com', true); -- haslo: userpass
INSERT INTO uzytkownicy (login, haslo, email, czy_aktywny) VALUES
    ('admin', '$2a$12$geHs5lnSczcjpQk3awE1y.1Ih45OOzRkh1nmkciDVLzpYf1sfW5Ku', 'asdasd@asd.com', true); -- haslo: adminpass

-- Przypisanie rol do userow
INSERT INTO uzytkownik_role (id_uzytkownika, id_roli) VALUES
    ((SELECT id_uzytkownika FROM uzytkownicy WHERE login = 'user'), (SELECT id_roli FROM role WHERE nazwa = 'ROLE_USER'));

--analogicznie dla admina
INSERT INTO uzytkownik_role (id_uzytkownika, id_roli) VALUES
    ((SELECT id_uzytkownika FROM uzytkownicy WHERE login = 'admin'), (SELECT id_roli FROM role WHERE nazwa = 'ROLE_ADMINISTRATOR'));
INSERT INTO uzytkownik_role (id_uzytkownika, id_roli) VALUES
    ((SELECT id_uzytkownika FROM uzytkownicy WHERE login = 'admin'), (SELECT id_roli FROM role WHERE nazwa = 'ROLE_USER'));
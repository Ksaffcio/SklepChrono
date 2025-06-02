package com.example.javaprojekt.service;

import com.example.javaprojekt.dto.StworzEdytujZegarekRequestDto;
import com.example.javaprojekt.dto.ZegarekDto;
import com.example.javaprojekt.entity.Zegarek;
import com.example.javaprojekt.entity.ZegarekAnalogowy;
import com.example.javaprojekt.repository.ZegarekRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest // Ładuje pełny kontekst Spring Boot
// @Transactional // Jeśli odkomentujesz, każda metoda testowa będzie w transakcji, która zostanie wycofana.
        // To zapewnia czystą bazę dla każdego testu bez ręcznego deleteAll().
class ZegarekServiceIntegrationTest {

    @Autowired
    private ZegarekService zegarekService;

    @Autowired
    private ZegarekRepository zegarekRepository; // Prawdziwe repozytorium

    private StworzEdytujZegarekRequestDto requestDtoAnalog;
    private StworzEdytujZegarekRequestDto requestDtoCyfrowy;
    private StworzEdytujZegarekRequestDto requestDtoSmartwatch;

    @BeforeEach
    void setUp() {
        // Jeśli nie używasz @Transactional na klasie, czyść bazę ręcznie
        // zegarekRepository.deleteAll(); // Odkomentuj, jeśli nie używasz @Transactional na klasie

        requestDtoAnalog = new StworzEdytujZegarekRequestDto(
                "TestRolex", "Submariner", new BigDecimal("1000.00"), "Analog opis", 5,
                "Automatyczny", "Stal", "300m", "ANALOGOWY",
                "Indeksy", null, null, null, null);

        requestDtoCyfrowy = new StworzEdytujZegarekRequestDto(
                "TestCasio", "GShock", new BigDecimal("200.00"), "Cyfrowy opis", 10,
                "Kwarcowy", "Plastik", "200m", "CYFROWY",
                null, true, "Stoper, Alarm", null, null);

        requestDtoSmartwatch = new StworzEdytujZegarekRequestDto(
                "TestApple", "Watch", new BigDecimal("1500.00"), "Smart opis", 3,
                "Elektroniczny", "Aluminium", "50m", "SMARTWATCH",
                null, null, null, "watchOS", true);
    }

    @AfterEach // Użyj jeśli nie masz @Transactional na klasie
    void tearDown() {
        zegarekRepository.deleteAll();
    }


    @Test
    void testStworzZegarek_Analogowy_iZnajdz() {
        ZegarekDto stworzony = zegarekService.stworzZegarek(requestDtoAnalog);
        assertNotNull(stworzony.getIdZegarka());

        Optional<ZegarekDto> znaleziony = zegarekService.znajdzZegarekPoId(stworzony.getIdZegarka());
        assertTrue(znaleziony.isPresent());
        assertEquals("TestRolex", znaleziony.get().getMarka());
        assertTrue(znaleziony.get().getTypSpecyficznyZegarka().contains("Analogowy"));
        assertEquals("Indeksy", znaleziony.get().getTypTarczy());

        // Weryfikacja bezpośrednio w repozytorium
        Optional<Zegarek> zBazy = zegarekRepository.findById(stworzony.getIdZegarka());
        assertTrue(zBazy.isPresent());
        assertTrue(zBazy.get() instanceof ZegarekAnalogowy);
    }

    @Test
    void testStworzZegarek_Cyfrowy() {
        ZegarekDto stworzony = zegarekService.stworzZegarek(requestDtoCyfrowy);
        assertNotNull(stworzony.getIdZegarka());
        assertTrue(stworzony.getTypSpecyficznyZegarka().contains("Cyfrowy"));
        assertTrue(stworzony.getCzyPodswietlenie());
        assertEquals("Stoper, Alarm", stworzony.getDodatkoweFunkcje());
    }

    @Test
    void testStworzZegarek_Smartwatch() {
        ZegarekDto stworzony = zegarekService.stworzZegarek(requestDtoSmartwatch);
        assertNotNull(stworzony.getIdZegarka());
        assertTrue(stworzony.getTypSpecyficznyZegarka().contains("Smartwatch"));
        assertEquals("watchOS", stworzony.getSystemOperacyjny());
        assertTrue(stworzony.getCzyNFC());
    }

    @Test
    void testStworzZegarek_nieznanyTyp_powinienRzucicWyjatek() {
        StworzEdytujZegarekRequestDto dtoZlyTyp = new StworzEdytujZegarekRequestDto(
                "ZlyMarka", "ZlyModel", BigDecimal.TEN, "ZlyOpis", 1,
                null, null, null, "NIEZNANY_TYP",
                null, null, null, null, null);

        assertThrows(IllegalArgumentException.class, () -> zegarekService.stworzZegarek(dtoZlyTyp));
        assertEquals(0, zegarekRepository.count()); // Nic nie powinno zostać dodane
    }

    @Test
    void testZnajdzWszystkieZegarki() {
        zegarekService.stworzZegarek(requestDtoAnalog);
        zegarekService.stworzZegarek(requestDtoCyfrowy);

        List<ZegarekDto> zegarki = zegarekService.znajdzWszystkieZegarki();
        assertEquals(2, zegarki.size());
    }

    @Test
    void testZnajdzWszystkieZegarki_gdyBrak_powinnaBycPustaLista() {
        List<ZegarekDto> zegarki = zegarekService.znajdzWszystkieZegarki();
        assertTrue(zegarki.isEmpty());
    }

    @Test
    void testZnajdzZegarekPoId_gdyNieIstnieje() {
        Optional<ZegarekDto> zegarek = zegarekService.znajdzZegarekPoId(999L);
        assertFalse(zegarek.isPresent());
    }

    @Test
    void testAktualizujZegarek_gdyIstnieje() {
        ZegarekDto stworzony = zegarekService.stworzZegarek(requestDtoAnalog);
        StworzEdytujZegarekRequestDto updateDto = new StworzEdytujZegarekRequestDto(
                "RolexUpdated", "SubmarinerUpdated", new BigDecimal("1200.00"), "Analog opis updated", 3,
                "AutomatycznyUpdated", "Zloto", "500m", "ANALOGOWY",
                "Brylanty", null, null, null, null);

        Optional<ZegarekDto> zaktualizowany = zegarekService.aktualizujZegarek(stworzony.getIdZegarka(), updateDto);
        assertTrue(zaktualizowany.isPresent());
        assertEquals("RolexUpdated", zaktualizowany.get().getMarka());
        assertEquals("Brylanty", zaktualizowany.get().getTypTarczy());
        assertEquals(3, zaktualizowany.get().getIloscNaStanie());
    }

    @Test
    void testAktualizujZegarek_probujeZmienicTyp_aktualizujePolaWspolne() {
        ZegarekDto stworzonyAnalog = zegarekService.stworzZegarek(requestDtoAnalog);
        Long id = stworzonyAnalog.getIdZegarka();

        StworzEdytujZegarekRequestDto updateDtoProbaZmianyTypu = new StworzEdytujZegarekRequestDto(
                "MarkaPoZmianie", "ModelPoZmianie", BigDecimal.valueOf(99.99), "OpisPoZmianie", 1,
                "MechanizmPoZmianie", "PasekPoZmianie", "WodoPoZmianie",
                "CYFROWY", // Proba zmiany typu na CYFROWY
                null, true, "NoweFunkcje", null, null
        );

        Optional<ZegarekDto> zaktualizowany = zegarekService.aktualizujZegarek(id, updateDtoProbaZmianyTypu);
        assertTrue(zaktualizowany.isPresent());
        assertEquals("MarkaPoZmianie", zaktualizowany.get().getMarka()); // Pola wspolne zaktualizowane
        assertTrue(zaktualizowany.get().getTypSpecyficznyZegarka().contains("Analogowy")); // Typ nie powinien sie zmienic
        // Pola specyficzne dla CYFROWY nie powinny byc ustawione na obiekcie Analogowy
        assertNull(zaktualizowany.get().getCzyPodswietlenie());
        assertEquals(requestDtoAnalog.getTypTarczy(), zaktualizowany.get().getTypTarczy()); // typTarczy powinien pozostać stary
    }


    @Test
    void testAktualizujZegarek_gdyNieIstnieje() {
        StworzEdytujZegarekRequestDto updateDto = new StworzEdytujZegarekRequestDto(
                "A", "B", BigDecimal.ONE, "C", 1, "D", "E", "F", "ANALOGOWY", "G", null, null, null, null);
        Optional<ZegarekDto> wynik = zegarekService.aktualizujZegarek(999L, updateDto);
        assertFalse(wynik.isPresent());
    }

    @Test
    void testUsunZegarek_gdyIstnieje() {
        ZegarekDto stworzony = zegarekService.stworzZegarek(requestDtoAnalog);
        boolean usunieto = zegarekService.usunZegarek(stworzony.getIdZegarka());
        assertTrue(usunieto);
        assertFalse(zegarekRepository.existsById(stworzony.getIdZegarka()));
    }

    @Test
    void testUsunZegarek_gdyNieIstnieje() {
        boolean usunieto = zegarekService.usunZegarek(999L);
        assertFalse(usunieto);
    }
}
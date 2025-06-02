package com.example.javaprojekt.repository;

import com.example.javaprojekt.entity.Zegarek;
import com.example.javaprojekt.entity.ZegarekAnalogowy;
import com.example.javaprojekt.entity.ZegarekCyfrowy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // Konfiguruje H2, wykonuje rollback po każdym teście
public class ZegarekRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ZegarekRepository zegarekRepository;

    @Test
    void testZapiszIZnajdzZegarekAnalogowy() {
        ZegarekAnalogowy zegarek = ZegarekAnalogowy.builder()
                .marka("TestMarkaRepo").model("TestModelRepo").cena(new BigDecimal("100.00"))
                .iloscNaStanie(1).typTarczy("CyfryRepo").typMechanizmu("Auto").materialPaska("Skora").wodoodpornosc("50m")
                .build();

        ZegarekAnalogowy zapisany = entityManager.persistAndFlush(zegarek);

        Optional<Zegarek> znaleziony = zegarekRepository.findById(zapisany.getIdZegarka());

        assertThat(znaleziony).isPresent();
        assertThat(znaleziony.get().getMarka()).isEqualTo("TestMarkaRepo");
        assertThat(znaleziony.get()).isInstanceOf(ZegarekAnalogowy.class);
        assertThat(((ZegarekAnalogowy) znaleziony.get()).getTypTarczy()).isEqualTo("CyfryRepo");
    }

    @Test
    void testHierarchiaZegarkow_zapiszIRetrieve() {
        ZegarekAnalogowy analog = ZegarekAnalogowy.builder().marka("Analog").model("A1").cena(BigDecimal.TEN).iloscNaStanie(1).typTarczy("Indeksy").build();
        ZegarekCyfrowy cyfrowy = ZegarekCyfrowy.builder().marka("Cyfrowy").model("C1").cena(BigDecimal.ONE).iloscNaStanie(2).czyPodswietlenie(true).build();

        entityManager.persist(analog);
        entityManager.persist(cyfrowy);
        entityManager.flush();
        entityManager.clear(); // Ważne, aby upewnić się, że odczytujemy z bazy, a nie z cache L1

        List<Zegarek> wszystkie = zegarekRepository.findAll();
        assertThat(wszystkie).hasSize(2);

        Zegarek odczytanyAnalog = zegarekRepository.findById(analog.getIdZegarka()).orElseThrow();
        Zegarek odczytanyCyfrowy = zegarekRepository.findById(cyfrowy.getIdZegarka()).orElseThrow();

        assertThat(odczytanyAnalog).isInstanceOf(ZegarekAnalogowy.class);
        assertThat(((ZegarekAnalogowy) odczytanyAnalog).getTypTarczy()).isEqualTo("Indeksy");

        assertThat(odczytanyCyfrowy).isInstanceOf(ZegarekCyfrowy.class);
        assertThat(((ZegarekCyfrowy) odczytanyCyfrowy).isCzyPodswietlenie()).isTrue();
    }
}
package com.example.javaprojekt.dto;

import com.fasterxml.jackson.annotation.JsonInclude; // Do pomijania pol null w JSON
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Pola null nie beda parsowane do jsona
public class ZegarekDto {
    private Long idZegarka;
    private String marka;
    private String model;
    private BigDecimal cena;
    private String opis;
    private Integer iloscNaStanie;
    private String typMechanizmu;
    private String materialPaska;
    private String wodoodpornosc;

    private String typSpecyficznyZegarka; // Przechowuje wynik z zegarek.uzyskajTypSpecyficzny()

    // Pola specyficzne, opcjonalne
    private String typTarczy;
    private Boolean czyPodswietlenie;
    private String dodatkoweFunkcje;
    private String systemOperacyjny;
    private Boolean czyNFC;

    // Konstruktor dla mapowania z ZegarekService
    public ZegarekDto(Long idZegarka, String marka, String model, BigDecimal cena, String opis, Integer iloscNaStanie, String typMechanizmu, String materialPaska, String wodoodpornosc, String typSpecyficznyZegarka) {
        this.idZegarka = idZegarka;
        this.marka = marka;
        this.model = model;
        this.cena = cena;
        this.opis = opis;
        this.iloscNaStanie = iloscNaStanie;
        this.typMechanizmu = typMechanizmu;
        this.materialPaska = materialPaska;
        this.wodoodpornosc = wodoodpornosc;
        this.typSpecyficznyZegarka = typSpecyficznyZegarka;
    }
}
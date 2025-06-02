package com.example.javaprojekt.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StworzEdytujZegarekRequestDto {

    @NotBlank(message = "Marka nie moze byc pusta")
    @Size(min = 2, max = 100, message = "Marka musi miec od {min} do {max} znakow")
    private String marka;

    @NotBlank(message = "Model nie moze byc pusty")
    @Size(min = 1, max = 100, message = "Model musi miec od {min} do {max} znakow")
    private String model;

    @NotNull(message = "Cena nie moze byc pusta")
    @DecimalMin(value = "0.01", message = "Cena musi byc wieksza niz 0")
    @Digits(integer = 8, fraction = 2, message = "Nieprawidlowy format ceny (max 8 cyfr przed przecinkiem, 2 po)")
    private BigDecimal cena;

    @Size(max = 2000, message = "Opis moze miec maksymalnie {max} znakow")
    private String opis;

    @NotNull(message = "Ilosc na stanie nie moze byc pusta")
    @Min(value = 0, message = "Ilosc na stanie nie moze byc ujemna")
    private Integer iloscNaStanie;

    @Size(max = 50, message = "Typ mechanizmu moze miec maksymalnie {max} znakow")
    private String typMechanizmu;

    @Size(max = 50, message = "Material paska moze miec maksymalnie {max} znakow")
    private String materialPaska;

    @Size(max = 50, message = "Wodoodpornosc moze miec maksymalnie {max} znakow")
    private String wodoodpornosc;

    // Nowe pole do okreslenia typu zegarka
    @NotBlank(message = "Typ zegarka (ANALOGOWY, CYFROWY, SMARTWATCH) jest wymagany")
    private String typZegarka; // Oczekiwane wartosci: "ANALOGOWY", "CYFROWY", "SMARTWATCH"

    // Pola specyficzne dla typow - opcjonalne, beda uzywane w zaleznosci od typZegarka
    private String typTarczy; // Dla ANALOGOWY
    private Boolean czyPodswietlenie; // Dla CYFROWY
    private String dodatkoweFunkcje; // Dla CYFROWY
    private String systemOperacyjny; // Dla SMARTWATCH
    private Boolean czyNFC; // Dla SMARTWATCH
}
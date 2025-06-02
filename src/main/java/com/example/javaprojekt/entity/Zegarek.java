package com.example.javaprojekt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder; // Dla dziedziczenia z builderem Lombok

import java.math.BigDecimal;

@Entity
@Table(name = "zegarki")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // Strategia dziedziczenia
@DiscriminatorColumn(name = "typ_zegarka", discriminatorType = DiscriminatorType.STRING) // Kolumna okreslajaca typ
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder // Pozwala na uzycie buildera w klasach dziedziczacych
public abstract class Zegarek { // KLASA STAJE SIE ABSTRAKCYJNA

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idZegarka;

    @Column(nullable = false, length = 100)
    private String marka;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cena;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String opis;

    @Column(nullable = false)
    private Integer iloscNaStanie;

    // Te pola moga byc wspolne, ale ich interpretacja moze byc rozna
    @Column(length = 50)
    private String typMechanizmu; // np. Kwarcowy, Automatyczny

    @Column(length = 50)
    private String materialPaska;

    @Column(length = 50)
    private String wodoodpornosc;

    // Abstrakcyjna metoda, ktora beda implementowac podklasy
    public abstract String uzyskajTypSpecyficzny();

}
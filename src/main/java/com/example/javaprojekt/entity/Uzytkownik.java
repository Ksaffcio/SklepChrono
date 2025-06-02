package com.example.javaprojekt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;



// Encja reprezentujaca uzytkownika systemu.
@Entity
@Table(name = "uzytkownicy")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Uzytkownik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUzytkownika;

    @Column(nullable = false, unique = true, length = 100)
    private String login;

    @Column(nullable = false, length = 255) // Dla hasla
    private String haslo;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private boolean czyAktywny = true; // Domyslnie uzytkownik jest aktywny

    // Jeden uzytkownik moze miec wiele rol, jedna rola moze byc przypisana do wielu uzytkownikow
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE}) // relacja wiele - wiele
    @JoinTable(
            name = "uzytkownik_role", // Nazwa tabeli laczacej
            joinColumns = @JoinColumn(name = "id_uzytkownika"), // Kolumna laczaca z tabela Uzytkownik
            inverseJoinColumns = @JoinColumn(name = "id_roli")  // Kolumna laczaca z tabela Rola
    )
    private Set<Rola> role = new HashSet<>();

    // FetchType.EAGER dla rol oznacza, ze role beda ladowane od razu z uzytkownikiem
    // Dla niewielkiej liczby rol na uzytkownika to jest OK, pozniej moge rozbudowac ale watpie ze bedzie potrzeba
}
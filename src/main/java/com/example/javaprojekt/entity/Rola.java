package com.example.javaprojekt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Prosta encja reprezentujaca role uzytkownika.
@Entity
@Table(name = "role")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rola {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRoli; // int wystarczy bo rol bedze malo (wsm to 2)

    @Column(nullable = false, unique = true, length = 50)
    private String nazwa; // ROLE_USER, ROLE_ADMINISTRATOR itd...

}

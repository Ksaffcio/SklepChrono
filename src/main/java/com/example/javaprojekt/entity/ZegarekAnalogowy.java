package com.example.javaprojekt.entity;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("ANALOGOWY") // Wartosc w kolumnie dyskryminatora
@Data
@EqualsAndHashCode(callSuper = true) // Wazne dla klas dziedziczacych z Lombokiem
@NoArgsConstructor
@SuperBuilder
public class ZegarekAnalogowy extends Zegarek {

    private String typTarczy; //cyfry arabskie, rzymskie, material lub co chcemy

    public ZegarekAnalogowy(String typTarczy) {
        this.typTarczy = typTarczy;
    }

    // Implementacja metody abstrakcyjnej
    @Override
    public String uzyskajTypSpecyficzny() {
        return "Analogowy" + (typTarczy != null ? " (Tarcza: " + typTarczy + ")" : "");
    }
}
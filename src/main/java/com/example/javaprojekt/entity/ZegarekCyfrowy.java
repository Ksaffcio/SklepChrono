package com.example.javaprojekt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("CYFROWY")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class ZegarekCyfrowy extends Zegarek {

    private boolean czyPodswietlenie;

    @Column(length = 100)
    private String dodatkoweFunkcje; // np. Stoper, Alarm, Kalendarz

    public ZegarekCyfrowy(boolean czyPodswietlenie, String dodatkoweFunkcje) {
        this.czyPodswietlenie = czyPodswietlenie;
        this.dodatkoweFunkcje = dodatkoweFunkcje;
    }

    @Override
    public String uzyskajTypSpecyficzny() {
        return "Cyfrowy" + (czyPodswietlenie ? " z podswietleniem" : "");
    }
}
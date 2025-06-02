package com.example.javaprojekt.entity;

import com.example.javaprojekt.entity.Zegarek;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("SMARTWATCH")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class Smartwatch extends Zegarek {
    @Column(length = 50)
    private String systemOperacyjny; // np. Wear OS, watchOS

    @Column(name = "czy_nfc")
    private boolean czyNFC;

    public Smartwatch(String systemOperacyjny, boolean czyNFC) {
        this.systemOperacyjny = systemOperacyjny;
        this.czyNFC = czyNFC;
    }

    @Override
    public String uzyskajTypSpecyficzny() {
        return "Smartwatch (" + systemOperacyjny + (czyNFC ? ", NFC" : "") + ")";
    }
}
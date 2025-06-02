
package com.example.javaprojekt.service;

import com.example.javaprojekt.dto.StworzEdytujZegarekRequestDto;
import com.example.javaprojekt.dto.ZegarekDto;
import com.example.javaprojekt.entity.*; // Import nowych klas zegarkow
import com.example.javaprojekt.repository.ZegarekRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ZegarekService {

    private final ZegarekRepository zegarekRepository;

    // dodano informacje o typie specyficznym zegarka
    private ZegarekDto mapToZegarekDto(Zegarek zegarek) {
        ZegarekDto dto = new ZegarekDto( // update  konstruktor ZegarekDto jesli trzeba
                zegarek.getIdZegarka(),
                zegarek.getMarka(),
                zegarek.getModel(),
                zegarek.getCena(),
                zegarek.getOpis(),
                zegarek.getIloscNaStanie(),
                zegarek.getTypMechanizmu(),
                zegarek.getMaterialPaska(),
                zegarek.getWodoodpornosc(),
                zegarek.uzyskajTypSpecyficzny() // Dodajemy informacje z metody polimorficznej
        );
        // Mozna dodac mapowanie specyficznych pol, jesli ZegarekDto je obsluguje
        if (zegarek instanceof ZegarekAnalogowy) {
            dto.setTypTarczy(((ZegarekAnalogowy) zegarek).getTypTarczy());
        } else if (zegarek instanceof ZegarekCyfrowy) {
            dto.setCzyPodswietlenie(((ZegarekCyfrowy) zegarek).isCzyPodswietlenie());
            dto.setDodatkoweFunkcje(((ZegarekCyfrowy) zegarek).getDodatkoweFunkcje());
        } else if (zegarek instanceof Smartwatch) {
            dto.setSystemOperacyjny(((Smartwatch) zegarek).getSystemOperacyjny());
            dto.setCzyNFC(((Smartwatch) zegarek).isCzyNFC());
        }
        return dto;
    }

    // Metoda do tworzenia nowego zegarka (Simple Factory)
    @Transactional
    public ZegarekDto stworzZegarek(StworzEdytujZegarekRequestDto requestDto) {
        Zegarek zegarek;

        switch (requestDto.getTypZegarka().toUpperCase()) {
            case "ANALOGOWY":
                zegarek = ZegarekAnalogowy.builder() // Uzywamy SuperBuilder
                        .typTarczy(requestDto.getTypTarczy())
                        .build();
                break;
            case "CYFROWY":
                zegarek = ZegarekCyfrowy.builder()
                        .czyPodswietlenie(requestDto.getCzyPodswietlenie() != null && requestDto.getCzyPodswietlenie())
                        .dodatkoweFunkcje(requestDto.getDodatkoweFunkcje())
                        .build();
                break;
            case "SMARTWATCH":
                zegarek = Smartwatch.builder()
                        .systemOperacyjny(requestDto.getSystemOperacyjny())
                        .czyNFC(requestDto.getCzyNFC() != null && requestDto.getCzyNFC())
                        .build();
                break;
            default:
                throw new IllegalArgumentException("Nieznany typ zegarka: " + requestDto.getTypZegarka());
        }

        // Ustawienie wspolnych pol
        zegarek.setMarka(requestDto.getMarka());
        zegarek.setModel(requestDto.getModel());
        zegarek.setCena(requestDto.getCena());
        zegarek.setOpis(requestDto.getOpis());
        zegarek.setIloscNaStanie(requestDto.getIloscNaStanie());
        zegarek.setTypMechanizmu(requestDto.getTypMechanizmu());
        zegarek.setMaterialPaska(requestDto.getMaterialPaska());
        zegarek.setWodoodpornosc(requestDto.getWodoodpornosc());

        Zegarek zapisanyZegarek = zegarekRepository.save(zegarek);
        return mapToZegarekDto(zapisanyZegarek);
    }

    @Transactional(readOnly = true)
    public List<ZegarekDto> znajdzWszystkieZegarki() {
        return zegarekRepository.findAll()
                .stream()
                .map(this::mapToZegarekDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ZegarekDto> znajdzZegarekPoId(Long idZegarka) {
        return zegarekRepository.findById(idZegarka)
                .map(this::mapToZegarekDto);
    }

    // Aktualizacja - na razie prosta, zaklada ze typ sie nie zmienia,
    // ale aktualizuje pola wspolne i specyficzne jesli to mozliwe.
    // Bardziej zaawansowana aktualizacja moglaby obslugiwac zmiane typu.
    @Transactional
    public Optional<ZegarekDto> aktualizujZegarek(Long idZegarka, StworzEdytujZegarekRequestDto requestDto) {
        Optional<Zegarek> zegarekOptional = zegarekRepository.findById(idZegarka);
        if (zegarekOptional.isPresent()) {
            Zegarek zegarek = zegarekOptional.get();

            // Aktualizacja wspolnych pol
            zegarek.setMarka(requestDto.getMarka());
            zegarek.setModel(requestDto.getModel());
            zegarek.setCena(requestDto.getCena());
            zegarek.setOpis(requestDto.getOpis());
            zegarek.setIloscNaStanie(requestDto.getIloscNaStanie());
            zegarek.setTypMechanizmu(requestDto.getTypMechanizmu());
            zegarek.setMaterialPaska(requestDto.getMaterialPaska());
            zegarek.setWodoodpornosc(requestDto.getWodoodpornosc());

            // Aktualizacja pol specyficznych dla typu (jesli typ sie zgadza)
            // W bardziej zaawansowanym scenariuszu moglibysmy obslugiwac zmiane typu zegarka.
            if (zegarek instanceof ZegarekAnalogowy && "ANALOGOWY".equalsIgnoreCase(requestDto.getTypZegarka())) {
                ((ZegarekAnalogowy) zegarek).setTypTarczy(requestDto.getTypTarczy());
            } else if (zegarek instanceof ZegarekCyfrowy && "CYFROWY".equalsIgnoreCase(requestDto.getTypZegarka())) {
                ZegarekCyfrowy zc = (ZegarekCyfrowy) zegarek;
                if (requestDto.getCzyPodswietlenie() != null) zc.setCzyPodswietlenie(requestDto.getCzyPodswietlenie());
                zc.setDodatkoweFunkcje(requestDto.getDodatkoweFunkcje());
            } else if (zegarek instanceof Smartwatch && "SMARTWATCH".equalsIgnoreCase(requestDto.getTypZegarka())) {
                Smartwatch sw = (Smartwatch) zegarek;
                sw.setSystemOperacyjny(requestDto.getSystemOperacyjny());
                if (requestDto.getCzyNFC() != null) sw.setCzyNFC(requestDto.getCzyNFC());
            } else if (!zegarek.getClass().getSimpleName().toUpperCase().startsWith(requestDto.getTypZegarka().toUpperCase())) {
                // Typ w DTO nie zgadza sie z typem istniejacego zegarka - na razie ignorujemy zmiane typu
                // Mozna rzucic wyjatek lub zaimplementowac logike zmiany typu
                System.out.println("Ostrzezenie: Proba zmiany typu zegarka nie jest w pelni obslugiwana w tej metodzie aktualizacji.");
            }


            Zegarek zaktualizowanyZegarek = zegarekRepository.save(zegarek);
            return Optional.of(mapToZegarekDto(zaktualizowanyZegarek));
        } else {
            return Optional.empty();
        }
    }

    @Transactional
    public boolean usunZegarek(Long idZegarka) {
        if (zegarekRepository.existsById(idZegarka)) {
            zegarekRepository.deleteById(idZegarka);
            return true;
        }
        return false;
    }
}
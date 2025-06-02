
package com.example.javaprojekt.controller;

import com.example.javaprojekt.dto.StworzEdytujZegarekRequestDto;
import com.example.javaprojekt.dto.ZegarekDto;
import com.example.javaprojekt.exception.ApiErrorResponse; // Potrzebne do schemy dla bledow
import com.example.javaprojekt.service.ZegarekService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/zegarki")
@RequiredArgsConstructor
@Tag(name = "Zegarki API", description = "API do zarzadzania zegarkami w sklepie")
public class ZegarekController {

    private final ZegarekService zegarekService;

    @Operation(summary = "Tworzy nowy zegarek",
            description = "Dodaje nowy zegarek do bazy danych. Wymaga roli ADMINISTRATOR.",
            security = @SecurityRequirement(name = "basicAuth")) // Odniesienie do schematu bezpieczenstwa (zdefiniujemy globalnie pozniej)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Zegarek pomyslnie utworzony",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ZegarekDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Niepoprawne dane wejsciowe (blad walidacji)",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)) }),
            @ApiResponse(responseCode = "401", description = "Brak autoryzacji (niezalogowany)", content = @Content),
            @ApiResponse(responseCode = "403", description = "Brak uprawnien (np. zalogowany jako USER)", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ZegarekDto> stworzZegarek(
            @Parameter(description = "Dane nowego zegarka", required = true, schema = @Schema(implementation = StworzEdytujZegarekRequestDto.class))
            @Valid @RequestBody StworzEdytujZegarekRequestDto requestDto) {
        ZegarekDto stworzonyZegarek = zegarekService.stworzZegarek(requestDto);
        return new ResponseEntity<>(stworzonyZegarek, HttpStatus.CREATED);
    }

    @Operation(summary = "Pobiera wszystkie zegarki", description = "Zwraca liste wszystkich dostepnych zegarkow. Publicznie dostepne.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista zegarkow pobrana pomyslnie",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ZegarekDto.class)) }) // Powinno byc List<ZegarekDto>, ale Swagger ogarnie
    })
    @GetMapping
    public ResponseEntity<List<ZegarekDto>> pobierzWszystkieZegarki() {
        List<ZegarekDto> zegarki = zegarekService.znajdzWszystkieZegarki();
        return ResponseEntity.ok(zegarki);
    }

    @Operation(summary = "Pobiera zegarek po jego ID", description = "Zwraca szczegoly konkretnego zegarka. Publicznie dostepne.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Znaleziono zegarek",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ZegarekDto.class)) }),
            @ApiResponse(responseCode = "404", description = "Zegarek o podanym ID nie zostal znaleziony", content = @Content)
    })
    @GetMapping("/{idZegarka}")
    public ResponseEntity<ZegarekDto> pobierzZegarekPoId(
            @Parameter(description = "ID zegarka do pobrania", required = true, example = "1")
            @PathVariable Long idZegarka) {
        Optional<ZegarekDto> zegarekDtoOptional = zegarekService.znajdzZegarekPoId(idZegarka);
        return zegarekDtoOptional
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Aktualizuje istniejacy zegarek",
            description = "Modyfikuje dane istniejacego zegarka na podstawie jego ID. Wymaga roli ADMINISTRATOR.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Zegarek pomyslnie zaktualizowany",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ZegarekDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Niepoprawne dane wejsciowe (blad walidacji)",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)) }),
            @ApiResponse(responseCode = "401", description = "Brak autoryzacji", content = @Content),
            @ApiResponse(responseCode = "403", description = "Brak uprawnien", content = @Content),
            @ApiResponse(responseCode = "404", description = "Zegarek o podanym ID nie zostal znaleziony", content = @Content)
    })
    @PutMapping("/{idZegarka}")
    public ResponseEntity<ZegarekDto> aktualizujZegarek(
            @Parameter(description = "ID zegarka do aktualizacji", required = true, example = "1")
            @PathVariable Long idZegarka,
            @Parameter(description = "Zaktualizowane dane zegarka", required = true, schema = @Schema(implementation = StworzEdytujZegarekRequestDto.class))
            @Valid @RequestBody StworzEdytujZegarekRequestDto requestDto) {
        Optional<ZegarekDto> zaktualizowanyZegarekOptional = zegarekService.aktualizujZegarek(idZegarka, requestDto);
        return zaktualizowanyZegarekOptional
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Usuwa zegarek",
            description = "Usuwa zegarek z bazy danych na podstawie jego ID. Wymaga roli ADMINISTRATOR.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Zegarek pomyslnie usuniety", content = @Content),
            @ApiResponse(responseCode = "401", description = "Brak autoryzacji", content = @Content),
            @ApiResponse(responseCode = "403", description = "Brak uprawnien", content = @Content),
            @ApiResponse(responseCode = "404", description = "Zegarek o podanym ID nie zostal znaleziony", content = @Content)
    })
    @DeleteMapping("/{idZegarka}")
    public ResponseEntity<Void> usunZegarek(
            @Parameter(description = "ID zegarka do usuniecia", required = true, example = "1")
            @PathVariable Long idZegarka) {
        boolean usunieto = zegarekService.usunZegarek(idZegarka);
        if (usunieto) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
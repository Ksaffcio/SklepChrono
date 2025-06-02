package com.example.javaprojekt.controller;

import com.example.javaprojekt.dto.StworzEdytujZegarekRequestDto;
import com.example.javaprojekt.dto.ZegarekDto;
import com.example.javaprojekt.service.ZegarekService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import com.example.javaprojekt.config.SecurityConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@WebMvcTest(controllers = ZegarekController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)) // Testujemy tylko warstwę ZegarekController
public class ZegarekControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // Mockujemy ZegarekService, aby izolować testy kontrolera
    private ZegarekService zegarekService;

    @Autowired
    private ObjectMapper objectMapper; // Do konwersji obiektów na/z JSON

    private ZegarekDto zegarekDto1;
    private StworzEdytujZegarekRequestDto stworzRequestDto;

    @BeforeEach
    void setUp() {
        // Przykładowe DTO dla zegarka
        zegarekDto1 = new ZegarekDto();
        zegarekDto1.setIdZegarka(1L);
        zegarekDto1.setMarka("Rolex");
        zegarekDto1.setModel("Submariner");
        zegarekDto1.setCena(new BigDecimal("50000.00"));
        zegarekDto1.setOpis("Klasyczny nurek");
        zegarekDto1.setIloscNaStanie(10);
        zegarekDto1.setTypSpecyficznyZegarka("Analogowy (Tarcza: Indeksy)");
        zegarekDto1.setTypTarczy("Indeksy");


        // Przykładowe DTO do tworzenia zegarka
        stworzRequestDto = new StworzEdytujZegarekRequestDto(
                "Omega", "Speedmaster", new BigDecimal("25000.00"), "Ksiezycowy zegarek", 5,
                "Mechaniczny", "Stal", "50m", "ANALOGOWY",
                "Chronograf", null, null, null, null);
    }

    @Test
    @WithMockUser // Domyślnie user z rolą USER, wystarczy dla publicznego GET
    void testPobierzWszystkieZegarki_powinienZwrocicListe() throws Exception {
        // Given
        List<ZegarekDto> listaZegarkow = Collections.singletonList(zegarekDto1);
        given(zegarekService.znajdzWszystkieZegarki()).willReturn(listaZegarkow);

        // When & Then
        mockMvc.perform(get("/api/zegarki")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].marka", is("Rolex")));
    }

    @Test
    @WithMockUser
    void testPobierzZegarekPoId_gdyIstnieje_powinienZwrocicZegarek() throws Exception {
        // Given
        given(zegarekService.znajdzZegarekPoId(1L)).willReturn(Optional.of(zegarekDto1));

        // When & Then
        mockMvc.perform(get("/api/zegarki/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.marka", is("Rolex")));
    }

    @Test
    @WithMockUser
    void testPobierzZegarekPoId_gdyNieIstnieje_powinienZwrocicNotFound() throws Exception {
        // Given
        given(zegarekService.znajdzZegarekPoId(99L)).willReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/zegarki/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRATOR"}) // Testujemy jako admin
    void testStworzZegarek_poprawneDane_powinienUtworzyc() throws Exception {
        // Given
        ZegarekDto odpowiedzDto = new ZegarekDto(); // Symulacja odpowiedzi z serwisu
        odpowiedzDto.setIdZegarka(2L);
        odpowiedzDto.setMarka(stworzRequestDto.getMarka());
        odpowiedzDto.setModel(stworzRequestDto.getModel());
        // ... ustaw inne pola jeśli potrzebne do asercji
        given(zegarekService.stworzZegarek(any(StworzEdytujZegarekRequestDto.class))).willReturn(odpowiedzDto);

        // When & Then
        mockMvc.perform(post("/api/zegarki")
                        .with(csrf()) // Dodajemy CSRF, mimo że jest wyłączone globalnie, to dobra praktyka w testach MockMvc
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stworzRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.marka", is("Omega")))
                .andExpect(jsonPath("$.idZegarka", is(2)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRATOR"})
    void testStworzZegarek_niepoprawneDane_powinienZwrocicBadRequest() throws Exception {
        // Given
        StworzEdytujZegarekRequestDto niepoprawneDto = new StworzEdytujZegarekRequestDto(); // Puste pola
        niepoprawneDto.setModel("Tylko Model"); // Inne pola będą niepoprawne zgodnie z @NotBlank itp.

        // When & Then
        // Nie mockujemy serwisu, bo błąd walidacji powinien wystąpić przed wywołaniem serwisu
        mockMvc.perform(post("/api/zegarki")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(niepoprawneDto)))
                .andExpect(status().isBadRequest()) // Oczekujemy błędu walidacji
                .andExpect(jsonPath("$.validationErrors.marka").exists())
                .andExpect(jsonPath("$.validationErrors.cena").exists())
                .andExpect(jsonPath("$.validationErrors.iloscNaStanie").exists())
                .andExpect(jsonPath("$.validationErrors.typZegarka").exists());
    }


    @Test
    @WithMockUser(username = "user", roles = {"USER"}) // Zwykły użytkownik nie może tworzyć
    void testStworzZegarek_brakUprawnien_powinienZwrocicForbidden() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/zegarki")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stworzRequestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRATOR"})
    void testAktualizujZegarek_gdyIstnieje_powinienZaktualizowac() throws Exception {
        // Given
        ZegarekDto zaktualizowanyDto = new ZegarekDto();
        zaktualizowanyDto.setIdZegarka(1L);
        zaktualizowanyDto.setMarka("RolexUpdated");
        zaktualizowanyDto.setModel(stworzRequestDto.getModel()); // Załóżmy, że model się nie zmienił

        given(zegarekService.aktualizujZegarek(eq(1L), any(StworzEdytujZegarekRequestDto.class)))
                .willReturn(Optional.of(zaktualizowanyDto));

        StworzEdytujZegarekRequestDto updateRequest = new StworzEdytujZegarekRequestDto(
                "RolexUpdated", stworzRequestDto.getModel(), stworzRequestDto.getCena(), stworzRequestDto.getOpis(),
                stworzRequestDto.getIloscNaStanie(), stworzRequestDto.getTypMechanizmu(), stworzRequestDto.getMaterialPaska(),
                stworzRequestDto.getWodoodpornosc(), stworzRequestDto.getTypZegarka(), stworzRequestDto.getTypTarczy(),
                stworzRequestDto.getCzyPodswietlenie(), stworzRequestDto.getDodatkoweFunkcje(),
                stworzRequestDto.getSystemOperacyjny(), stworzRequestDto.getCzyNFC());


        // When & Then
        mockMvc.perform(put("/api/zegarki/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.marka", is("RolexUpdated")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRATOR"})
    void testAktualizujZegarek_gdyNieIstnieje_powinienZwrocicNotFound() throws Exception {
        // Given
        given(zegarekService.aktualizujZegarek(eq(99L), any(StworzEdytujZegarekRequestDto.class)))
                .willReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/zegarki/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stworzRequestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRATOR"})
    void testUsunZegarek_gdyIstnieje_powinienUsunac() throws Exception {
        // Given
        when(zegarekService.usunZegarek(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/zegarki/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRATOR"})
    void testUsunZegarek_gdyNieIstnieje_powinienZwrocicNotFound() throws Exception {
        // Given
        when(zegarekService.usunZegarek(99L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/zegarki/99")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
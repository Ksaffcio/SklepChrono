# Wprowadzenie

## Cel projektu

Głównym celem projektu było stworzenie backendu aplikacji e-commerce
"Sklep z Zegarkami". Aplikacja umożliwia zarządzanie katalogiem
zegarków, obsługuje różne typy użytkowników z odpowiednimi uprawnieniami
i udostępnia API REST.

## Zakres projektu

Zaimplementowano następujące funkcjonalności:

-   Zarządzanie produktami (zegarkami) z uwzględnieniem ich różnych
    typów (polimorfizm).

-   Uwierzytelnianie i autoryzacja użytkowników (Spring Security).

-   Interfejs REST API.

-   Automatycznie generowana dokumentacja API (Swagger UI).

-   Migracje bazy danych PostgreSQL (Flyway).

-   Walidacja danych wejściowych i globalna obsługa błędów.

-   Testy jednostkowe i integracyjne (JUnit 5, Mockito, Spring Test).

## Użyte technologie

-   Java 21, Spring Boot 3.2.4 (Spring Web, Data JPA, Security)

-   PostgreSQL, Flyway

-   Springdoc OpenAPI (Swagger UI)

-   Maven, Git

-   JUnit 5, Mockito, JaCoCo

-   Lombok

# Architektura i Projekt Systemu

## Architektura warstwowa

Aplikacja wykorzystuje architekturę warstwową: Kontrolery (obsługa żądań
HTTP), Serwisy (logika biznesowa), Repozytoria (dostęp do danych JPA),
Encje (model domeny).

## Model danych (ERD)

Diagram relacji encji (ERD) dla bazy danych:

![Diagram ERD bazy danych.](https://cdn.discordapp.com/attachments/1378123618453557559/1379125585028055040/Screenshot_2025-06-01_163357.png?ex=683f1a4a&is=683dc8ca&hm=5411a6277745b7b350bf1237ceda33bd6da1b442f0ba802bcbd281b075999447&)

Opis tabel: `zegarki` (z dziedziczeniem `SINGLE_TABLE` i kolumną
`typ_zegarka`), `uzytkownicy`, `role`, `uzytkownik_role`,
`flyway_schema_history`.

## Wzorce projektowe i zasady SOLID

-   **Wzorce:** Repozytorium, Wstrzykiwanie Zależności, Fabryka Prosta
    (w `ZegarekService`), DTO.

-   **Polimorfizm:** Hierarchia klas `Zegarek`.

-   **SOLID:** Starano się przestrzegać zasad (np. SRP poprzez podział
    na warstwy).

# Implementacja Kluczowych Modułów

## Moduł Zarządzania Zegarkami

### Model Encji i Dziedziczenie

Klasa abstrakcyjna `Zegarek` z podklasami (`ZegarekAnalogowy`,
`ZegarekCyfrowy`, `Smartwatch`) i strategią `SINGLE_TABLE`.

``` {.java caption="Fragment encji Zegarek.java" language="Java"}

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
```

Opis działania metody `uzyskajTypSpecyficzny()` w podklasach.

### Serwis i Fabryka Zegarków

Klasa `ZegarekService` zawiera logikę biznesową, w tym metodę
`stworzZegarek` działającą jako fabryka.

``` {.java caption="Metoda fabryczna w ZegarekService" language="Java"}
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
```

Szczegółowe wyjaśnienie, jak pola z DTO są mapowane na odpowiednie pola
encji w zależności od typu zegarka. Opis mapowania na `ZegarekDto`.

### Kontroler REST API dla Zegarków

Klasa `ZegarekController` udostępnia endpointy:

-   `POST /api/zegarki`: Tworzenie nowego zegarka (wymaga roli
    ADMINISTRATOR).

-   `GET /api/zegarki`: Pobieranie listy wszystkich zegarków
    (publiczne/user).

-   `GET /api/zegarki/{id}`: Pobieranie szczegółów zegarka o danym ID
    (publiczne/user).

-   `PUT /api/zegarki/{id}`: Aktualizacja zegarka (wymaga roli
    ADMINISTRATOR).

-   `DELETE /api/zegarki/{id}`: Usuwanie zegarka (wymaga roli
    ADMINISTRATOR).

Wykorzystanie `@Valid` do walidacji DTO.

## Moduł Bezpieczeństwa (Spring Security)

### Konfiguracja

Plik `SecurityConfig.java` definiuje zasady bezpieczeństwa. Użyto
`BCryptPasswordEncoder` do hashowania haseł. Główne reguły autoryzacji:

``` {.java caption="Fragment konfiguracji autoryzacji w SecurityConfig.java" language="Java"}
@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http

                .authorizeHttpRequests(authz -> authz
                        // Endpointy Swaggera
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                                // "/h2-console/**" -- z jakiegos powodu z tym jest masa bledow
                        ).permitAll()
                        // Reguly dla /api/zegarki
                        .requestMatchers(HttpMethod.POST, "/api/zegarki").hasRole("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.PUT, "/api/zegarki/**").hasRole("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/zegarki/**").hasRole("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.GET, "/api/zegarki", "/api/zegarki/**").permitAll() // Przeniesione dla spójności
                        // Wszystkie inne musi byc authenticated
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults())
                .formLogin(withDefaults()); // domyslny formularz logowania



        return http.build();
    }
```

Wyjaśnienie, dlaczego wybrano takie reguły i jak działają.

### Uwierzytelnianie

Implementacja `CustomUserDetailsService` do ładowania danych użytkownika
z bazy na podstawie encji `Uzytkownik` i `Rola`.

## Migracje Bazy Danych (Flyway)

Schemat bazy danych jest zarządzany przez Flyway. Skrypty migracyjne
znajdują się w `src/main/resources/db/migration/`.

-   `V1__Utworz_tabele_zegarki.sql`: Tworzy tabelę `zegarki`.

-   `V2__Utworz_tabele_uzytkownikow_i_rol_oraz_dane_startowe.sql`:
    Tworzy tabele `uzytkownicy`, `role`, `uzytkownik_role` i dodaje dane
    startowe (role, użytkownicy admin/user).

-   `V3__Dodaj_dziedziczenie_dla_zegarkow.sql`: Modyfikuje tabelę
    `zegarki` dodając kolumnę dyskryminatora `typ_zegarka` i kolumny
    specyficzne dla podtypów.

## Dokumentacja i Walidacja API

### Swagger UI (Springdoc OpenAPI)

Dokumentacja API jest automatycznie generowana i dostępna pod adresem
`http://localhost:8080/swagger-ui.html`. Wykorzystano adnotacje takie
jak `@Operation`, `@ApiResponse` do wzbogacenia opisów.

![Interfejs Swagger
UI.](https://cdn.discordapp.com/attachments/1378123618453557559/1379126475969531976/image.png?ex=683f1b1e&is=683dc99e&hm=ca7fdc4e2f5f3c4f147460c2887bffa82300f6496e826cfe0ab16991e8fef7c4&){#fig:swagger width="90%"}

### Walidacja DTO i Obsługa Błędów

Dane wejściowe są walidowane za pomocą adnotacji Jakarta Bean Validation
w DTO (np. `@NotBlank`, `@Size`). Błędy walidacji są obsługiwane przez
`GlobalExceptionHandler`, który zwraca status `400 Bad Request` z listą
błędów.


# Testowanie Aplikacji

## Strategia i Narzędzia

Zastosowano testy jednostkowe dla logiki serwisów (z użyciem
`@SpringBootTest` i prawdziwego repozytorium, ale na bazie H2) oraz
testy integracyjne dla warstwy web (kontrolery z `@WebMvcTest` i
mockowanymi serwisami). Repozytoria testowano z `@DataJpaTest`. Użyto
JUnit 5 i AssertJ.

## Pokrycie Kodu (JaCoCo)

Do pomiaru pokrycia kodu testami wykorzystano JaCoCo. Celem było
osiągnięcie min. 80% pokrycia instrukcji. Raport generowany jest
poleceniem `./mvnw clean verify` i dostępny w
`target/site/jacoco/index.html`.

![Raport pokrycia kodu
JaCoCo.](https://cdn.discordapp.com/attachments/1378123618453557559/1379127115584241795/Screenshot_2025-06-01_163451.png?ex=683f1bb7&is=683dca37&hm=dda9cf8729af2c8909de7a5b626236186827642a1c175210107c3c99dd107d87&){#fig:jacoco
width="\\textwidth"}

Szczegółowa analiza raportu pozwoliła na identyfikację obszarów
wymagających dodatkowych testów, np. w pakiecie `security` czy
niektórych gałęziach logiki w serwisach.

# Instrukcja Użytkownika

## Wymagania Systemowe

-   Java JDK 21

-   Apache Maven 3.6+

-   PostgreSQL (np. wersja 15 lub 16)

-   Klient API (np. Postman) lub przeglądarka internetowa

## Uruchomienie Aplikacji

1.  Sklonuj repozytorium projektu.

2.  Skonfiguruj połączenie z bazą danych PostgreSQL w pliku
    `src/main/resources/application.properties` (URL, użytkownik,
    hasło). Upewnij się, że baza danych istnieje i jest pusta (Flyway
    zajmie się schematem).

3.  Zbuduj projekt używając Mavena: `./mvnw clean install`

4.  Uruchom aplikację: `java -jar target/nazwa-twojego-jar-SNAPSHOT.jar`
    (zastąp `nazwa-twojego-jar` rzeczywistą nazwą) lub bezpośrednio z
    IDE.

5.  Aplikacja będzie dostępna pod adresem `http://localhost:8080`.

## Korzystanie z API

Interfejs API jest udokumentowany i dostępny do interakcji poprzez
Swagger UI pod adresem: `http://localhost:8080/swagger-ui.html`.

Dostępni użytkownicy (dane startowe z migracji Flyway):

-   Administrator: login `admin`, hasło `adminpass`

-   Użytkownik: login `user`, hasło `userpass`

Uwierzytelnianie odbywa się za pomocą HTTP Basic Auth.



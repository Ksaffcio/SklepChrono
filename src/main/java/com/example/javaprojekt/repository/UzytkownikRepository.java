
package com.example.javaprojekt.repository;

import com.example.javaprojekt.entity.Uzytkownik;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UzytkownikRepository extends JpaRepository<Uzytkownik, Long> {
    Optional<Uzytkownik> findByLogin(String login);
}
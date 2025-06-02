
package com.example.javaprojekt.repository;

import com.example.javaprojekt.entity.Rola;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolaRepository extends JpaRepository<Rola, Integer> {
    Optional<Rola> findByNazwa(String nazwa);
}
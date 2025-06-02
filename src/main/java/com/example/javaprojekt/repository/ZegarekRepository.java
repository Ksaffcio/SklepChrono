package com.example.javaprojekt.repository;

import com.example.javaprojekt.entity.Zegarek; // import encji
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ZegarekRepository extends JpaRepository<Zegarek, Long> {
    // JpaRepository dostarcza podstawowe metody CRUD (save, findById, findAll, deleteById, etc.)
    // dla encji Zegarek z kluczem glownym typu Long.

}
package com.opitech.superheroes.repository;

import com.opitech.superheroes.model.Hero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HeroRepository extends JpaRepository<Hero, Long> {

    //Validacion duplicados por nombre
    Optional<Hero> findByNameIgnoreCase(String name);

    //Busqueda parcial
    Page<Hero> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
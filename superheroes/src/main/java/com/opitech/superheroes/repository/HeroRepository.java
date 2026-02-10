package com.opitech.superheroes.repository;

import com.opitech.superheroes.model.Hero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HeroRepository extends JpaRepository<Hero, Long> {

    /**
     * Busca un héroe por su nombre para validacion de duplicados.
     *
     * @param name Nombre del héroe a buscar.
     * @return Un Optional que contiene el héroe si se encuentra, o vacío si no existe.
     */
    Optional<Hero> findByNameIgnoreCase(String name);

    /**
     * Busca héroes cuyo nombre contiene el texto especificado, ignorando mayúsculas y minúsculas.
     *
     * @param name     Texto a buscar dentro del nombre de los héroes.
     * @param pageable Información de paginación y ordenamiento.
     * @return Informacion de héroes que coinciden con la búsqueda.
     */
    Page<Hero> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
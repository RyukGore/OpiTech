package com.opitech.superheroes.service;

import com.opitech.superheroes.dto.HeroRequestDto;
import com.opitech.superheroes.dto.HeroResponseDto;
import com.opitech.superheroes.exception.HeroAlreadyExistsException;
import com.opitech.superheroes.exception.HeroNotFoundException;
import com.opitech.superheroes.mapper.HeroMapper;
import com.opitech.superheroes.model.Hero;
import com.opitech.superheroes.repository.HeroRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Servicio para la gestión de héroes.
 */
@Service
public class HeroService {

    private final HeroRepository heroRepository;

    public HeroService(HeroRepository heroRepository) {
        this.heroRepository = heroRepository;
    }

    /**
     * Retorna una página de héroes, aplicando paginación y ordenamiento.
     *
     * @param pageable información de paginación y ordenamiento (página, tamaño, sort).
     * @return página de héroes ya mapeados a DTO.
     */
    public Page<HeroResponseDto> getAllHeroes(Pageable pageable) {
        Page<Hero> page = heroRepository.findAll(pageable);
        return page.map(HeroMapper::toResponseDto);
    }

    /**
     * Retorna una página de héroes cuyo nombre contiene el texto de búsqueda, ignorando mayúsculas y espacios.
     * El parámetro de búsqueda debe tener al menos 2 caracteres no vacios.
     *
     * @param searchName texto a buscar dentro del nombre de los héroes.
     * @param pageable   información de paginación y ordenamiento.
     * @return Información de héroes que coinciden con la búsqueda.
     */
    public Page<HeroResponseDto> searchHeroesByName(String searchName, Pageable pageable) {
        if (searchName == null || searchName.trim().length() < 2) {
            throw new IllegalArgumentException("Parameter 'name' must have at least 2 non-blank characters");
        }

        String normalized = searchName.trim();

        Page<Hero> page = heroRepository.findByNameContainingIgnoreCase(normalized, pageable);
        return page.map(HeroMapper::toResponseDto);
    }

    /**
     * Retorna el héroe identificado por su ID.
     *
     * @param id ID del héroe a buscar.
     * @return Información del héroe.
     * @throws HeroNotFoundException si no se encuentra el héroe con el ID proporcionado.
     */
    public HeroResponseDto getHeroById(Long id) {
        Hero hero = heroRepository.findById(id)
                .orElseThrow(() -> new HeroNotFoundException(id));
        return HeroMapper.toResponseDto(hero);
    }

    /**
     * Crea un nuevo héroe.
     *
     * @param requestDto DTO con la información del héroe a crear.
     * @return DTO con la información del héroe creado.
     * @throws HeroAlreadyExistsException si ya existe un héroe con el mismo nombre.
     */
    public HeroResponseDto createHero(HeroRequestDto requestDto) {
        String name = requestDto.getName() != null
                ? requestDto.getName().trim()
                : null;

        // Comprobar duplicado por nombre
        heroRepository.findByNameIgnoreCase(name)
                .ifPresent(existing -> {
                    throw new HeroAlreadyExistsException(name);
                });

        Hero hero = HeroMapper.toEntity(requestDto);
        Hero saved = heroRepository.save(hero);
        return HeroMapper.toResponseDto(saved);
    }

    /**
     * Actualiza un héroe existente.
     *
     * @param id         ID del héroe a actualizar.
     * @param requestDto DTO con la información actualizada del héroe.
     * @return DTO con la información del héroe actualizado.
     * @throws HeroNotFoundException      si no se encuentra el héroe con el ID proporcionado.
     * @throws HeroAlreadyExistsException si ya existe otro héroe con el mismo nombre.
     */
    public HeroResponseDto updateHero(Long id, HeroRequestDto requestDto) {
        // Buscar el héroe existente o lanzar 404
        Hero existing = heroRepository.findById(id)
                .orElseThrow(() -> new HeroNotFoundException(id));

        // Normalizar nombre
        String newName = requestDto.getName() != null ? requestDto.getName().trim() : null;

        // Si el nombre cambia, comprobar duplicado
        if (newName != null && !newName.equalsIgnoreCase(existing.getName())) {
            heroRepository.findByNameIgnoreCase(newName)
                    .ifPresent(other -> {
                        throw new HeroAlreadyExistsException(newName);
                    });
        }

        // Actualizar la entidad con los datos del DTO
        HeroMapper.updateEntityFromDto(requestDto, existing);

        Hero saved = heroRepository.save(existing);
        return HeroMapper.toResponseDto(saved);
    }

    /**
     * Elimina un héroe existente.
     *
     * @param id ID del héroe a eliminar.
     * @throws HeroNotFoundException si no se encuentra el héroe con el ID proporcionado.
     */
    public void deleteHero(Long id) {
        Hero existing = heroRepository.findById(id)
                .orElseThrow(() -> new HeroNotFoundException(id));

        heroRepository.delete(existing);
    }
}
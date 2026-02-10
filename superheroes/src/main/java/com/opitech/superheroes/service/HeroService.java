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

@Service
public class HeroService {

    private final HeroRepository heroRepository;

    public HeroService(HeroRepository heroRepository) {
        this.heroRepository = heroRepository;
    }

    public Page<HeroResponseDto> getAllHeroes(Pageable pageable) {
        Page<Hero> page = heroRepository.findAll(pageable);
        return page.map(HeroMapper::toResponseDto);
    }

    public Page<HeroResponseDto> searchHeroesByName(String searchName, Pageable pageable) {
        if (searchName == null || searchName.trim().length() < 2) {
            throw new IllegalArgumentException("Parameter 'name' must have at least 2 non-blank characters");
        }

        String normalized = searchName.trim();

        Page<Hero> page = heroRepository.findByNameContainingIgnoreCase(normalized, pageable);
        return page.map(HeroMapper::toResponseDto);
    }

    public HeroResponseDto getHeroById(Long id) {
        Hero hero = heroRepository.findById(id)
                .orElseThrow(() -> new HeroNotFoundException(id));
        return HeroMapper.toResponseDto(hero);
    }

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

    public void deleteHero(Long id) {
        Hero existing = heroRepository.findById(id)
                .orElseThrow(() -> new HeroNotFoundException(id));

        heroRepository.delete(existing);
    }
}
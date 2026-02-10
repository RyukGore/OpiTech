package com.opitech.superheroes;

import com.opitech.superheroes.dto.HeroRequestDto;
import com.opitech.superheroes.dto.HeroResponseDto;
import com.opitech.superheroes.model.Hero;
import com.opitech.superheroes.exception.HeroAlreadyExistsException;
import com.opitech.superheroes.exception.HeroNotFoundException;
import com.opitech.superheroes.repository.HeroRepository;
import com.opitech.superheroes.service.HeroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HeroServiceTest {

    private HeroRepository heroRepository;
    private HeroService heroService;

    @BeforeEach
    void setUp() {
        heroRepository = mock(HeroRepository.class);
        heroService = new HeroService(heroRepository);
    }

    @Test
    void createHero_shouldCreateHero_whenNameIsUnique() {
        HeroRequestDto request = new HeroRequestDto();
        request.setName("  Superman  ");

        when(heroRepository.findByNameIgnoreCase("Superman"))
                .thenReturn(Optional.empty());

        Hero savedHero = new Hero();
        savedHero.setName("Superman");

        when(heroRepository.save(any(Hero.class)))
                .thenReturn(savedHero);

        HeroResponseDto response = heroService.createHero(request);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Superman");

        ArgumentCaptor<Hero> heroCaptor = ArgumentCaptor.forClass(Hero.class);
        verify(heroRepository).save(heroCaptor.capture());
        assertThat(heroCaptor.getValue().getName()).isEqualTo("Superman");
    }

    @Test
    void createHero_shouldThrowConflict_whenNameAlreadyExists() {
        HeroRequestDto request = new HeroRequestDto();
        request.setName("Batman");

        Hero existingHero = new Hero();
        existingHero.setName("Batman");

        when(heroRepository.findByNameIgnoreCase("Batman"))
                .thenReturn(Optional.of(existingHero));

        HeroAlreadyExistsException ex = assertThrows(
                HeroAlreadyExistsException.class,
                () -> heroService.createHero(request)
        );

        assertThat(ex.getMessage()).contains("Batman");
        verify(heroRepository, never()).save(any(Hero.class));
    }

    @Test
    void getHeroById_shouldReturnHero_whenExists() {

        Hero hero = new Hero();
        hero.setName("Flash");

        when(heroRepository.findById(5L))
                .thenReturn(Optional.of(hero));

        HeroResponseDto response = heroService.getHeroById(5L);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Flash");
    }

    @Test
    void getHeroById_shouldThrowNotFound_whenDoesNotExist() {
        when(heroRepository.findById(999L))
                .thenReturn(Optional.empty());

        HeroNotFoundException ex = assertThrows(
                HeroNotFoundException.class,
                () -> heroService.getHeroById(999L)
        );

        assertThat(ex.getMessage()).contains("999");
    }
}
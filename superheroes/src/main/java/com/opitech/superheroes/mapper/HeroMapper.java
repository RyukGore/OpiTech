package com.opitech.superheroes.mapper;

import com.opitech.superheroes.dto.HeroRequestDto;
import com.opitech.superheroes.dto.HeroResponseDto;
import com.opitech.superheroes.model.Hero;

public class HeroMapper {

    private HeroMapper() {
        // Utility class
    }

    /**
     * Mapea un HeroRequestDto a una entidad Hero.
     *
     * @param dto DTO de solicitud de héroe.
     * @return Entidad Hero mapeada.
     */
    public static Hero toEntity(HeroRequestDto dto) {
        Hero hero = new Hero();
        hero.setName(dto.getName() != null ? dto.getName().trim() : null);
        hero.setAlias(dto.getAlias());
        hero.setUniverse(dto.getUniverse());
        hero.setPowerLevel(dto.getPowerLevel());
        hero.setActive(dto.getActive() != null ? dto.getActive() : Boolean.TRUE);
        return hero;
    }

    /**
     * Actualiza una entidad Hero existente con los datos de un HeroRequestDto.
     *
     * @param dto  DTO de solicitud de héroe con los datos actualizados.
     * @param hero Entidad Hero a actualizar.
     */
    public static void updateEntityFromDto(HeroRequestDto dto, Hero hero) {
        hero.setName(dto.getName() != null ? dto.getName().trim() : null);
        hero.setAlias(dto.getAlias());
        hero.setUniverse(dto.getUniverse());
        hero.setPowerLevel(dto.getPowerLevel());
        if (dto.getActive() != null) {
            hero.setActive(dto.getActive());
        }
    }

    /**
     * Mapea una entidad Hero a un HeroResponseDto.
     *
     * @param hero Entidad Hero.
     * @return DTO de respuesta de héroe mapeado.
     */
    public static HeroResponseDto toResponseDto(Hero hero) {
        HeroResponseDto dto = new HeroResponseDto();
        dto.setId(hero.getId());
        dto.setName(hero.getName());
        dto.setAlias(hero.getAlias());
        dto.setUniverse(hero.getUniverse());
        dto.setPowerLevel(hero.getPowerLevel());
        dto.setActive(hero.getActive());
        dto.setCreatedAt(hero.getCreatedAt());
        dto.setUpdatedAt(hero.getUpdatedAt());
        return dto;
    }
}
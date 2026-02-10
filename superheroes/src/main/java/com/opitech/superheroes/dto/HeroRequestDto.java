package com.opitech.superheroes.dto;

import com.opitech.superheroes.model.Universe;
import jakarta.validation.constraints.*;

public class HeroRequestDto {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name length must be between 2 and 100 characters")
    private String name;

    @Size(max = 100, message = "Alias length must be at most 100 characters")
    private String alias;

    @NotNull(message = "Universe is required")
    private Universe universe;

    @NotNull(message = "Power level is required")
    @Min(value = 1, message = "Power level must be at least 1")
    @Max(value = 100, message = "Power level must be at most 100")
    private Integer powerLevel;

    private Boolean active;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Universe getUniverse() {
        return universe;
    }

    public void setUniverse(Universe universe) {
        this.universe = universe;
    }

    public Integer getPowerLevel() {
        return powerLevel;
    }

    public void setPowerLevel(Integer powerLevel) {
        this.powerLevel = powerLevel;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
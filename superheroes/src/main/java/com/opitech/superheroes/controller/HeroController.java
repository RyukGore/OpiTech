package com.opitech.superheroes.controller;

import com.opitech.superheroes.dto.HeroRequestDto;
import com.opitech.superheroes.dto.HeroResponseDto;
import com.opitech.superheroes.error.ApiError;
import com.opitech.superheroes.service.HeroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RestController
@RequestMapping("/api/v1/heroes")
@Tag(name = "Héroes", description = "API para la gestión de superhéroes")
public class HeroController {

    private final HeroService heroService;

    public HeroController(HeroService heroService) {
        this.heroService = heroService;
    }

    @Operation(
            summary = "Obtener todos los héroes",
            description = "Retorna una lista paginada de todos los superhéroes del sistema. Permite ordenamiento y paginación.",
            operationId = "getAllHeroes"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de héroes obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
            )
    })
    @GetMapping
    public Page<HeroResponseDto> getAllHeroes(
            @Parameter(
                    description = "Parámetros de paginación y ordenamiento. Ejemplo: ?page=0&size=10&sort=name,asc"
            )
            @PageableDefault(size = 10, sort = "name") Pageable pageable
    ) {
        return heroService.getAllHeroes(pageable);
    }


    @Operation(
            summary = "Obtener héroe por ID",
            description = "Retorna el detalle de un héroe identificado por su ID.",
            operationId = "getHeroById"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Héroe encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = HeroResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Héroe no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            ),
    })
    @GetMapping("/{id}")
    public HeroResponseDto getHeroById(
            @Parameter(description = "Identificador único del héroe", example = "1")
            @PathVariable Long id) {
        return heroService.getHeroById(id);
    }


    @Operation(
            summary = "Crear un nuevo héroe",
            description = "Crea un nuevo héroe con los datos proporcionados en el cuerpo de la petición.",
            operationId = "createHero"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Héroe creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = HeroResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (errores de validación)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(responseCode = "409", description = "Ya existe otro héroe con el mismo nombre",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            )
    })
    @PostMapping
    public ResponseEntity<HeroResponseDto> createHero(
            @Parameter(description = "Datos del héroe a crear", required = true)
            @Valid @RequestBody HeroRequestDto requestDto) {

        HeroResponseDto created = heroService.createHero(requestDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(created);
    }


    @Operation(
            summary = "Actualizar un héroe existente",
            description = "Actualiza los datos de un héroe identificado por su ID.",
            operationId = "updateHero"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Héroe actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = HeroResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (errores de validación)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(responseCode = "404", description = "Héroe no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            ),
            @ApiResponse(responseCode = "409", description = "Ya existe otro héroe con el mismo nombre",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            )
    })
    @PutMapping("/{id}")
    public HeroResponseDto updateHero(
            @Parameter(description = "Identificador único del héroe a actualizar", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nuevos datos del héroe", required = true)
            @Valid @RequestBody HeroRequestDto requestDto
    ) {
        return heroService.updateHero(id, requestDto);
    }


    @Operation(
            summary = "Eliminar un héroe",
            description = "Elimina un héroe identificado por su ID.",
            operationId = "deleteHero"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Héroe eliminado exitosamente (sin contenido en la respuesta)"),
            @ApiResponse(responseCode = "404", description = "Héroe no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            ),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHero(
            @Parameter(
                    description = "Identificador único del héroe a eliminar",
                    example = "1"
            )
            @PathVariable Long id
    ) {
        heroService.deleteHero(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Buscar héroes por nombre",
            description = "Busca héroes cuyo nombre contenga el texto indicado. Retorna una lista paginada.",
            operationId = "searchHeroes"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda de héroes realizada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
            )
    })
    @GetMapping("/search")
    public Page<HeroResponseDto> searchHeroes(
            @Parameter(
                    description = "Texto a buscar en el nombre del héroe (búsqueda parcial, case-insensitive)",
                    example = "man",
                    required = true
            )
            @RequestParam(name = "name") String name,
            @Parameter(
                    description = "Parámetros de paginación y ordenamiento. Ejemplo: ?page=0&size=10&sort=name,asc"
            )
            @PageableDefault(size = 10, sort = "name") Pageable pageable
    ) {
        return heroService.searchHeroesByName(name, pageable);
    }


}
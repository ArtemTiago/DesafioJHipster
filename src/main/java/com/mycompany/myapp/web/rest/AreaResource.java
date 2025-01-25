package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.AreaRepository;
import com.mycompany.myapp.service.AreaService;
import com.mycompany.myapp.service.dto.AreaDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Area}.
 */
@RestController
@RequestMapping("/api")
public class AreaResource {

    private final Logger log = LoggerFactory.getLogger(AreaResource.class);

    private static final String ENTITY_NAME = "area";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AreaService areaService;

    private final AreaRepository areaRepository;

    public AreaResource(AreaService areaService, AreaRepository areaRepository) {
        this.areaService = areaService;
        this.areaRepository = areaRepository;
    }

    /**
     * {@code POST  /areas} : Create a new area.
     *
     * @param areaDTO the areaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new areaDTO, or with status {@code 400 (Bad Request)} if the area has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/areas")
    public Mono<ResponseEntity<AreaDTO>> createArea(@Valid @RequestBody AreaDTO areaDTO) throws URISyntaxException {
        log.debug("REST request to save Area : {}", areaDTO);
        if (areaDTO.getId() != null) {
            throw new BadRequestAlertException("A new area cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return areaService
            .save(areaDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/areas/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /areas/:id} : Updates an existing area.
     *
     * @param id the id of the areaDTO to save.
     * @param areaDTO the areaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated areaDTO,
     * or with status {@code 400 (Bad Request)} if the areaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the areaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/areas/{id}")
    public Mono<ResponseEntity<AreaDTO>> updateArea(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AreaDTO areaDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Area : {}, {}", id, areaDTO);
        if (areaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, areaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return areaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return areaService
                    .update(areaDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /areas/:id} : Partial updates given fields of an existing area, field will ignore if it is null
     *
     * @param id the id of the areaDTO to save.
     * @param areaDTO the areaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated areaDTO,
     * or with status {@code 400 (Bad Request)} if the areaDTO is not valid,
     * or with status {@code 404 (Not Found)} if the areaDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the areaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/areas/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<AreaDTO>> partialUpdateArea(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AreaDTO areaDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Area partially : {}, {}", id, areaDTO);
        if (areaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, areaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return areaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<AreaDTO> result = areaService.partialUpdate(areaDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /areas} : get all the areas.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of areas in body.
     */
    @GetMapping("/areas")
    public Mono<ResponseEntity<List<AreaDTO>>> getAllAreas(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Areas");
        return areaService
            .countAll()
            .zipWith(areaService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /areas/:id} : get the "id" area.
     *
     * @param id the id of the areaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the areaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/areas/{id}")
    public Mono<ResponseEntity<AreaDTO>> getArea(@PathVariable Long id) {
        log.debug("REST request to get Area : {}", id);
        Mono<AreaDTO> areaDTO = areaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(areaDTO);
    }

    /**
     * {@code DELETE  /areas/:id} : delete the "id" area.
     *
     * @param id the id of the areaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/areas/{id}")
    public Mono<ResponseEntity<Void>> deleteArea(@PathVariable Long id) {
        log.debug("REST request to delete Area : {}", id);
        return areaService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}

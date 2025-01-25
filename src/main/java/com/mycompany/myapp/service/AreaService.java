package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Area;
import com.mycompany.myapp.repository.AreaRepository;
import com.mycompany.myapp.service.dto.AreaDTO;
import com.mycompany.myapp.service.mapper.AreaMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Area}.
 */
@Service
@Transactional
public class AreaService {

    private final Logger log = LoggerFactory.getLogger(AreaService.class);

    private final AreaRepository areaRepository;

    private final AreaMapper areaMapper;

    public AreaService(AreaRepository areaRepository, AreaMapper areaMapper) {
        this.areaRepository = areaRepository;
        this.areaMapper = areaMapper;
    }

    /**
     * Save a area.
     *
     * @param areaDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<AreaDTO> save(AreaDTO areaDTO) {
        log.debug("Request to save Area : {}", areaDTO);
        return areaRepository.save(areaMapper.toEntity(areaDTO)).map(areaMapper::toDto);
    }

    /**
     * Update a area.
     *
     * @param areaDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<AreaDTO> update(AreaDTO areaDTO) {
        log.debug("Request to update Area : {}", areaDTO);
        return areaRepository.save(areaMapper.toEntity(areaDTO)).map(areaMapper::toDto);
    }

    /**
     * Partially update a area.
     *
     * @param areaDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<AreaDTO> partialUpdate(AreaDTO areaDTO) {
        log.debug("Request to partially update Area : {}", areaDTO);

        return areaRepository
            .findById(areaDTO.getId())
            .map(existingArea -> {
                areaMapper.partialUpdate(existingArea, areaDTO);

                return existingArea;
            })
            .flatMap(areaRepository::save)
            .map(areaMapper::toDto);
    }

    /**
     * Get all the areas.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<AreaDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Areas");
        return areaRepository.findAllBy(pageable).map(areaMapper::toDto);
    }

    /**
     * Returns the number of areas available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return areaRepository.count();
    }

    /**
     * Get one area by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<AreaDTO> findOne(Long id) {
        log.debug("Request to get Area : {}", id);
        return areaRepository.findById(id).map(areaMapper::toDto);
    }

    /**
     * Delete the area by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Area : {}", id);
        return areaRepository.deleteById(id);
    }
}

package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Curso;
import com.mycompany.myapp.repository.CursoRepository;
import com.mycompany.myapp.service.dto.CursoDTO;
import com.mycompany.myapp.service.mapper.CursoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Curso}.
 */
@Service
@Transactional
public class CursoService {

    private final Logger log = LoggerFactory.getLogger(CursoService.class);

    private final CursoRepository cursoRepository;

    private final CursoMapper cursoMapper;

    public CursoService(CursoRepository cursoRepository, CursoMapper cursoMapper) {
        this.cursoRepository = cursoRepository;
        this.cursoMapper = cursoMapper;
    }

    /**
     * Save a curso.
     *
     * @param cursoDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CursoDTO> save(CursoDTO cursoDTO) {
        log.debug("Request to save Curso : {}", cursoDTO);
        return cursoRepository.save(cursoMapper.toEntity(cursoDTO)).map(cursoMapper::toDto);
    }

    /**
     * Update a curso.
     *
     * @param cursoDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CursoDTO> update(CursoDTO cursoDTO) {
        log.debug("Request to update Curso : {}", cursoDTO);
        return cursoRepository.save(cursoMapper.toEntity(cursoDTO)).map(cursoMapper::toDto);
    }

    /**
     * Partially update a curso.
     *
     * @param cursoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<CursoDTO> partialUpdate(CursoDTO cursoDTO) {
        log.debug("Request to partially update Curso : {}", cursoDTO);

        return cursoRepository
            .findById(cursoDTO.getId())
            .map(existingCurso -> {
                cursoMapper.partialUpdate(existingCurso, cursoDTO);

                return existingCurso;
            })
            .flatMap(cursoRepository::save)
            .map(cursoMapper::toDto);
    }

    /**
     * Get all the cursos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CursoDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Cursos");
        return cursoRepository.findAllBy(pageable).map(cursoMapper::toDto);
    }

    /**
     * Returns the number of cursos available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return cursoRepository.count();
    }

    /**
     * Get one curso by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<CursoDTO> findOne(Long id) {
        log.debug("Request to get Curso : {}", id);
        return cursoRepository.findById(id).map(cursoMapper::toDto);
    }

    /**
     * Delete the curso by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Curso : {}", id);
        return cursoRepository.deleteById(id);
    }
}

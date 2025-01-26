package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Curso;
import com.mycompany.myapp.domain.enumeration.StatusCurso;
import com.mycompany.myapp.repository.CursoRepository;
import com.mycompany.myapp.service.dto.CursoDTO;
import com.mycompany.myapp.service.mapper.CursoMapper;
import java.time.Instant;
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

        return isNomeDuplicado(cursoDTO.getNome(), null)
            .flatMap(isDuplicado -> {
                if (isDuplicado) {
                    return Mono.error(new RuntimeException("O curso: '" + cursoDTO.getNome() + "' já existe."));
                } else {
                    return cursoRepository.save(cursoMapper.toEntity(cursoDTO)).map(cursoMapper::toDto);
                }
            });
    }

    /**
     * Verificar se o nome do curso já existe, considerando o ID para não validar o curso sendo editado.
     *
     * @param nome Nome do curso.
     * @param id   ID do curso (pode ser null ao salvar novo curso).
     * @return Mono<Boolean> indicando se o nome já existe.
     */
    public Mono<Boolean> isNomeDuplicado(String nome, Long id) {
        return cursoRepository.findAll().filter(curso -> curso.getNome().equalsIgnoreCase(nome) && !curso.getId().equals(id)).hasElements();
    }

    /**
     * Update a curso.
     *
     * @param cursoDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CursoDTO> update(CursoDTO cursoDTO) {
        log.debug("Request to update Curso : {}", cursoDTO);

        if (cursoDTO.getStatus().equals(StatusCurso.INATIVO) && cursoDTO.getDataInatividade() == null) {
            cursoDTO.setDataInatividade(Instant.now());
        } else if (cursoDTO.getStatus().equals(StatusCurso.ATIVO) && cursoDTO.getDataInatividade() != null) {
            cursoDTO.setDataInatividade(null);
        }

        return isNomeDuplicado(cursoDTO.getNome(), cursoDTO.getId())
            .flatMap(isDuplicado -> {
                if (isDuplicado) {
                    return Mono.error(new RuntimeException("O curso: '" + cursoDTO.getNome() + "' já existe."));
                } else {
                    return cursoRepository.save(cursoMapper.toEntity(cursoDTO)).map(cursoMapper::toDto);
                }
            });
    }

    private String getOriginalNome(Long cursoId) {
        return cursoRepository.findById(cursoId).map(Curso::getNome).defaultIfEmpty("").block();
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

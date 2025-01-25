package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Curso;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Curso entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CursoRepository extends ReactiveCrudRepository<Curso, Long>, CursoRepositoryInternal {
    Flux<Curso> findAllBy(Pageable pageable);

    @Query("SELECT * FROM curso entity WHERE entity.area_id = :id")
    Flux<Curso> findByArea(Long id);

    @Query("SELECT * FROM curso entity WHERE entity.area_id IS NULL")
    Flux<Curso> findAllWhereAreaIsNull();

    @Override
    <S extends Curso> Mono<S> save(S entity);

    @Override
    Flux<Curso> findAll();

    @Override
    Mono<Curso> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CursoRepositoryInternal {
    <S extends Curso> Mono<S> save(S entity);

    Flux<Curso> findAllBy(Pageable pageable);

    Flux<Curso> findAll();

    Mono<Curso> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Curso> findAllBy(Pageable pageable, Criteria criteria);

}

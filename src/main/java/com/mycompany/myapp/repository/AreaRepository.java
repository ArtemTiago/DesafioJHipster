package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Area;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Area entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AreaRepository extends ReactiveCrudRepository<Area, Long>, AreaRepositoryInternal {
    Flux<Area> findAllBy(Pageable pageable);

    @Override
    <S extends Area> Mono<S> save(S entity);

    @Override
    Flux<Area> findAll();

    @Override
    Mono<Area> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AreaRepositoryInternal {
    <S extends Area> Mono<S> save(S entity);

    Flux<Area> findAllBy(Pageable pageable);

    Flux<Area> findAll();

    Mono<Area> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Area> findAllBy(Pageable pageable, Criteria criteria);

}

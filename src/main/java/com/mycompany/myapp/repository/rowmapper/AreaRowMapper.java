package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Area;
import com.mycompany.myapp.domain.enumeration.StatusCurso;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Area}, with proper type conversions.
 */
@Service
public class AreaRowMapper implements BiFunction<Row, String, Area> {

    private final ColumnConverter converter;

    public AreaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Area} stored in the database.
     */
    @Override
    public Area apply(Row row, String prefix) {
        Area entity = new Area();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNome(converter.fromRow(row, prefix + "_nome", String.class));
        entity.setDescricao(converter.fromRow(row, prefix + "_descricao", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", StatusCurso.class));
        entity.setDataCriacao(converter.fromRow(row, prefix + "_data_criacao", Instant.class));
        entity.setDataInatividade(converter.fromRow(row, prefix + "_data_inatividade", Instant.class));
        return entity;
    }
}

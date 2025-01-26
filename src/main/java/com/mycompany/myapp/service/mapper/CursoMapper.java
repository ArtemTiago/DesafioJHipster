package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Area;
import com.mycompany.myapp.domain.Curso;
import com.mycompany.myapp.service.dto.AreaDTO;
import com.mycompany.myapp.service.dto.CursoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Curso} and its DTO {@link CursoDTO}.
 */
@Mapper(componentModel = "spring")
public interface CursoMapper extends EntityMapper<CursoDTO, Curso> {
    @Mapping(target = "area", source = "area", qualifiedByName = "areaId")
    CursoDTO toDto(Curso s);

    @Named("areaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nome", source = "nome")
    AreaDTO toDtoAreaId(Area area);
}

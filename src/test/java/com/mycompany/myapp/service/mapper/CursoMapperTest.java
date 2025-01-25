package com.mycompany.myapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CursoMapperTest {

    private CursoMapper cursoMapper;

    @BeforeEach
    public void setUp() {
        cursoMapper = new CursoMapperImpl();
    }
}

package com.mycompany.myapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AreaMapperTest {

    private AreaMapper areaMapper;

    @BeforeEach
    public void setUp() {
        areaMapper = new AreaMapperImpl();
    }
}

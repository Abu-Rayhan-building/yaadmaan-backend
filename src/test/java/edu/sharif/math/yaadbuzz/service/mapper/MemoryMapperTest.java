package edu.sharif.math.yaadbuzz.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MemoryMapperTest {

    private MemoryMapper memoryMapper;

    @BeforeEach
    public void setUp() {
        memoryMapper = new MemoryMapperImpl();
    }
}

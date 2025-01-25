package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Area;
import com.mycompany.myapp.domain.enumeration.StatusCurso;
import com.mycompany.myapp.repository.AreaRepository;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.service.dto.AreaDTO;
import com.mycompany.myapp.service.mapper.AreaMapper;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link AreaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AreaResourceIT {

    private static final String DEFAULT_NOME = "AAAAAAAAAA";
    private static final String UPDATED_NOME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRICAO = "AAAAAAAAAA";
    private static final String UPDATED_DESCRICAO = "BBBBBBBBBB";

    private static final StatusCurso DEFAULT_STATUS = StatusCurso.ATIVO;
    private static final StatusCurso UPDATED_STATUS = StatusCurso.INATIVO;

    private static final Instant DEFAULT_DATA_CRIACAO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATA_CRIACAO = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DATA_INATIVIDADE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATA_INATIVIDADE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/areas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private AreaMapper areaMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Area area;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Area createEntity(EntityManager em) {
        Area area = new Area()
            .nome(DEFAULT_NOME)
            .descricao(DEFAULT_DESCRICAO)
            .status(DEFAULT_STATUS)
            .dataCriacao(DEFAULT_DATA_CRIACAO)
            .dataInatividade(DEFAULT_DATA_INATIVIDADE);
        return area;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Area createUpdatedEntity(EntityManager em) {
        Area area = new Area()
            .nome(UPDATED_NOME)
            .descricao(UPDATED_DESCRICAO)
            .status(UPDATED_STATUS)
            .dataCriacao(UPDATED_DATA_CRIACAO)
            .dataInatividade(UPDATED_DATA_INATIVIDADE);
        return area;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Area.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        area = createEntity(em);
    }

    @Test
    void createArea() throws Exception {
        int databaseSizeBeforeCreate = areaRepository.findAll().collectList().block().size();
        // Create the Area
        AreaDTO areaDTO = areaMapper.toDto(area);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(areaDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Area in the database
        List<Area> areaList = areaRepository.findAll().collectList().block();
        assertThat(areaList).hasSize(databaseSizeBeforeCreate + 1);
        Area testArea = areaList.get(areaList.size() - 1);
        assertThat(testArea.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testArea.getDescricao()).isEqualTo(DEFAULT_DESCRICAO);
        assertThat(testArea.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testArea.getDataCriacao()).isEqualTo(DEFAULT_DATA_CRIACAO);
        assertThat(testArea.getDataInatividade()).isEqualTo(DEFAULT_DATA_INATIVIDADE);
    }

    @Test
    void createAreaWithExistingId() throws Exception {
        // Create the Area with an existing ID
        area.setId(1L);
        AreaDTO areaDTO = areaMapper.toDto(area);

        int databaseSizeBeforeCreate = areaRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(areaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Area in the database
        List<Area> areaList = areaRepository.findAll().collectList().block();
        assertThat(areaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNomeIsRequired() throws Exception {
        int databaseSizeBeforeTest = areaRepository.findAll().collectList().block().size();
        // set the field null
        area.setNome(null);

        // Create the Area, which fails.
        AreaDTO areaDTO = areaMapper.toDto(area);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(areaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Area> areaList = areaRepository.findAll().collectList().block();
        assertThat(areaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = areaRepository.findAll().collectList().block().size();
        // set the field null
        area.setStatus(null);

        // Create the Area, which fails.
        AreaDTO areaDTO = areaMapper.toDto(area);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(areaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Area> areaList = areaRepository.findAll().collectList().block();
        assertThat(areaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkDataCriacaoIsRequired() throws Exception {
        int databaseSizeBeforeTest = areaRepository.findAll().collectList().block().size();
        // set the field null
        area.setDataCriacao(null);

        // Create the Area, which fails.
        AreaDTO areaDTO = areaMapper.toDto(area);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(areaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Area> areaList = areaRepository.findAll().collectList().block();
        assertThat(areaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllAreas() {
        // Initialize the database
        areaRepository.save(area).block();

        // Get all the areaList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(area.getId().intValue()))
            .jsonPath("$.[*].nome")
            .value(hasItem(DEFAULT_NOME))
            .jsonPath("$.[*].descricao")
            .value(hasItem(DEFAULT_DESCRICAO))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].dataCriacao")
            .value(hasItem(DEFAULT_DATA_CRIACAO.toString()))
            .jsonPath("$.[*].dataInatividade")
            .value(hasItem(DEFAULT_DATA_INATIVIDADE.toString()));
    }

    @Test
    void getArea() {
        // Initialize the database
        areaRepository.save(area).block();

        // Get the area
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, area.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(area.getId().intValue()))
            .jsonPath("$.nome")
            .value(is(DEFAULT_NOME))
            .jsonPath("$.descricao")
            .value(is(DEFAULT_DESCRICAO))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.dataCriacao")
            .value(is(DEFAULT_DATA_CRIACAO.toString()))
            .jsonPath("$.dataInatividade")
            .value(is(DEFAULT_DATA_INATIVIDADE.toString()));
    }

    @Test
    void getNonExistingArea() {
        // Get the area
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingArea() throws Exception {
        // Initialize the database
        areaRepository.save(area).block();

        int databaseSizeBeforeUpdate = areaRepository.findAll().collectList().block().size();

        // Update the area
        Area updatedArea = areaRepository.findById(area.getId()).block();
        updatedArea
            .nome(UPDATED_NOME)
            .descricao(UPDATED_DESCRICAO)
            .status(UPDATED_STATUS)
            .dataCriacao(UPDATED_DATA_CRIACAO)
            .dataInatividade(UPDATED_DATA_INATIVIDADE);
        AreaDTO areaDTO = areaMapper.toDto(updatedArea);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, areaDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(areaDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Area in the database
        List<Area> areaList = areaRepository.findAll().collectList().block();
        assertThat(areaList).hasSize(databaseSizeBeforeUpdate);
        Area testArea = areaList.get(areaList.size() - 1);
        assertThat(testArea.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testArea.getDescricao()).isEqualTo(UPDATED_DESCRICAO);
        assertThat(testArea.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testArea.getDataCriacao()).isEqualTo(UPDATED_DATA_CRIACAO);
        assertThat(testArea.getDataInatividade()).isEqualTo(UPDATED_DATA_INATIVIDADE);
    }

    @Test
    void putNonExistingArea() throws Exception {
        int databaseSizeBeforeUpdate = areaRepository.findAll().collectList().block().size();
        area.setId(count.incrementAndGet());

        // Create the Area
        AreaDTO areaDTO = areaMapper.toDto(area);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, areaDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(areaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Area in the database
        List<Area> areaList = areaRepository.findAll().collectList().block();
        assertThat(areaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchArea() throws Exception {
        int databaseSizeBeforeUpdate = areaRepository.findAll().collectList().block().size();
        area.setId(count.incrementAndGet());

        // Create the Area
        AreaDTO areaDTO = areaMapper.toDto(area);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(areaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Area in the database
        List<Area> areaList = areaRepository.findAll().collectList().block();
        assertThat(areaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamArea() throws Exception {
        int databaseSizeBeforeUpdate = areaRepository.findAll().collectList().block().size();
        area.setId(count.incrementAndGet());

        // Create the Area
        AreaDTO areaDTO = areaMapper.toDto(area);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(areaDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Area in the database
        List<Area> areaList = areaRepository.findAll().collectList().block();
        assertThat(areaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAreaWithPatch() throws Exception {
        // Initialize the database
        areaRepository.save(area).block();

        int databaseSizeBeforeUpdate = areaRepository.findAll().collectList().block().size();

        // Update the area using partial update
        Area partialUpdatedArea = new Area();
        partialUpdatedArea.setId(area.getId());

        partialUpdatedArea
            .nome(UPDATED_NOME)
            .descricao(UPDATED_DESCRICAO)
            .dataCriacao(UPDATED_DATA_CRIACAO)
            .dataInatividade(UPDATED_DATA_INATIVIDADE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedArea.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedArea))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Area in the database
        List<Area> areaList = areaRepository.findAll().collectList().block();
        assertThat(areaList).hasSize(databaseSizeBeforeUpdate);
        Area testArea = areaList.get(areaList.size() - 1);
        assertThat(testArea.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testArea.getDescricao()).isEqualTo(UPDATED_DESCRICAO);
        assertThat(testArea.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testArea.getDataCriacao()).isEqualTo(UPDATED_DATA_CRIACAO);
        assertThat(testArea.getDataInatividade()).isEqualTo(UPDATED_DATA_INATIVIDADE);
    }

    @Test
    void fullUpdateAreaWithPatch() throws Exception {
        // Initialize the database
        areaRepository.save(area).block();

        int databaseSizeBeforeUpdate = areaRepository.findAll().collectList().block().size();

        // Update the area using partial update
        Area partialUpdatedArea = new Area();
        partialUpdatedArea.setId(area.getId());

        partialUpdatedArea
            .nome(UPDATED_NOME)
            .descricao(UPDATED_DESCRICAO)
            .status(UPDATED_STATUS)
            .dataCriacao(UPDATED_DATA_CRIACAO)
            .dataInatividade(UPDATED_DATA_INATIVIDADE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedArea.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedArea))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Area in the database
        List<Area> areaList = areaRepository.findAll().collectList().block();
        assertThat(areaList).hasSize(databaseSizeBeforeUpdate);
        Area testArea = areaList.get(areaList.size() - 1);
        assertThat(testArea.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testArea.getDescricao()).isEqualTo(UPDATED_DESCRICAO);
        assertThat(testArea.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testArea.getDataCriacao()).isEqualTo(UPDATED_DATA_CRIACAO);
        assertThat(testArea.getDataInatividade()).isEqualTo(UPDATED_DATA_INATIVIDADE);
    }

    @Test
    void patchNonExistingArea() throws Exception {
        int databaseSizeBeforeUpdate = areaRepository.findAll().collectList().block().size();
        area.setId(count.incrementAndGet());

        // Create the Area
        AreaDTO areaDTO = areaMapper.toDto(area);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, areaDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(areaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Area in the database
        List<Area> areaList = areaRepository.findAll().collectList().block();
        assertThat(areaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchArea() throws Exception {
        int databaseSizeBeforeUpdate = areaRepository.findAll().collectList().block().size();
        area.setId(count.incrementAndGet());

        // Create the Area
        AreaDTO areaDTO = areaMapper.toDto(area);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(areaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Area in the database
        List<Area> areaList = areaRepository.findAll().collectList().block();
        assertThat(areaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamArea() throws Exception {
        int databaseSizeBeforeUpdate = areaRepository.findAll().collectList().block().size();
        area.setId(count.incrementAndGet());

        // Create the Area
        AreaDTO areaDTO = areaMapper.toDto(area);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(areaDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Area in the database
        List<Area> areaList = areaRepository.findAll().collectList().block();
        assertThat(areaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteArea() {
        // Initialize the database
        areaRepository.save(area).block();

        int databaseSizeBeforeDelete = areaRepository.findAll().collectList().block().size();

        // Delete the area
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, area.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Area> areaList = areaRepository.findAll().collectList().block();
        assertThat(areaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

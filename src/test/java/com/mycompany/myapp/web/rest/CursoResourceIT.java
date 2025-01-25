package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Curso;
import com.mycompany.myapp.domain.enumeration.StatusCurso;
import com.mycompany.myapp.repository.CursoRepository;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.service.dto.CursoDTO;
import com.mycompany.myapp.service.mapper.CursoMapper;
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
 * Integration tests for the {@link CursoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CursoResourceIT {

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

    private static final String ENTITY_API_URL = "/api/cursos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private CursoMapper cursoMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Curso curso;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Curso createEntity(EntityManager em) {
        Curso curso = new Curso()
            .nome(DEFAULT_NOME)
            .descricao(DEFAULT_DESCRICAO)
            .status(DEFAULT_STATUS)
            .dataCriacao(DEFAULT_DATA_CRIACAO)
            .dataInatividade(DEFAULT_DATA_INATIVIDADE);
        return curso;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Curso createUpdatedEntity(EntityManager em) {
        Curso curso = new Curso()
            .nome(UPDATED_NOME)
            .descricao(UPDATED_DESCRICAO)
            .status(UPDATED_STATUS)
            .dataCriacao(UPDATED_DATA_CRIACAO)
            .dataInatividade(UPDATED_DATA_INATIVIDADE);
        return curso;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Curso.class).block();
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
        curso = createEntity(em);
    }

    @Test
    void createCurso() throws Exception {
        int databaseSizeBeforeCreate = cursoRepository.findAll().collectList().block().size();
        // Create the Curso
        CursoDTO cursoDTO = cursoMapper.toDto(curso);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cursoDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Curso in the database
        List<Curso> cursoList = cursoRepository.findAll().collectList().block();
        assertThat(cursoList).hasSize(databaseSizeBeforeCreate + 1);
        Curso testCurso = cursoList.get(cursoList.size() - 1);
        assertThat(testCurso.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testCurso.getDescricao()).isEqualTo(DEFAULT_DESCRICAO);
        assertThat(testCurso.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testCurso.getDataCriacao()).isEqualTo(DEFAULT_DATA_CRIACAO);
        assertThat(testCurso.getDataInatividade()).isEqualTo(DEFAULT_DATA_INATIVIDADE);
    }

    @Test
    void createCursoWithExistingId() throws Exception {
        // Create the Curso with an existing ID
        curso.setId(1L);
        CursoDTO cursoDTO = cursoMapper.toDto(curso);

        int databaseSizeBeforeCreate = cursoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cursoDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Curso in the database
        List<Curso> cursoList = cursoRepository.findAll().collectList().block();
        assertThat(cursoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNomeIsRequired() throws Exception {
        int databaseSizeBeforeTest = cursoRepository.findAll().collectList().block().size();
        // set the field null
        curso.setNome(null);

        // Create the Curso, which fails.
        CursoDTO cursoDTO = cursoMapper.toDto(curso);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cursoDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Curso> cursoList = cursoRepository.findAll().collectList().block();
        assertThat(cursoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = cursoRepository.findAll().collectList().block().size();
        // set the field null
        curso.setStatus(null);

        // Create the Curso, which fails.
        CursoDTO cursoDTO = cursoMapper.toDto(curso);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cursoDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Curso> cursoList = cursoRepository.findAll().collectList().block();
        assertThat(cursoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkDataCriacaoIsRequired() throws Exception {
        int databaseSizeBeforeTest = cursoRepository.findAll().collectList().block().size();
        // set the field null
        curso.setDataCriacao(null);

        // Create the Curso, which fails.
        CursoDTO cursoDTO = cursoMapper.toDto(curso);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cursoDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Curso> cursoList = cursoRepository.findAll().collectList().block();
        assertThat(cursoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllCursos() {
        // Initialize the database
        cursoRepository.save(curso).block();

        // Get all the cursoList
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
            .value(hasItem(curso.getId().intValue()))
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
    void getCurso() {
        // Initialize the database
        cursoRepository.save(curso).block();

        // Get the curso
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, curso.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(curso.getId().intValue()))
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
    void getNonExistingCurso() {
        // Get the curso
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingCurso() throws Exception {
        // Initialize the database
        cursoRepository.save(curso).block();

        int databaseSizeBeforeUpdate = cursoRepository.findAll().collectList().block().size();

        // Update the curso
        Curso updatedCurso = cursoRepository.findById(curso.getId()).block();
        updatedCurso
            .nome(UPDATED_NOME)
            .descricao(UPDATED_DESCRICAO)
            .status(UPDATED_STATUS)
            .dataCriacao(UPDATED_DATA_CRIACAO)
            .dataInatividade(UPDATED_DATA_INATIVIDADE);
        CursoDTO cursoDTO = cursoMapper.toDto(updatedCurso);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, cursoDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cursoDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Curso in the database
        List<Curso> cursoList = cursoRepository.findAll().collectList().block();
        assertThat(cursoList).hasSize(databaseSizeBeforeUpdate);
        Curso testCurso = cursoList.get(cursoList.size() - 1);
        assertThat(testCurso.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testCurso.getDescricao()).isEqualTo(UPDATED_DESCRICAO);
        assertThat(testCurso.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testCurso.getDataCriacao()).isEqualTo(UPDATED_DATA_CRIACAO);
        assertThat(testCurso.getDataInatividade()).isEqualTo(UPDATED_DATA_INATIVIDADE);
    }

    @Test
    void putNonExistingCurso() throws Exception {
        int databaseSizeBeforeUpdate = cursoRepository.findAll().collectList().block().size();
        curso.setId(count.incrementAndGet());

        // Create the Curso
        CursoDTO cursoDTO = cursoMapper.toDto(curso);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, cursoDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cursoDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Curso in the database
        List<Curso> cursoList = cursoRepository.findAll().collectList().block();
        assertThat(cursoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCurso() throws Exception {
        int databaseSizeBeforeUpdate = cursoRepository.findAll().collectList().block().size();
        curso.setId(count.incrementAndGet());

        // Create the Curso
        CursoDTO cursoDTO = cursoMapper.toDto(curso);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cursoDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Curso in the database
        List<Curso> cursoList = cursoRepository.findAll().collectList().block();
        assertThat(cursoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCurso() throws Exception {
        int databaseSizeBeforeUpdate = cursoRepository.findAll().collectList().block().size();
        curso.setId(count.incrementAndGet());

        // Create the Curso
        CursoDTO cursoDTO = cursoMapper.toDto(curso);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cursoDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Curso in the database
        List<Curso> cursoList = cursoRepository.findAll().collectList().block();
        assertThat(cursoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCursoWithPatch() throws Exception {
        // Initialize the database
        cursoRepository.save(curso).block();

        int databaseSizeBeforeUpdate = cursoRepository.findAll().collectList().block().size();

        // Update the curso using partial update
        Curso partialUpdatedCurso = new Curso();
        partialUpdatedCurso.setId(curso.getId());

        partialUpdatedCurso.nome(UPDATED_NOME).descricao(UPDATED_DESCRICAO).status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCurso.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCurso))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Curso in the database
        List<Curso> cursoList = cursoRepository.findAll().collectList().block();
        assertThat(cursoList).hasSize(databaseSizeBeforeUpdate);
        Curso testCurso = cursoList.get(cursoList.size() - 1);
        assertThat(testCurso.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testCurso.getDescricao()).isEqualTo(UPDATED_DESCRICAO);
        assertThat(testCurso.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testCurso.getDataCriacao()).isEqualTo(DEFAULT_DATA_CRIACAO);
        assertThat(testCurso.getDataInatividade()).isEqualTo(DEFAULT_DATA_INATIVIDADE);
    }

    @Test
    void fullUpdateCursoWithPatch() throws Exception {
        // Initialize the database
        cursoRepository.save(curso).block();

        int databaseSizeBeforeUpdate = cursoRepository.findAll().collectList().block().size();

        // Update the curso using partial update
        Curso partialUpdatedCurso = new Curso();
        partialUpdatedCurso.setId(curso.getId());

        partialUpdatedCurso
            .nome(UPDATED_NOME)
            .descricao(UPDATED_DESCRICAO)
            .status(UPDATED_STATUS)
            .dataCriacao(UPDATED_DATA_CRIACAO)
            .dataInatividade(UPDATED_DATA_INATIVIDADE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCurso.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCurso))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Curso in the database
        List<Curso> cursoList = cursoRepository.findAll().collectList().block();
        assertThat(cursoList).hasSize(databaseSizeBeforeUpdate);
        Curso testCurso = cursoList.get(cursoList.size() - 1);
        assertThat(testCurso.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testCurso.getDescricao()).isEqualTo(UPDATED_DESCRICAO);
        assertThat(testCurso.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testCurso.getDataCriacao()).isEqualTo(UPDATED_DATA_CRIACAO);
        assertThat(testCurso.getDataInatividade()).isEqualTo(UPDATED_DATA_INATIVIDADE);
    }

    @Test
    void patchNonExistingCurso() throws Exception {
        int databaseSizeBeforeUpdate = cursoRepository.findAll().collectList().block().size();
        curso.setId(count.incrementAndGet());

        // Create the Curso
        CursoDTO cursoDTO = cursoMapper.toDto(curso);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, cursoDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cursoDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Curso in the database
        List<Curso> cursoList = cursoRepository.findAll().collectList().block();
        assertThat(cursoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCurso() throws Exception {
        int databaseSizeBeforeUpdate = cursoRepository.findAll().collectList().block().size();
        curso.setId(count.incrementAndGet());

        // Create the Curso
        CursoDTO cursoDTO = cursoMapper.toDto(curso);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cursoDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Curso in the database
        List<Curso> cursoList = cursoRepository.findAll().collectList().block();
        assertThat(cursoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCurso() throws Exception {
        int databaseSizeBeforeUpdate = cursoRepository.findAll().collectList().block().size();
        curso.setId(count.incrementAndGet());

        // Create the Curso
        CursoDTO cursoDTO = cursoMapper.toDto(curso);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cursoDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Curso in the database
        List<Curso> cursoList = cursoRepository.findAll().collectList().block();
        assertThat(cursoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCurso() {
        // Initialize the database
        cursoRepository.save(curso).block();

        int databaseSizeBeforeDelete = cursoRepository.findAll().collectList().block().size();

        // Delete the curso
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, curso.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Curso> cursoList = cursoRepository.findAll().collectList().block();
        assertThat(cursoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

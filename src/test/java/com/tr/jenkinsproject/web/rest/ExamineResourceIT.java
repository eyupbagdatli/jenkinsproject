package com.tr.jenkinsproject.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tr.jenkinsproject.IntegrationTest;
import com.tr.jenkinsproject.domain.Examine;
import com.tr.jenkinsproject.repository.ExamineRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ExamineResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ExamineResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/examines";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ExamineRepository examineRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restExamineMockMvc;

    private Examine examine;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Examine createEntity(EntityManager em) {
        Examine examine = new Examine().name(DEFAULT_NAME);
        return examine;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Examine createUpdatedEntity(EntityManager em) {
        Examine examine = new Examine().name(UPDATED_NAME);
        return examine;
    }

    @BeforeEach
    public void initTest() {
        examine = createEntity(em);
    }

    @Test
    @Transactional
    void createExamine() throws Exception {
        int databaseSizeBeforeCreate = examineRepository.findAll().size();
        // Create the Examine
        restExamineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(examine)))
            .andExpect(status().isCreated());

        // Validate the Examine in the database
        List<Examine> examineList = examineRepository.findAll();
        assertThat(examineList).hasSize(databaseSizeBeforeCreate + 1);
        Examine testExamine = examineList.get(examineList.size() - 1);
        assertThat(testExamine.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createExamineWithExistingId() throws Exception {
        // Create the Examine with an existing ID
        examine.setId(1L);

        int databaseSizeBeforeCreate = examineRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restExamineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(examine)))
            .andExpect(status().isBadRequest());

        // Validate the Examine in the database
        List<Examine> examineList = examineRepository.findAll();
        assertThat(examineList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllExamines() throws Exception {
        // Initialize the database
        examineRepository.saveAndFlush(examine);

        // Get all the examineList
        restExamineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(examine.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getExamine() throws Exception {
        // Initialize the database
        examineRepository.saveAndFlush(examine);

        // Get the examine
        restExamineMockMvc
            .perform(get(ENTITY_API_URL_ID, examine.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(examine.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingExamine() throws Exception {
        // Get the examine
        restExamineMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewExamine() throws Exception {
        // Initialize the database
        examineRepository.saveAndFlush(examine);

        int databaseSizeBeforeUpdate = examineRepository.findAll().size();

        // Update the examine
        Examine updatedExamine = examineRepository.findById(examine.getId()).get();
        // Disconnect from session so that the updates on updatedExamine are not directly saved in db
        em.detach(updatedExamine);
        updatedExamine.name(UPDATED_NAME);

        restExamineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedExamine.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedExamine))
            )
            .andExpect(status().isOk());

        // Validate the Examine in the database
        List<Examine> examineList = examineRepository.findAll();
        assertThat(examineList).hasSize(databaseSizeBeforeUpdate);
        Examine testExamine = examineList.get(examineList.size() - 1);
        assertThat(testExamine.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExistingExamine() throws Exception {
        int databaseSizeBeforeUpdate = examineRepository.findAll().size();
        examine.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExamineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, examine.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(examine))
            )
            .andExpect(status().isBadRequest());

        // Validate the Examine in the database
        List<Examine> examineList = examineRepository.findAll();
        assertThat(examineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchExamine() throws Exception {
        int databaseSizeBeforeUpdate = examineRepository.findAll().size();
        examine.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(examine))
            )
            .andExpect(status().isBadRequest());

        // Validate the Examine in the database
        List<Examine> examineList = examineRepository.findAll();
        assertThat(examineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamExamine() throws Exception {
        int databaseSizeBeforeUpdate = examineRepository.findAll().size();
        examine.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamineMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(examine)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Examine in the database
        List<Examine> examineList = examineRepository.findAll();
        assertThat(examineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateExamineWithPatch() throws Exception {
        // Initialize the database
        examineRepository.saveAndFlush(examine);

        int databaseSizeBeforeUpdate = examineRepository.findAll().size();

        // Update the examine using partial update
        Examine partialUpdatedExamine = new Examine();
        partialUpdatedExamine.setId(examine.getId());

        restExamineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExamine.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedExamine))
            )
            .andExpect(status().isOk());

        // Validate the Examine in the database
        List<Examine> examineList = examineRepository.findAll();
        assertThat(examineList).hasSize(databaseSizeBeforeUpdate);
        Examine testExamine = examineList.get(examineList.size() - 1);
        assertThat(testExamine.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void fullUpdateExamineWithPatch() throws Exception {
        // Initialize the database
        examineRepository.saveAndFlush(examine);

        int databaseSizeBeforeUpdate = examineRepository.findAll().size();

        // Update the examine using partial update
        Examine partialUpdatedExamine = new Examine();
        partialUpdatedExamine.setId(examine.getId());

        partialUpdatedExamine.name(UPDATED_NAME);

        restExamineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExamine.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedExamine))
            )
            .andExpect(status().isOk());

        // Validate the Examine in the database
        List<Examine> examineList = examineRepository.findAll();
        assertThat(examineList).hasSize(databaseSizeBeforeUpdate);
        Examine testExamine = examineList.get(examineList.size() - 1);
        assertThat(testExamine.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingExamine() throws Exception {
        int databaseSizeBeforeUpdate = examineRepository.findAll().size();
        examine.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExamineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, examine.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(examine))
            )
            .andExpect(status().isBadRequest());

        // Validate the Examine in the database
        List<Examine> examineList = examineRepository.findAll();
        assertThat(examineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchExamine() throws Exception {
        int databaseSizeBeforeUpdate = examineRepository.findAll().size();
        examine.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(examine))
            )
            .andExpect(status().isBadRequest());

        // Validate the Examine in the database
        List<Examine> examineList = examineRepository.findAll();
        assertThat(examineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamExamine() throws Exception {
        int databaseSizeBeforeUpdate = examineRepository.findAll().size();
        examine.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamineMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(examine)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Examine in the database
        List<Examine> examineList = examineRepository.findAll();
        assertThat(examineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteExamine() throws Exception {
        // Initialize the database
        examineRepository.saveAndFlush(examine);

        int databaseSizeBeforeDelete = examineRepository.findAll().size();

        // Delete the examine
        restExamineMockMvc
            .perform(delete(ENTITY_API_URL_ID, examine.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Examine> examineList = examineRepository.findAll();
        assertThat(examineList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

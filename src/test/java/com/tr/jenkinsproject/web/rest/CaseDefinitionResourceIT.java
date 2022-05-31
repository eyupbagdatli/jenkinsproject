package com.tr.jenkinsproject.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tr.jenkinsproject.IntegrationTest;
import com.tr.jenkinsproject.domain.CaseDefinition;
import com.tr.jenkinsproject.repository.CaseDefinitionRepository;
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
 * Integration tests for the {@link CaseDefinitionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CaseDefinitionResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/case-definitions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CaseDefinitionRepository caseDefinitionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCaseDefinitionMockMvc;

    private CaseDefinition caseDefinition;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CaseDefinition createEntity(EntityManager em) {
        CaseDefinition caseDefinition = new CaseDefinition().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION).active(DEFAULT_ACTIVE);
        return caseDefinition;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CaseDefinition createUpdatedEntity(EntityManager em) {
        CaseDefinition caseDefinition = new CaseDefinition().name(UPDATED_NAME).description(UPDATED_DESCRIPTION).active(UPDATED_ACTIVE);
        return caseDefinition;
    }

    @BeforeEach
    public void initTest() {
        caseDefinition = createEntity(em);
    }

    @Test
    @Transactional
    void createCaseDefinition() throws Exception {
        int databaseSizeBeforeCreate = caseDefinitionRepository.findAll().size();
        // Create the CaseDefinition
        restCaseDefinitionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(caseDefinition))
            )
            .andExpect(status().isCreated());

        // Validate the CaseDefinition in the database
        List<CaseDefinition> caseDefinitionList = caseDefinitionRepository.findAll();
        assertThat(caseDefinitionList).hasSize(databaseSizeBeforeCreate + 1);
        CaseDefinition testCaseDefinition = caseDefinitionList.get(caseDefinitionList.size() - 1);
        assertThat(testCaseDefinition.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCaseDefinition.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCaseDefinition.getActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    void createCaseDefinitionWithExistingId() throws Exception {
        // Create the CaseDefinition with an existing ID
        caseDefinition.setId(1L);

        int databaseSizeBeforeCreate = caseDefinitionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCaseDefinitionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(caseDefinition))
            )
            .andExpect(status().isBadRequest());

        // Validate the CaseDefinition in the database
        List<CaseDefinition> caseDefinitionList = caseDefinitionRepository.findAll();
        assertThat(caseDefinitionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCaseDefinitions() throws Exception {
        // Initialize the database
        caseDefinitionRepository.saveAndFlush(caseDefinition);

        // Get all the caseDefinitionList
        restCaseDefinitionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(caseDefinition.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));
    }

    @Test
    @Transactional
    void getCaseDefinition() throws Exception {
        // Initialize the database
        caseDefinitionRepository.saveAndFlush(caseDefinition);

        // Get the caseDefinition
        restCaseDefinitionMockMvc
            .perform(get(ENTITY_API_URL_ID, caseDefinition.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(caseDefinition.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingCaseDefinition() throws Exception {
        // Get the caseDefinition
        restCaseDefinitionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCaseDefinition() throws Exception {
        // Initialize the database
        caseDefinitionRepository.saveAndFlush(caseDefinition);

        int databaseSizeBeforeUpdate = caseDefinitionRepository.findAll().size();

        // Update the caseDefinition
        CaseDefinition updatedCaseDefinition = caseDefinitionRepository.findById(caseDefinition.getId()).get();
        // Disconnect from session so that the updates on updatedCaseDefinition are not directly saved in db
        em.detach(updatedCaseDefinition);
        updatedCaseDefinition.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).active(UPDATED_ACTIVE);

        restCaseDefinitionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCaseDefinition.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCaseDefinition))
            )
            .andExpect(status().isOk());

        // Validate the CaseDefinition in the database
        List<CaseDefinition> caseDefinitionList = caseDefinitionRepository.findAll();
        assertThat(caseDefinitionList).hasSize(databaseSizeBeforeUpdate);
        CaseDefinition testCaseDefinition = caseDefinitionList.get(caseDefinitionList.size() - 1);
        assertThat(testCaseDefinition.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCaseDefinition.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCaseDefinition.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void putNonExistingCaseDefinition() throws Exception {
        int databaseSizeBeforeUpdate = caseDefinitionRepository.findAll().size();
        caseDefinition.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCaseDefinitionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, caseDefinition.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(caseDefinition))
            )
            .andExpect(status().isBadRequest());

        // Validate the CaseDefinition in the database
        List<CaseDefinition> caseDefinitionList = caseDefinitionRepository.findAll();
        assertThat(caseDefinitionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCaseDefinition() throws Exception {
        int databaseSizeBeforeUpdate = caseDefinitionRepository.findAll().size();
        caseDefinition.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCaseDefinitionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(caseDefinition))
            )
            .andExpect(status().isBadRequest());

        // Validate the CaseDefinition in the database
        List<CaseDefinition> caseDefinitionList = caseDefinitionRepository.findAll();
        assertThat(caseDefinitionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCaseDefinition() throws Exception {
        int databaseSizeBeforeUpdate = caseDefinitionRepository.findAll().size();
        caseDefinition.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCaseDefinitionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(caseDefinition)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CaseDefinition in the database
        List<CaseDefinition> caseDefinitionList = caseDefinitionRepository.findAll();
        assertThat(caseDefinitionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCaseDefinitionWithPatch() throws Exception {
        // Initialize the database
        caseDefinitionRepository.saveAndFlush(caseDefinition);

        int databaseSizeBeforeUpdate = caseDefinitionRepository.findAll().size();

        // Update the caseDefinition using partial update
        CaseDefinition partialUpdatedCaseDefinition = new CaseDefinition();
        partialUpdatedCaseDefinition.setId(caseDefinition.getId());

        partialUpdatedCaseDefinition.name(UPDATED_NAME);

        restCaseDefinitionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCaseDefinition.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCaseDefinition))
            )
            .andExpect(status().isOk());

        // Validate the CaseDefinition in the database
        List<CaseDefinition> caseDefinitionList = caseDefinitionRepository.findAll();
        assertThat(caseDefinitionList).hasSize(databaseSizeBeforeUpdate);
        CaseDefinition testCaseDefinition = caseDefinitionList.get(caseDefinitionList.size() - 1);
        assertThat(testCaseDefinition.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCaseDefinition.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCaseDefinition.getActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    void fullUpdateCaseDefinitionWithPatch() throws Exception {
        // Initialize the database
        caseDefinitionRepository.saveAndFlush(caseDefinition);

        int databaseSizeBeforeUpdate = caseDefinitionRepository.findAll().size();

        // Update the caseDefinition using partial update
        CaseDefinition partialUpdatedCaseDefinition = new CaseDefinition();
        partialUpdatedCaseDefinition.setId(caseDefinition.getId());

        partialUpdatedCaseDefinition.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).active(UPDATED_ACTIVE);

        restCaseDefinitionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCaseDefinition.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCaseDefinition))
            )
            .andExpect(status().isOk());

        // Validate the CaseDefinition in the database
        List<CaseDefinition> caseDefinitionList = caseDefinitionRepository.findAll();
        assertThat(caseDefinitionList).hasSize(databaseSizeBeforeUpdate);
        CaseDefinition testCaseDefinition = caseDefinitionList.get(caseDefinitionList.size() - 1);
        assertThat(testCaseDefinition.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCaseDefinition.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCaseDefinition.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void patchNonExistingCaseDefinition() throws Exception {
        int databaseSizeBeforeUpdate = caseDefinitionRepository.findAll().size();
        caseDefinition.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCaseDefinitionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, caseDefinition.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(caseDefinition))
            )
            .andExpect(status().isBadRequest());

        // Validate the CaseDefinition in the database
        List<CaseDefinition> caseDefinitionList = caseDefinitionRepository.findAll();
        assertThat(caseDefinitionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCaseDefinition() throws Exception {
        int databaseSizeBeforeUpdate = caseDefinitionRepository.findAll().size();
        caseDefinition.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCaseDefinitionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(caseDefinition))
            )
            .andExpect(status().isBadRequest());

        // Validate the CaseDefinition in the database
        List<CaseDefinition> caseDefinitionList = caseDefinitionRepository.findAll();
        assertThat(caseDefinitionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCaseDefinition() throws Exception {
        int databaseSizeBeforeUpdate = caseDefinitionRepository.findAll().size();
        caseDefinition.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCaseDefinitionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(caseDefinition))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CaseDefinition in the database
        List<CaseDefinition> caseDefinitionList = caseDefinitionRepository.findAll();
        assertThat(caseDefinitionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCaseDefinition() throws Exception {
        // Initialize the database
        caseDefinitionRepository.saveAndFlush(caseDefinition);

        int databaseSizeBeforeDelete = caseDefinitionRepository.findAll().size();

        // Delete the caseDefinition
        restCaseDefinitionMockMvc
            .perform(delete(ENTITY_API_URL_ID, caseDefinition.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CaseDefinition> caseDefinitionList = caseDefinitionRepository.findAll();
        assertThat(caseDefinitionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

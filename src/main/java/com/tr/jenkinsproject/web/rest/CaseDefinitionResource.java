package com.tr.jenkinsproject.web.rest;

import com.tr.jenkinsproject.domain.CaseDefinition;
import com.tr.jenkinsproject.repository.CaseDefinitionRepository;
import com.tr.jenkinsproject.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.tr.jenkinsproject.domain.CaseDefinition}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CaseDefinitionResource {

    private final Logger log = LoggerFactory.getLogger(CaseDefinitionResource.class);

    private static final String ENTITY_NAME = "caseDefinition";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CaseDefinitionRepository caseDefinitionRepository;

    public CaseDefinitionResource(CaseDefinitionRepository caseDefinitionRepository) {
        this.caseDefinitionRepository = caseDefinitionRepository;
    }

    /**
     * {@code POST  /case-definitions} : Create a new caseDefinition.
     *
     * @param caseDefinition the caseDefinition to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new caseDefinition, or with status {@code 400 (Bad Request)} if the caseDefinition has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/case-definitions")
    public ResponseEntity<CaseDefinition> createCaseDefinition(@RequestBody CaseDefinition caseDefinition) throws URISyntaxException {
        log.debug("REST request to save CaseDefinition : {}", caseDefinition);
        if (caseDefinition.getId() != null) {
            throw new BadRequestAlertException("A new caseDefinition cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CaseDefinition result = caseDefinitionRepository.save(caseDefinition);
        return ResponseEntity
            .created(new URI("/api/case-definitions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /case-definitions/:id} : Updates an existing caseDefinition.
     *
     * @param id the id of the caseDefinition to save.
     * @param caseDefinition the caseDefinition to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated caseDefinition,
     * or with status {@code 400 (Bad Request)} if the caseDefinition is not valid,
     * or with status {@code 500 (Internal Server Error)} if the caseDefinition couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/case-definitions/{id}")
    public ResponseEntity<CaseDefinition> updateCaseDefinition(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CaseDefinition caseDefinition
    ) throws URISyntaxException {
        log.debug("REST request to update CaseDefinition : {}, {}", id, caseDefinition);
        if (caseDefinition.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, caseDefinition.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!caseDefinitionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CaseDefinition result = caseDefinitionRepository.save(caseDefinition);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, caseDefinition.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /case-definitions/:id} : Partial updates given fields of an existing caseDefinition, field will ignore if it is null
     *
     * @param id the id of the caseDefinition to save.
     * @param caseDefinition the caseDefinition to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated caseDefinition,
     * or with status {@code 400 (Bad Request)} if the caseDefinition is not valid,
     * or with status {@code 404 (Not Found)} if the caseDefinition is not found,
     * or with status {@code 500 (Internal Server Error)} if the caseDefinition couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/case-definitions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CaseDefinition> partialUpdateCaseDefinition(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CaseDefinition caseDefinition
    ) throws URISyntaxException {
        log.debug("REST request to partial update CaseDefinition partially : {}, {}", id, caseDefinition);
        if (caseDefinition.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, caseDefinition.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!caseDefinitionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CaseDefinition> result = caseDefinitionRepository
            .findById(caseDefinition.getId())
            .map(existingCaseDefinition -> {
                if (caseDefinition.getName() != null) {
                    existingCaseDefinition.setName(caseDefinition.getName());
                }
                if (caseDefinition.getDescription() != null) {
                    existingCaseDefinition.setDescription(caseDefinition.getDescription());
                }
                if (caseDefinition.getActive() != null) {
                    existingCaseDefinition.setActive(caseDefinition.getActive());
                }

                return existingCaseDefinition;
            })
            .map(caseDefinitionRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, caseDefinition.getId().toString())
        );
    }

    /**
     * {@code GET  /case-definitions} : get all the caseDefinitions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of caseDefinitions in body.
     */
    @GetMapping("/case-definitions")
    public ResponseEntity<List<CaseDefinition>> getAllCaseDefinitions(Pageable pageable) {
        log.debug("REST request to get a page of CaseDefinitions");
        Page<CaseDefinition> page = caseDefinitionRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /case-definitions/:id} : get the "id" caseDefinition.
     *
     * @param id the id of the caseDefinition to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the caseDefinition, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/case-definitions/{id}")
    public ResponseEntity<CaseDefinition> getCaseDefinition(@PathVariable Long id) {
        log.debug("REST request to get CaseDefinition : {}", id);
        Optional<CaseDefinition> caseDefinition = caseDefinitionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(caseDefinition);
    }

    /**
     * {@code DELETE  /case-definitions/:id} : delete the "id" caseDefinition.
     *
     * @param id the id of the caseDefinition to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/case-definitions/{id}")
    public ResponseEntity<Void> deleteCaseDefinition(@PathVariable Long id) {
        log.debug("REST request to delete CaseDefinition : {}", id);
        caseDefinitionRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}

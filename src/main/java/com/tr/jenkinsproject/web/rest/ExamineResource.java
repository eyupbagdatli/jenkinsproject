package com.tr.jenkinsproject.web.rest;

import com.tr.jenkinsproject.domain.Examine;
import com.tr.jenkinsproject.repository.ExamineRepository;
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
 * REST controller for managing {@link com.tr.jenkinsproject.domain.Examine}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ExamineResource {

    private final Logger log = LoggerFactory.getLogger(ExamineResource.class);

    private static final String ENTITY_NAME = "examine";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExamineRepository examineRepository;

    public ExamineResource(ExamineRepository examineRepository) {
        this.examineRepository = examineRepository;
    }

    /**
     * {@code POST  /examines} : Create a new examine.
     *
     * @param examine the examine to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new examine, or with status {@code 400 (Bad Request)} if the examine has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/examines")
    public ResponseEntity<Examine> createExamine(@RequestBody Examine examine) throws URISyntaxException {
        log.debug("REST request to save Examine : {}", examine);
        if (examine.getId() != null) {
            throw new BadRequestAlertException("A new examine cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Examine result = examineRepository.save(examine);
        return ResponseEntity
            .created(new URI("/api/examines/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /examines/:id} : Updates an existing examine.
     *
     * @param id the id of the examine to save.
     * @param examine the examine to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated examine,
     * or with status {@code 400 (Bad Request)} if the examine is not valid,
     * or with status {@code 500 (Internal Server Error)} if the examine couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/examines/{id}")
    public ResponseEntity<Examine> updateExamine(@PathVariable(value = "id", required = false) final Long id, @RequestBody Examine examine)
        throws URISyntaxException {
        log.debug("REST request to update Examine : {}, {}", id, examine);
        if (examine.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, examine.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!examineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Examine result = examineRepository.save(examine);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, examine.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /examines/:id} : Partial updates given fields of an existing examine, field will ignore if it is null
     *
     * @param id the id of the examine to save.
     * @param examine the examine to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated examine,
     * or with status {@code 400 (Bad Request)} if the examine is not valid,
     * or with status {@code 404 (Not Found)} if the examine is not found,
     * or with status {@code 500 (Internal Server Error)} if the examine couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/examines/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Examine> partialUpdateExamine(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Examine examine
    ) throws URISyntaxException {
        log.debug("REST request to partial update Examine partially : {}, {}", id, examine);
        if (examine.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, examine.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!examineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Examine> result = examineRepository
            .findById(examine.getId())
            .map(existingExamine -> {
                if (examine.getName() != null) {
                    existingExamine.setName(examine.getName());
                }

                return existingExamine;
            })
            .map(examineRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, examine.getId().toString())
        );
    }

    /**
     * {@code GET  /examines} : get all the examines.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of examines in body.
     */
    @GetMapping("/examines")
    public ResponseEntity<List<Examine>> getAllExamines(Pageable pageable) {
        log.debug("REST request to get a page of Examines");
        Page<Examine> page = examineRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /examines/:id} : get the "id" examine.
     *
     * @param id the id of the examine to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the examine, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/examines/{id}")
    public ResponseEntity<Examine> getExamine(@PathVariable Long id) {
        log.debug("REST request to get Examine : {}", id);
        Optional<Examine> examine = examineRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(examine);
    }

    /**
     * {@code DELETE  /examines/:id} : delete the "id" examine.
     *
     * @param id the id of the examine to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/examines/{id}")
    public ResponseEntity<Void> deleteExamine(@PathVariable Long id) {
        log.debug("REST request to delete Examine : {}", id);
        examineRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}

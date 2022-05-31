package com.tr.jenkinsproject.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * A Examine.
 */
@Entity
@Table(name = "examine")
public class Examine implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    private CaseDefinition fk_examine__case_definition_id;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Examine id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Examine name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CaseDefinition getFk_examine__case_definition_id() {
        return this.fk_examine__case_definition_id;
    }

    public void setFk_examine__case_definition_id(CaseDefinition caseDefinition) {
        this.fk_examine__case_definition_id = caseDefinition;
    }

    public Examine fk_examine__case_definition_id(CaseDefinition caseDefinition) {
        this.setFk_examine__case_definition_id(caseDefinition);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Examine)) {
            return false;
        }
        return id != null && id.equals(((Examine) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Examine{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}

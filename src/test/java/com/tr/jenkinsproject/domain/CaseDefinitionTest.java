package com.tr.jenkinsproject.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.tr.jenkinsproject.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CaseDefinitionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CaseDefinition.class);
        CaseDefinition caseDefinition1 = new CaseDefinition();
        caseDefinition1.setId(1L);
        CaseDefinition caseDefinition2 = new CaseDefinition();
        caseDefinition2.setId(caseDefinition1.getId());
        assertThat(caseDefinition1).isEqualTo(caseDefinition2);
        caseDefinition2.setId(2L);
        assertThat(caseDefinition1).isNotEqualTo(caseDefinition2);
        caseDefinition1.setId(null);
        assertThat(caseDefinition1).isNotEqualTo(caseDefinition2);
    }
}

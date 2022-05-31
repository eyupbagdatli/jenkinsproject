package com.tr.jenkinsproject.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.tr.jenkinsproject.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExamineTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Examine.class);
        Examine examine1 = new Examine();
        examine1.setId(1L);
        Examine examine2 = new Examine();
        examine2.setId(examine1.getId());
        assertThat(examine1).isEqualTo(examine2);
        examine2.setId(2L);
        assertThat(examine1).isNotEqualTo(examine2);
        examine1.setId(null);
        assertThat(examine1).isNotEqualTo(examine2);
    }
}

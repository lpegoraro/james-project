package org.apache.james.jpa.healthcheck;

import javax.persistence.EntityManagerFactory;

import org.apache.james.backends.jpa.JpaTestCluster;
import org.apache.james.core.healthcheck.Result;
import org.apache.james.core.healthcheck.ResultStatus;
import org.apache.james.mailrepository.jpa.JPAUrl;
import org.apache.openjpa.persistence.EntityManagerFactoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(JPAHealthCheckExtension.class)
class JPAHealthCheckTest {

    private JPAHealthCheck jpaHealthCheck;

    @Test
    void testWhenActive() {
        final JpaTestCluster JPA_TEST_CLUSTER = JpaTestCluster.create(JPAUrl.class);
        jpaHealthCheck = new JPAHealthCheck(JPA_TEST_CLUSTER.getEntityManagerFactory());
        Result result = jpaHealthCheck.check();
        ResultStatus healthy = ResultStatus.HEALTHY;
        assertThat(result.getStatus()).as("Result %s status should be %s", result.getStatus(), healthy)
                .isEqualTo(healthy);
    }

    @Test
    void testWhenInactive() {
        jpaHealthCheck = new JPAHealthCheck((EntityManagerFactory)
                new EntityManagerFactoryImpl().getBrokerFactory());
        Result result = jpaHealthCheck.check();
        ResultStatus unhealthy = ResultStatus.UNHEALTHY;
        assertThat(result.getStatus()).as("Result %s status should be %s", result.getStatus(), unhealthy)
                .isEqualTo(unhealthy);
    }
}
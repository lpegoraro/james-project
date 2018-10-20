package org.apache.james.jpa.healthcheck;

import org.apache.james.backends.jpa.JpaTestCluster;
import org.apache.james.core.healthcheck.Result;
import org.apache.james.mailrepository.jpa.JPAUrl;
import org.apache.openjpa.persistence.EntityManagerFactoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.persistence.EntityManagerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(JPAHealthCheckExtension.class)
class JPAHealthCheckTest {

    private JPAHealthCheck jpaHealthCheck;

    @Test
    void testWhenActive() {
        final JpaTestCluster JPA_TEST_CLUSTER = JpaTestCluster.create(JPAUrl.class);
        jpaHealthCheck = new JPAHealthCheck(JPA_TEST_CLUSTER.getEntityManagerFactory());
        assertEquals(Result.healthy(jpaHealthCheck.componentName()),
                jpaHealthCheck.check(), "Result should be healthy");
    }

    @Test
    void testWhenInactive() {
        jpaHealthCheck = new JPAHealthCheck((EntityManagerFactory)
                new EntityManagerFactoryImpl().getBrokerFactory());
        assertEquals(Result.unhealthy(jpaHealthCheck.componentName()),
                jpaHealthCheck.check(), "Result should be unhealthy");
    }
}
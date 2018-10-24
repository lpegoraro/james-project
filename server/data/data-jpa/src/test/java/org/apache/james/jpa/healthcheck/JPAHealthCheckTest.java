package org.apache.james.jpa.healthcheck;

import org.apache.james.backends.jpa.JpaTestCluster;
import org.apache.james.core.healthcheck.Result;
import org.apache.james.mailrepository.jpa.JPAUrl;
import org.apache.openjpa.persistence.EntityManagerFactoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.persistence.EntityManagerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@ExtendWith(JPAHealthCheckExtension.class)
class JPAHealthCheckTest {

    private JPAHealthCheck jpaHealthCheck;

    @Test
    void testWhenActive() {
        final JpaTestCluster JPA_TEST_CLUSTER = JpaTestCluster.create(JPAUrl.class);
        jpaHealthCheck = new JPAHealthCheck(JPA_TEST_CLUSTER.getEntityManagerFactory());
        assertThat("Health check should be healthy", jpaHealthCheck.check(), is(equalTo(Result.healthy(jpaHealthCheck.componentName()))));
    }

    @Test
    void testWhenInactive() {
        jpaHealthCheck = new JPAHealthCheck((EntityManagerFactory)
                new EntityManagerFactoryImpl().getBrokerFactory());
        assertThat("Health check should be healthy", jpaHealthCheck.check(), is(equalTo(Result.unhealthy(jpaHealthCheck.componentName()))));
    }
}
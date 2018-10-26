package org.apache.james.jpa.healthcheck;

import static org.apache.james.core.healthcheck.Result.healthy;
import static org.apache.james.core.healthcheck.Result.unhealthy;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

import org.apache.james.core.healthcheck.ComponentName;
import org.apache.james.core.healthcheck.HealthCheck;
import org.apache.james.core.healthcheck.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JPAHealthCheck implements HealthCheck {

    private static final Logger LOGGER = LoggerFactory.getLogger(JPAHealthCheck.class);
    private final EntityManagerFactory entityManagerFactory;

    @Inject
    public JPAHealthCheck(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public ComponentName componentName() {
        return new ComponentName("JPA Backend");
    }

    @Override
    public Result check() {
        LOGGER.debug("Checking if EntityManager is created successfully");
        try {
            if (entityManagerFactory.createEntityManager().isOpen()) {
                LOGGER.debug("EntityManager can execute queries, the connection is healthy");
                return healthy(componentName());
            }
        } catch (IllegalStateException stateException) {
            LOGGER.debug("EntityManagerFactory or EntityManager thrown an IllegalStateException, the connection is unhealthy");
            return unhealthy(componentName());
        }
        return unhealthy(componentName());
    }
}

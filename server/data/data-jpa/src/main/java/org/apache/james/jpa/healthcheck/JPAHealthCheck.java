package org.apache.james.jpa.healthcheck;

import com.github.fge.lambdas.Throwing;
import org.apache.james.backends.jpa.TransactionRunner;
import org.apache.james.core.healthcheck.ComponentName;
import org.apache.james.core.healthcheck.HealthCheck;
import org.apache.james.core.healthcheck.Result;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.concurrent.TimeoutException;


public class JPAHealthCheck implements HealthCheck {

    private final TransactionRunner transactionRunner;

    @Inject
    public JPAHealthCheck(EntityManagerFactory entityManagerFactory) {
        this.transactionRunner = new TransactionRunner(entityManagerFactory);
    }

    @Override
    public ComponentName componentName() {
        return new ComponentName("JPA Backend");
    }

    @Override
    public Result check() {
        try {
            transactionRunner.run(
                    Throwing.<EntityManager>consumer(entityManager -> {
                        entityManager.createNativeQuery("SELECT CURRENT_TIME()");
                    }).sneakyThrow());
        } catch (TimeoutException ex) {
            return Result.unhealthy(componentName());
        }
        return Result.healthy(componentName());
    }

    ª

}

package org.apache.james.jpa.healthcheck;

import org.apache.james.backends.jpa.JpaTestCluster;
import org.apache.james.mailrepository.api.MailRepositoryUrlStore;
import org.apache.james.mailrepository.jpa.JPAMailRepositoryUrlStore;
import org.apache.james.mailrepository.jpa.JPAUrl;
import org.junit.jupiter.api.extension.*;

public class JPAHealthCheckExtension implements ParameterResolver, AfterEachCallback {
    private static final JpaTestCluster JPA_TEST_CLUSTER = JpaTestCluster.create(JPAUrl.class);

    @Override
    public void afterEach(ExtensionContext context) {
        JPA_TEST_CLUSTER.clear("JAMES_MAIL_REPOS");
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return (parameterContext.getParameter().getType() == MailRepositoryUrlStore.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return new JPAMailRepositoryUrlStore(JPA_TEST_CLUSTER.getEntityManagerFactory());
    }
}

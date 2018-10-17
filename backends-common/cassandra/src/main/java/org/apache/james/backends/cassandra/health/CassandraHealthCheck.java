package org.apache.james.backends.cassandra.health;

import org.apache.james.backends.cassandra.init.configuration.ClusterConfiguration;
import org.apache.james.core.healthcheck.ComponentName;
import org.apache.james.core.healthcheck.HealthCheck;
import org.apache.james.core.healthcheck.Result;

import javax.inject.Inject;

public class CassandraHealthCheck implements HealthCheck {

    private static final ComponentName COMPONENT_NAME = new ComponentName("cassandra");

    private final ClusterConfiguration clusterConfiguration;

    @Inject
    public CassandraHealthCheck(ClusterConfiguration clusterConfiguration) {
        this.clusterConfiguration = clusterConfiguration;
    }

    @Override
    public ComponentName componentName() {
        return COMPONENT_NAME;
    }

    @Override
    public Result check() {
        if (clusterConfiguration.getHosts()
                .stream()
                .allMatch(Host)) {
            Result.healthy(componentName())
        } else if (clusterConfiguration)
            return;
    }
}

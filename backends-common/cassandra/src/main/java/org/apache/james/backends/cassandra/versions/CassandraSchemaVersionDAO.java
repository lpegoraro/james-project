/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.james.backends.cassandra.versions;

import static com.datastax.driver.core.querybuilder.QueryBuilder.bindMarker;
import static com.datastax.driver.core.querybuilder.QueryBuilder.insertInto;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static org.apache.james.backends.cassandra.versions.table.CassandraSchemaVersionTable.KEY;
import static org.apache.james.backends.cassandra.versions.table.CassandraSchemaVersionTable.TABLE_NAME;
import static org.apache.james.backends.cassandra.versions.table.CassandraSchemaVersionTable.VALUE;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.apache.james.backends.cassandra.utils.CassandraAsyncExecutor;
import org.apache.james.backends.cassandra.utils.CassandraUtils;
import org.apache.james.backends.cassandra.versions.table.CassandraSchemaVersionTable;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;

public class CassandraSchemaVersionDAO {
    private final PreparedStatement readVersionStatement;
    private final PreparedStatement writeVersionStatement;
    private CassandraUtils cassandraUtils;
    private final CassandraAsyncExecutor cassandraAsyncExecutor;

    @Inject
    public CassandraSchemaVersionDAO(Session session, CassandraUtils cassandraUtils) {
        cassandraAsyncExecutor = new CassandraAsyncExecutor(session);
        readVersionStatement = prepareReadVersionStatement(session);
        writeVersionStatement = prepareWriteVersionStatement(session);
        this.cassandraUtils = cassandraUtils;
    }

    private PreparedStatement prepareReadVersionStatement(Session session) {
        return session.prepare(
            select(VALUE)
                .from(TABLE_NAME));
    }

    private PreparedStatement prepareWriteVersionStatement(Session session) {
        return session.prepare(
            insertInto(CassandraSchemaVersionTable.TABLE_NAME)
                .value(KEY, bindMarker(KEY))
                .value(VALUE, bindMarker(VALUE)));
    }

    public CompletableFuture<Optional<Integer>> getCurrentSchemaVersion() {
        return cassandraAsyncExecutor.execute(readVersionStatement.bind())
            .thenApply(resultSet -> cassandraUtils.convertToStream(resultSet)
                .map(row -> row.getInt(VALUE))
                .reduce(Math::max));
    }

    public CompletableFuture<Void> updateVersion(int newVersion) {
        return cassandraAsyncExecutor.executeVoid(
            writeVersionStatement.bind()
                .setUUID(KEY, UUIDs.timeBased())
                .setInt(VALUE, newVersion));
    }
}

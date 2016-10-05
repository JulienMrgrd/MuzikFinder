/*
 * Copyright (c) 2008-2014 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sources.mongo.com.mongodb.binding;

import static sources.mongo.com.mongodb.assertions.Assertions.notNull;

import sources.mongo.com.mongodb.ReadPreference;
import sources.mongo.com.mongodb.connection.Cluster;
import sources.mongo.com.mongodb.connection.Connection;
import sources.mongo.com.mongodb.connection.Server;
import sources.mongo.com.mongodb.connection.ServerDescription;
import sources.mongo.com.mongodb.selector.PrimaryServerSelector;
import sources.mongo.com.mongodb.selector.ReadPreferenceServerSelector;
import sources.mongo.com.mongodb.selector.ServerSelector;

/**
 * A simple ReadWriteBinding implementation that supplies write connection sources bound to a possibly different primary each time, and a
 * read connection source bound to a possible different server each time.
 *
 * @since 3.0
 */
public class ClusterBinding extends AbstractReferenceCounted implements ReadWriteBinding {
    private final Cluster cluster;
    private final ReadPreference readPreference;

    /**
     * Creates an instance.
     * @param cluster        a non-null Cluster which will be used to select a server to bind to
     * @param readPreference a non-null ReadPreference for read operations
     */
    public ClusterBinding(final Cluster cluster, final ReadPreference readPreference) {
        this.cluster = notNull("cluster", cluster);
        this.readPreference = notNull("readPreference", readPreference);
    }

    @Override
    public ReadWriteBinding retain() {
        super.retain();
        return this;
    }

    @Override
    public ReadPreference getReadPreference() {
        return readPreference;
    }

    @Override
    public ConnectionSource getReadConnectionSource() {
        return new ClusterBindingConnectionSource(new ReadPreferenceServerSelector(readPreference));
    }

    @Override
    public ConnectionSource getWriteConnectionSource() {
        return new ClusterBindingConnectionSource(new PrimaryServerSelector());
    }

    private final class ClusterBindingConnectionSource extends AbstractReferenceCounted implements ConnectionSource {
        private final Server server;

        private ClusterBindingConnectionSource(final ServerSelector serverSelector) {
            this.server = cluster.selectServer(serverSelector);
            ClusterBinding.this.retain();
        }

        @Override
        public ServerDescription getServerDescription() {
            return server.getDescription();
        }

        @Override
        public Connection getConnection() {
            return server.getConnection();
        }

        public ConnectionSource retain() {
            super.retain();
            ClusterBinding.this.retain();
            return this;
        }

        @Override
        public void release() {
            super.release();
            ClusterBinding.this.release();
        }
    }
}

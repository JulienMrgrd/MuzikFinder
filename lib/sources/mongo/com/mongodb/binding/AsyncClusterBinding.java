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
import sources.mongo.com.mongodb.async.SingleResultCallback;
import sources.mongo.com.mongodb.connection.AsyncConnection;
import sources.mongo.com.mongodb.connection.Cluster;
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
public class AsyncClusterBinding extends AbstractReferenceCounted implements AsyncReadWriteBinding {
    private final Cluster cluster;
    private final ReadPreference readPreference;

    /**
     * Creates an instance.
     *
     * @param cluster        a non-null Cluster which will be used to select a server to bind to
     * @param readPreference a non-null ReadPreference for read operations
     */
    public AsyncClusterBinding(final Cluster cluster, final ReadPreference readPreference) {
        this.cluster = notNull("cluster", cluster);
        this.readPreference = notNull("readPreference", readPreference);
    }

    @Override
    public AsyncReadWriteBinding retain() {
        super.retain();
        return this;
    }

    @Override
    public ReadPreference getReadPreference() {
        return readPreference;
    }

    @Override
    public void getReadConnectionSource(final SingleResultCallback<AsyncConnectionSource> callback) {
        getAsyncClusterBindingConnectionSource(new ReadPreferenceServerSelector(readPreference), callback);
    }

    @Override
    public void getWriteConnectionSource(final SingleResultCallback<AsyncConnectionSource> callback) {
        getAsyncClusterBindingConnectionSource(new PrimaryServerSelector(), callback);
    }

    private void getAsyncClusterBindingConnectionSource(final ServerSelector serverSelector,
                                                        final SingleResultCallback<AsyncConnectionSource> callback) {
        cluster.selectServerAsync(serverSelector, new SingleResultCallback<Server>() {
            @Override
            public void onResult(final Server result, final Throwable t) {
                if (t != null) {
                    callback.onResult(null, t);
                } else {
                    callback.onResult(new AsyncClusterBindingConnectionSource(result), null);
                }
            }
        });
    }

    private final class AsyncClusterBindingConnectionSource extends AbstractReferenceCounted implements AsyncConnectionSource {
        private final Server server;

        private AsyncClusterBindingConnectionSource(final Server server) {
            this.server = server;
            AsyncClusterBinding.this.retain();
        }

        @Override
        public ServerDescription getServerDescription() {
            return server.getDescription();
        }

        @Override
        public void getConnection(final SingleResultCallback<AsyncConnection> callback) {
            server.getConnectionAsync(callback);
        }

        public AsyncConnectionSource retain() {
            super.retain();
            AsyncClusterBinding.this.retain();
            return this;
        }

        @Override
        public void release() {
            super.release();
            AsyncClusterBinding.this.release();
        }
    }
}

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

package sources.mongo.com.mongodb.connection;

import static sources.mongo.com.mongodb.connection.ProtocolHelper.getQueryFailureException;

import sources.mongo.com.mongodb.MongoNamespace;
import sources.mongo.com.mongodb.ServerAddress;
import sources.mongo.com.mongodb.async.SingleResultCallback;
import sources.mongo.com.mongodb.diagnostics.logging.Logger;
import sources.mongo.com.mongodb.diagnostics.logging.Loggers;
import sources.mongo.org.bson.BsonDocument;
import sources.mongo.org.bson.codecs.BsonDocumentCodec;
import sources.mongo.org.bson.codecs.Decoder;

import static java.lang.String.format;

class QueryResultCallback<T> extends ResponseCallback {
    public static final Logger LOGGER = Loggers.getLogger("protocol.query");

    private final MongoNamespace namespace;
    private final SingleResultCallback<QueryResult<T>> callback;
    private final Decoder<T> decoder;

    public QueryResultCallback(final MongoNamespace namespace, final SingleResultCallback<QueryResult<T>> callback,
                               final Decoder<T> decoder,
                               final int requestId, final ServerAddress serverAddress) {
        super(requestId, serverAddress);
        this.namespace = namespace;
        this.callback = callback;
        this.decoder = decoder;
    }

    @Override
    protected void callCallback(final ResponseBuffers responseBuffers, final Throwable t) {
        try {
            if (t != null) {
                callback.onResult(null, t);
            } else if (responseBuffers.getReplyHeader().isQueryFailure()) {
                BsonDocument errorDocument = new ReplyMessage<BsonDocument>(responseBuffers, new BsonDocumentCodec(),
                                                                            getRequestId()).getDocuments().get(0);
                callback.onResult(null, getQueryFailureException(errorDocument, getServerAddress()));
            } else {
                QueryResult<T> result = new QueryResult<T>(namespace, new ReplyMessage<T>(responseBuffers, decoder, getRequestId()),
                                                           getServerAddress());
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(format("Query results received %s documents with cursor %s",
                                        result.getResults().size(),
                                        result.getCursor()));
                }
                callback.onResult(result, null);
            }
        } catch (Throwable t1) {
            callback.onResult(null, t1);
        } finally {
            try {
                if (responseBuffers != null) {
                    responseBuffers.close();
                }
            } catch (Throwable t1) {
                LOGGER.debug("GetMore ResponseBuffer close exception", t1);
            }
        }
    }
}

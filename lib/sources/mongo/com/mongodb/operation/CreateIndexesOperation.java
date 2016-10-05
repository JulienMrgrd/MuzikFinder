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

package sources.mongo.com.mongodb.operation;

import sources.mongo.com.mongodb.DuplicateKeyException;
import sources.mongo.com.mongodb.ErrorCategory;
import sources.mongo.com.mongodb.MongoCommandException;
import sources.mongo.com.mongodb.MongoException;
import sources.mongo.com.mongodb.MongoInternalException;
import sources.mongo.com.mongodb.MongoNamespace;
import sources.mongo.com.mongodb.WriteConcern;
import sources.mongo.com.mongodb.WriteConcernResult;
import sources.mongo.com.mongodb.async.SingleResultCallback;
import sources.mongo.com.mongodb.binding.AsyncWriteBinding;
import sources.mongo.com.mongodb.binding.WriteBinding;
import sources.mongo.com.mongodb.bulk.IndexRequest;
import sources.mongo.com.mongodb.bulk.InsertRequest;
import sources.mongo.com.mongodb.connection.AsyncConnection;
import sources.mongo.com.mongodb.connection.Connection;
import sources.mongo.org.bson.BsonArray;
import sources.mongo.org.bson.BsonBoolean;
import sources.mongo.org.bson.BsonDocument;
import sources.mongo.org.bson.BsonDouble;
import sources.mongo.org.bson.BsonInt32;
import sources.mongo.org.bson.BsonInt64;
import sources.mongo.org.bson.BsonString;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;
import static sources.mongo.com.mongodb.assertions.Assertions.notNull;
import static sources.mongo.com.mongodb.internal.async.ErrorHandlingResultCallback.errorHandlingCallback;
import static sources.mongo.com.mongodb.operation.CommandOperationHelper.executeWrappedCommandProtocol;
import static sources.mongo.com.mongodb.operation.CommandOperationHelper.executeWrappedCommandProtocolAsync;
import static sources.mongo.com.mongodb.operation.IndexHelper.generateIndexName;
import static sources.mongo.com.mongodb.operation.OperationHelper.AsyncCallableWithConnection;
import static sources.mongo.com.mongodb.operation.OperationHelper.CallableWithConnection;
import static sources.mongo.com.mongodb.operation.OperationHelper.releasingCallback;
import static sources.mongo.com.mongodb.operation.OperationHelper.serverIsAtLeastVersionTwoDotSix;
import static sources.mongo.com.mongodb.operation.OperationHelper.withConnection;

/**
 * An operation that creates one or more indexes.
 *
 * <p>Multiple index creation is supported starting with MongoDB server version 2.6</p>
 *
 * @mongodb.driver.manual reference/command/createIndexes/ Create indexes
 * @since 3.0
 */
public class CreateIndexesOperation implements AsyncWriteOperation<Void>, WriteOperation<Void> {
    private final MongoNamespace namespace;
    private final List<IndexRequest> requests;
    private final MongoNamespace systemIndexes;

    /**
     * Construct a new instance.
     *
     * @param namespace     the database and collection namespace for the operation.
     * @param requests the index request
     */
    public CreateIndexesOperation(final MongoNamespace namespace, final List<IndexRequest> requests) {
        this.namespace = notNull("namespace", namespace);
        this.systemIndexes = new MongoNamespace(namespace.getDatabaseName(), "system.indexes");
        this.requests = notNull("indexRequests", requests);
    }

    /**
     * Gets the index requests.
     *
     * @return the index requests
     */
    public List<IndexRequest> getRequests() {
        return requests;
    }

    /**
     * Gets the index names.
     *
     * @return a list of index names
     */
    public List<String> getIndexNames() {
        List<String> indexNames = new ArrayList<String>(requests.size());
        for (IndexRequest request : requests) {
            if (request.getName() != null) {
                indexNames.add(request.getName());
            } else {
                indexNames.add(IndexHelper.generateIndexName(request.getKeys()));
            }
        }
        return indexNames;
    }

    @Override
    public Void execute(final WriteBinding binding) {
        return withConnection(binding, new CallableWithConnection<Void>() {
            @Override
            public Void call(final Connection connection) {
                if (serverIsAtLeastVersionTwoDotSix(connection.getDescription())) {
                    try {
                        executeWrappedCommandProtocol(namespace.getDatabaseName(), getCommand(), connection);
                    } catch (MongoCommandException e) {
                        throw checkForDuplicateKeyError(e);
                    }
                } else {
                    if (requests.size() > 1) {
                        throw new MongoInternalException("Creation of multiple indexes simultaneously not supported until MongoDB 2.6");
                    }
                    connection.insert(systemIndexes, true, WriteConcern.ACKNOWLEDGED, asList(new InsertRequest(getIndex(requests.get(0)))));
                }
                return null;
            }
        });
    }

    @Override
    public void executeAsync(final AsyncWriteBinding binding, final SingleResultCallback<Void> callback) {
        withConnection(binding, new AsyncCallableWithConnection() {
            @Override
            public void call(final AsyncConnection connection, final Throwable t) {
                if (t != null) {
                    errorHandlingCallback(callback).onResult(null, t);
                } else {
                    final SingleResultCallback<Void> wrappedCallback = releasingCallback(errorHandlingCallback(callback), connection);
                    if (serverIsAtLeastVersionTwoDotSix(connection.getDescription())) {
                        executeWrappedCommandProtocolAsync(namespace.getDatabaseName(), getCommand(), connection,
                                                           new SingleResultCallback<BsonDocument>() {
                                                               @Override
                                                               public void onResult(final BsonDocument result, final Throwable t) {
                                                                   wrappedCallback.onResult(null, translateException(t));
                                                               }});
                    } else {
                        if (requests.size() > 1) {
                            wrappedCallback.onResult(null, new MongoInternalException("Creation of multiple indexes simultaneously not "
                                                                                      + "supported until MongoDB 2.6"));
                        }
                        else {
                            connection.insertAsync(systemIndexes, true, WriteConcern.ACKNOWLEDGED,
                                                   asList(new InsertRequest(getIndex(requests.get(0)))),
                                                   new SingleResultCallback<WriteConcernResult>() {
                                                       @Override
                                                       public void onResult(final WriteConcernResult result, final Throwable t) {
                                                           wrappedCallback.onResult(null, translateException(t));
                                                       }
                                                   });
                        }
                    }
                }
            }
        });
    }

    private BsonDocument getIndex(final IndexRequest request) {
        BsonDocument index = new BsonDocument();
        index.append("key", request.getKeys());
        index.append("name", new BsonString(request.getName() != null ? request.getName() : generateIndexName(request.getKeys())));
        index.append("ns", new BsonString(namespace.getFullName()));
        if (request.isBackground()) {
            index.append("background", BsonBoolean.TRUE);
        }
        if (request.isUnique()) {
            index.append("unique", BsonBoolean.TRUE);
        }
        if (request.isSparse()) {
            index.append("sparse", BsonBoolean.TRUE);
        }
        if (request.getExpireAfter(TimeUnit.SECONDS) != null) {
            index.append("expireAfterSeconds", new BsonInt64(request.getExpireAfter(TimeUnit.SECONDS)));
        }
        if (request.getVersion() != null) {
            index.append("v", new BsonInt32(request.getVersion()));
        }
        if (request.getWeights() != null) {
            index.append("weights", request.getWeights());
        }
        if (request.getDefaultLanguage() != null) {
            index.append("default_language", new BsonString(request.getDefaultLanguage()));
        }
        if (request.getLanguageOverride() != null) {
            index.append("language_override", new BsonString(request.getLanguageOverride()));
        }
        if (request.getTextVersion() != null) {
            index.append("textIndexVersion", new BsonInt32(request.getTextVersion()));
        }
        if (request.getSphereVersion() != null) {
            index.append("2dsphereIndexVersion", new BsonInt32(request.getSphereVersion()));
        }
        if (request.getBits() != null) {
            index.append("bits", new BsonInt32(request.getBits()));
        }
        if (request.getMin() != null) {
            index.append("min", new BsonDouble(request.getMin()));
        }
        if (request.getMax() != null) {
            index.append("max", new BsonDouble(request.getMax()));
        }
        if (request.getBucketSize() != null) {
            index.append("bucketSize", new BsonDouble(request.getBucketSize()));
        }
        if (request.getDropDups()) {
            index.append("dropDups", BsonBoolean.TRUE);
        }
        if (request.getStorageEngine() != null) {
            index.append("storageEngine", request.getStorageEngine());
        }
        return index;
    }

    private BsonDocument getCommand() {
        BsonDocument command = new BsonDocument("createIndexes", new BsonString(namespace.getCollectionName()));
        List<BsonDocument> values = new ArrayList<BsonDocument>();
        for (IndexRequest request : requests) {
            values.add(getIndex(request));
        }
        command.put("indexes", new BsonArray(values));
        return command;
    }

    private MongoException translateException(final Throwable t) {
        return (t instanceof MongoCommandException) ? checkForDuplicateKeyError((MongoCommandException) t)
                                                      : MongoException.fromThrowable(t);
    }

    @SuppressWarnings("deprecation")
    private MongoException checkForDuplicateKeyError(final MongoCommandException e) {
        if (ErrorCategory.fromErrorCode(e.getCode()) == ErrorCategory.DUPLICATE_KEY) {
            return new DuplicateKeyException(e.getResponse(), e.getServerAddress(), WriteConcernResult.acknowledged(0, false, null));
        } else {
            return e;
        }
    }
}

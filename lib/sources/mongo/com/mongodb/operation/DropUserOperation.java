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

import static sources.mongo.com.mongodb.assertions.Assertions.notNull;
import static sources.mongo.com.mongodb.internal.async.ErrorHandlingResultCallback.errorHandlingCallback;
import static sources.mongo.com.mongodb.operation.CommandOperationHelper.executeWrappedCommandProtocol;
import static sources.mongo.com.mongodb.operation.CommandOperationHelper.executeWrappedCommandProtocolAsync;
import static sources.mongo.com.mongodb.operation.OperationHelper.AsyncCallableWithConnection;
import static sources.mongo.com.mongodb.operation.OperationHelper.CallableWithConnection;
import static sources.mongo.com.mongodb.operation.OperationHelper.VoidTransformer;
import static sources.mongo.com.mongodb.operation.OperationHelper.releasingCallback;
import static sources.mongo.com.mongodb.operation.OperationHelper.serverIsAtLeastVersionTwoDotSix;
import static sources.mongo.com.mongodb.operation.OperationHelper.withConnection;

import sources.mongo.com.mongodb.MongoNamespace;
import sources.mongo.com.mongodb.WriteConcern;
import sources.mongo.com.mongodb.WriteConcernResult;
import sources.mongo.com.mongodb.async.SingleResultCallback;
import sources.mongo.com.mongodb.binding.AsyncWriteBinding;
import sources.mongo.com.mongodb.binding.WriteBinding;
import sources.mongo.com.mongodb.bulk.DeleteRequest;
import sources.mongo.com.mongodb.connection.AsyncConnection;
import sources.mongo.com.mongodb.connection.Connection;
import sources.mongo.org.bson.BsonDocument;
import sources.mongo.org.bson.BsonString;

import static java.util.Arrays.asList;

/**
 * An operation to remove a user.
 *
 * @since 3.0
 */
public class DropUserOperation implements AsyncWriteOperation<Void>, WriteOperation<Void> {
    private final String databaseName;
    private final String userName;

    /**
     * Construct a new instance.
     *
     * @param databaseName the name of the database for the operation.
     * @param userName     the name of the user to be dropped.
     */
    public DropUserOperation(final String databaseName, final String userName) {
        this.databaseName = notNull("databaseName", databaseName);
        this.userName = notNull("userName", userName);
    }

    @Override
    public Void execute(final WriteBinding binding) {
        return withConnection(binding, new CallableWithConnection<Void>() {
            @Override
            public Void call(final Connection connection) {
                if (serverIsAtLeastVersionTwoDotSix(connection.getDescription())) {
                    executeWrappedCommandProtocol(databaseName, getCommand(), connection);
                } else {
                    connection.delete(getNamespace(), true, WriteConcern.ACKNOWLEDGED, asList(getDeleteRequest()));
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
                        executeWrappedCommandProtocolAsync(databaseName, getCommand(), connection, new VoidTransformer<BsonDocument>(),
                                                           wrappedCallback);
                    } else {
                        connection.deleteAsync(getNamespace(), true, WriteConcern.ACKNOWLEDGED, asList(getDeleteRequest()),
                                               new SingleResultCallback<WriteConcernResult>() {
                                                   @Override
                                                   public void onResult(final WriteConcernResult result, final Throwable t) {
                                                       wrappedCallback.onResult(null, t);
                                                   }
                                               });
                    }
                }
            }
        });
    }

    private MongoNamespace getNamespace() {
        return new MongoNamespace(databaseName, "system.users");
    }

    private DeleteRequest getDeleteRequest() {
        return new DeleteRequest(new BsonDocument("user", new BsonString(userName)));
    }

    private BsonDocument getCommand() {
        return new BsonDocument("dropUser", new BsonString(userName));
    }
}

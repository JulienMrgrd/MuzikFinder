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
import static sources.mongo.com.mongodb.operation.CommandOperationHelper.executeWrappedCommandProtocol;
import static sources.mongo.com.mongodb.operation.CommandOperationHelper.executeWrappedCommandProtocolAsync;
import static sources.mongo.com.mongodb.operation.CommandOperationHelper.isNamespaceError;
import static sources.mongo.com.mongodb.operation.OperationHelper.VoidTransformer;

import sources.mongo.com.mongodb.MongoCommandException;
import sources.mongo.com.mongodb.MongoNamespace;
import sources.mongo.com.mongodb.async.SingleResultCallback;
import sources.mongo.com.mongodb.binding.AsyncWriteBinding;
import sources.mongo.com.mongodb.binding.WriteBinding;
import sources.mongo.org.bson.BsonDocument;
import sources.mongo.org.bson.BsonString;

/**
 * Operation to drop a Collection in MongoDB.  The {@code execute} method throws MongoCommandFailureException if something goes wrong, but
 * it will not throw an Exception if the collection does not exist before trying to drop it.
 *
 * @since 3.0
 */
public class DropCollectionOperation implements AsyncWriteOperation<Void>, WriteOperation<Void> {
    private final MongoNamespace namespace;

    /**
     * Construct a new instance.
     *
     * @param namespace the database and collection namespace for the operation.
     */
    public DropCollectionOperation(final MongoNamespace namespace) {
        this.namespace = notNull("namespace", namespace);
    }

    @Override
    public Void execute(final WriteBinding binding) {
        try {
            executeWrappedCommandProtocol(namespace.getDatabaseName(), getCommand(), binding);
        } catch (MongoCommandException e) {
            CommandOperationHelper.rethrowIfNotNamespaceError(e);
        }
        return null;
    }

    @Override
    public void executeAsync(final AsyncWriteBinding binding, final SingleResultCallback<Void> callback) {
        executeWrappedCommandProtocolAsync(namespace.getDatabaseName(), getCommand(), binding, new VoidTransformer<BsonDocument>(),
                                           new SingleResultCallback<Void>() {
                                               @Override
                                               public void onResult(final Void result, final Throwable t) {
                                                   if (t != null && !isNamespaceError(t)) {
                                                       callback.onResult(null, t);
                                                   } else {
                                                       callback.onResult(result, null);
                                                   }
                                               }
                                           });
    }

    private BsonDocument getCommand() {
        return new BsonDocument("drop", new BsonString(namespace.getCollectionName()));
    }

}

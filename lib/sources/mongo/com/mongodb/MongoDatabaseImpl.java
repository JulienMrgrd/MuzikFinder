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

package sources.mongo.com.mongodb;

import static sources.mongo.com.mongodb.MongoClient.getDefaultCodecRegistry;
import static sources.mongo.com.mongodb.assertions.Assertions.notNull;

import sources.mongo.com.mongodb.client.ListCollectionsIterable;
import sources.mongo.com.mongodb.client.MongoCollection;
import sources.mongo.com.mongodb.client.MongoDatabase;
import sources.mongo.com.mongodb.client.MongoIterable;
import sources.mongo.com.mongodb.client.model.CreateCollectionOptions;
import sources.mongo.com.mongodb.operation.CommandReadOperation;
import sources.mongo.com.mongodb.operation.CreateCollectionOperation;
import sources.mongo.com.mongodb.operation.DropDatabaseOperation;
import sources.mongo.com.mongodb.operation.OperationExecutor;
import sources.mongo.org.bson.BsonDocument;
import sources.mongo.org.bson.Document;
import sources.mongo.org.bson.codecs.configuration.CodecRegistry;
import sources.mongo.org.bson.conversions.Bson;

class MongoDatabaseImpl implements MongoDatabase {
    private final String name;
    private final ReadPreference readPreference;
    private final CodecRegistry codecRegistry;
    private final WriteConcern writeConcern;
    private final OperationExecutor executor;

    MongoDatabaseImpl(final String name, final CodecRegistry codecRegistry, final ReadPreference readPreference,
                      final WriteConcern writeConcern, final OperationExecutor executor) {
        this.name = notNull("name", name);
        this.codecRegistry = notNull("codecRegistry", codecRegistry);
        this.readPreference = notNull("readPreference", readPreference);
        this.writeConcern = notNull("writeConcern", writeConcern);
        this.executor = notNull("executor", executor);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CodecRegistry getCodecRegistry() {
        return codecRegistry;
    }

    @Override
    public ReadPreference getReadPreference() {
        return readPreference;
    }

    @Override
    public WriteConcern getWriteConcern() {
        return writeConcern;
    }

    @Override
    public MongoDatabase withCodecRegistry(final CodecRegistry codecRegistry) {
        return new MongoDatabaseImpl(name, codecRegistry, readPreference, writeConcern, executor);
    }

    @Override
    public MongoDatabase withReadPreference(final ReadPreference readPreference) {
        return new MongoDatabaseImpl(name, codecRegistry, readPreference, writeConcern, executor);
    }

    @Override
    public MongoDatabase withWriteConcern(final WriteConcern writeConcern) {
        return new MongoDatabaseImpl(name, codecRegistry, readPreference, writeConcern, executor);
    }

    @Override
    public MongoCollection<Document> getCollection(final String collectionName) {
        return getCollection(collectionName, Document.class);
    }

    @Override
    public <TDocument> MongoCollection<TDocument> getCollection(final String collectionName, final Class<TDocument> documentClass) {
        return new MongoCollectionImpl<TDocument>(new MongoNamespace(name, collectionName), documentClass, codecRegistry, readPreference,
                                                  writeConcern, executor);
    }

    @Override
    public Document runCommand(final Bson command) {
        return runCommand(command, Document.class);
    }

    @Override
    public Document runCommand(final Bson command, final ReadPreference readPreference) {
        return runCommand(command, readPreference, Document.class);
    }

    @Override
    public <TResult> TResult runCommand(final Bson command, final Class<TResult> resultClass) {
        return runCommand(command, ReadPreference.primary(), resultClass);
    }

    @Override
    public <TResult> TResult runCommand(final Bson command, final ReadPreference readPreference, final Class<TResult> resultClass) {
        notNull("readPreference", readPreference);
        return executor.execute(new CommandReadOperation<TResult>(getName(), toBsonDocument(command), codecRegistry.get(resultClass)),
                readPreference);
    }

    @Override
    public void drop() {
        executor.execute(new DropDatabaseOperation(name));
    }

    @Override
    public MongoIterable<String> listCollectionNames() {
        return new ListCollectionsIterableImpl<BsonDocument>(name, BsonDocument.class, getDefaultCodecRegistry(), ReadPreference.primary(),
                executor).map(new Function<BsonDocument, String>() {
            @Override
            public String apply(final BsonDocument result) {
                return result.getString("name").getValue();
            }
        });
    }

    @Override
    public ListCollectionsIterable<Document> listCollections() {
        return listCollections(Document.class);
    }

    @Override
    public <TResult> ListCollectionsIterable<TResult> listCollections(final Class<TResult> resultClass) {
        return new ListCollectionsIterableImpl<TResult>(name, resultClass, codecRegistry, ReadPreference.primary(), executor);
    }

    @Override
    public void createCollection(final String collectionName) {
        createCollection(collectionName, new CreateCollectionOptions());
    }

    @Override
    public void createCollection(final String collectionName, final CreateCollectionOptions createCollectionOptions) {
        executor.execute(new CreateCollectionOperation(name, collectionName)
                .capped(createCollectionOptions.isCapped())
                .sizeInBytes(createCollectionOptions.getSizeInBytes())
                .autoIndex(createCollectionOptions.isAutoIndex())
                .maxDocuments(createCollectionOptions.getMaxDocuments())
                .usePowerOf2Sizes(createCollectionOptions.isUsePowerOf2Sizes())
                .storageEngineOptions(toBsonDocument(createCollectionOptions.getStorageEngineOptions())));
    }

    private BsonDocument toBsonDocument(final Bson document) {
        return document == null ? null : document.toBsonDocument(BsonDocument.class, codecRegistry);
    }
}

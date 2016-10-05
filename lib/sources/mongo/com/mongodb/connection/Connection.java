/*
 * Copyright 2013-2015 MongoDB, Inc.
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

import sources.mongo.com.mongodb.MongoNamespace;
import sources.mongo.com.mongodb.WriteConcern;
import sources.mongo.com.mongodb.WriteConcernResult;
import sources.mongo.com.mongodb.annotations.ThreadSafe;
import sources.mongo.com.mongodb.binding.ReferenceCounted;
import sources.mongo.com.mongodb.bulk.BulkWriteResult;
import sources.mongo.com.mongodb.bulk.DeleteRequest;
import sources.mongo.com.mongodb.bulk.InsertRequest;
import sources.mongo.com.mongodb.bulk.UpdateRequest;
import sources.mongo.org.bson.BsonDocument;
import sources.mongo.org.bson.FieldNameValidator;
import sources.mongo.org.bson.codecs.Decoder;

import java.util.List;


/**
 * A synchronous connection to a MongoDB server with blocking operations.
 *
 * <p> Implementations of this class are thread safe.  </p>
 *
 * @since 3.0
 */
@ThreadSafe
public interface Connection extends ReferenceCounted {

    @Override
    Connection retain();

    /**
     * Gets the description of the connection.
     *
     * @return the connection description
     */
    ConnectionDescription getDescription();

    /**
     * Insert the documents using the insert wire protocol and apply the write concern.
     *
     * @param namespace    the namespace
     * @param ordered      whether the writes are ordered
     * @param writeConcern the write concern
     * @param inserts      the inserts
     * @return the write concern result
     */
    WriteConcernResult insert(MongoNamespace namespace, boolean ordered, WriteConcern writeConcern, List<InsertRequest> inserts);

    /**
     * Update the documents using the update wire protocol and apply the write concern.
     *
     * @param namespace    the namespace
     * @param ordered      whether the writes are ordered
     * @param writeConcern the write concern
     * @param updates      the updates
     * @return the write concern result
     */
    WriteConcernResult update(MongoNamespace namespace, boolean ordered, WriteConcern writeConcern,
                              List<UpdateRequest> updates);

    /**
     * Delete the documents using the delete wire protocol and apply the write concern.
     *
     * @param namespace    the namespace
     * @param ordered      whether the writes are ordered
     * @param writeConcern the write concern
     * @param deletes      the deletes
     * @return the write concern result
     */
    WriteConcernResult delete(MongoNamespace namespace, boolean ordered, WriteConcern writeConcern,
                              List<DeleteRequest> deletes);

    /**
     * Insert the documents using the insert command.
     *
     * @param namespace    the namespace
     * @param ordered      whether the writes are ordered
     * @param writeConcern the write concern
     * @param inserts      the inserts
     * @return the bulk write result
     */
    BulkWriteResult insertCommand(MongoNamespace namespace, boolean ordered, WriteConcern writeConcern, List<InsertRequest> inserts);

    /**
     * Update the documents using the update command.
     *
     * @param namespace    the namespace
     * @param ordered      whether the writes are ordered
     * @param writeConcern the write concern
     * @param updates      the updates
     * @return the bulk write result
     */
    BulkWriteResult updateCommand(MongoNamespace namespace, boolean ordered, WriteConcern writeConcern, List<UpdateRequest> updates);

    /**
     * Delete the documents using the delete command.
     *
     * @param namespace    the namespace
     * @param ordered      whether the writes are ordered
     * @param writeConcern the write concern
     * @param deletes      the deletes
     * @return the bulk write result
     */
    BulkWriteResult deleteCommand(MongoNamespace namespace, boolean ordered, WriteConcern writeConcern, List<DeleteRequest> deletes);

    /**
     * Execute the command.
     *
     * @param database             the database to execute the command in
     * @param command              the command document
     * @param slaveOk              whether the command can run on a secondary
     * @param fieldNameValidator   the field name validator for the command document
     * @param commandResultDecoder the decoder for the result
     * @param <T>                  the type of the result
     * @return the command result
     */
    <T> T command(String database, BsonDocument command, boolean slaveOk, FieldNameValidator fieldNameValidator,
                  Decoder<T> commandResultDecoder);

    /**
     * Execute the query.
     *
     * @param namespace       the namespace to query
     * @param queryDocument   the query document
     * @param fields          the field to include or exclude
     * @param numberToReturn  the number of documents to return
     * @param skip            the number of documents to skip
     * @param slaveOk         whether the query can run on a secondary
     * @param tailableCursor  whether to return a tailable cursor
     * @param awaitData       whether a tailable cursor should wait before returning if no documents are available
     * @param noCursorTimeout whether the cursor should not timeout
     * @param partial         whether partial results from sharded clusters are acceptable
     * @param oplogReplay     whether to replay the oplog
     * @param resultDecoder   the decoder for the query result documents
     * @param <T>             the query result document type
     * @return the query results
     */
    <T> QueryResult<T> query(MongoNamespace namespace, BsonDocument queryDocument, BsonDocument fields,
                             int numberToReturn, int skip,
                             boolean slaveOk, boolean tailableCursor, boolean awaitData, boolean noCursorTimeout,
                             boolean partial, boolean oplogReplay,
                             Decoder<T> resultDecoder);

    /**
     * Get more result documents from a cursor.
     *
     * @param namespace      the namespace to get more documents from
     * @param cursorId       the cursor id
     * @param numberToReturn the number of documents to return
     * @param resultDecoder  the decoder for the query results
     * @param <T>            the type of the query result documents
     * @return the query results
     */
    <T> QueryResult<T> getMore(MongoNamespace namespace, long cursorId, int numberToReturn, Decoder<T> resultDecoder);

    /**
     * Kills the given list of cursors.
     *
     * @param cursors the cursors
     */
    void killCursor(List<Long> cursors);
}

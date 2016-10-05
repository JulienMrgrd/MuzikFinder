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

import sources.mongo.com.mongodb.MongoNamespace;
import sources.mongo.com.mongodb.WriteConcern;
import sources.mongo.com.mongodb.bulk.InsertRequest;
import sources.mongo.com.mongodb.internal.validator.CollectibleDocumentFieldNameValidator;
import sources.mongo.com.mongodb.internal.validator.MappedFieldNameValidator;
import sources.mongo.com.mongodb.internal.validator.NoOpFieldNameValidator;
import sources.mongo.org.bson.BsonBinaryWriter;
import sources.mongo.org.bson.BsonDocument;
import sources.mongo.org.bson.FieldNameValidator;
import sources.mongo.org.bson.codecs.EncoderContext;
import sources.mongo.org.bson.io.BsonOutput;

import static sources.mongo.com.mongodb.assertions.Assertions.notNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An insert command message.
 *
 * @mongodb.driver.manual reference/command/insert/#dbcmd.insert Insert Command
 */
class InsertCommandMessage extends BaseWriteCommandMessage {
    private final List<InsertRequest> insertRequestList;

    /**
     * Construct a new instance.
     *
     * @param namespace         the namespace
     * @param ordered           whether the inserts are ordered
     * @param writeConcern      the write concern
     * @param insertRequestList the list of inserts
     * @param settings          the message settings
     */
    public InsertCommandMessage(final MongoNamespace namespace, final boolean ordered, final WriteConcern writeConcern,
                                final List<InsertRequest> insertRequestList, final MessageSettings settings) {
        super(namespace, ordered, writeConcern, settings);
        this.insertRequestList = notNull("insertRequestList", insertRequestList);
    }

    @Override
    public int getItemCount() {
        return insertRequestList.size();
    }

    @Override
    protected FieldNameValidator getFieldNameValidator() {
        Map<String, FieldNameValidator> map = new HashMap<String, FieldNameValidator>();
        map.put("documents", new CollectibleDocumentFieldNameValidator());
        return new MappedFieldNameValidator(new NoOpFieldNameValidator(), map);
    }

    /**
     * Gets the list of insert requests.
     *
     * @return the non-null list of insert requests
     */
    public List<InsertRequest> getRequests() {
        return Collections.unmodifiableList(insertRequestList);
    }

    /**
     * Gets the command name, which is "insert".
     *
     * @return the command name
     */
    protected String getCommandName() {
        return "insert";
    }

    protected InsertCommandMessage writeTheWrites(final BsonOutput bsonOutput, final int commandStartPosition,
                                                  final BsonBinaryWriter writer) {
        InsertCommandMessage nextMessage = null;
        writer.writeStartArray("documents");
        writer.pushMaxDocumentSize(getSettings().getMaxDocumentSize());
        for (int i = 0; i < insertRequestList.size(); i++) {
            writer.mark();
            BsonDocument document = insertRequestList.get(i).getDocument();
            getCodec(document).encode(writer, document, EncoderContext.builder().isEncodingCollectibleDocument(true).build());
            if (exceedsLimits(bsonOutput.getPosition() - commandStartPosition, i + 1)) {
                writer.reset();
                nextMessage = new InsertCommandMessage(getWriteNamespace(), isOrdered(), getWriteConcern(),
                                                       insertRequestList.subList(i, insertRequestList.size()),
                                                       getSettings());
                break;
            }
        }
        writer.popMaxDocumentSize();
        writer.writeEndArray();
        return nextMessage;
    }
}

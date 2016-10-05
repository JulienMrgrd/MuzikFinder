/*
 * Copyright 2008-2015 MongoDB, Inc.
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

import sources.mongo.com.mongodb.ServerAddress;
import sources.mongo.com.mongodb.Tag;
import sources.mongo.com.mongodb.TagSet;
import sources.mongo.org.bson.BsonArray;
import sources.mongo.org.bson.BsonBoolean;
import sources.mongo.org.bson.BsonDocument;
import sources.mongo.org.bson.BsonInt32;
import sources.mongo.org.bson.BsonString;
import sources.mongo.org.bson.BsonValue;
import sources.mongo.org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static sources.mongo.com.mongodb.connection.ConnectionDescription.getDefaultMaxMessageSize;
import static sources.mongo.com.mongodb.connection.ConnectionDescription.getDefaultMaxWriteBatchSize;
import static sources.mongo.com.mongodb.connection.ServerConnectionState.CONNECTED;
import static sources.mongo.com.mongodb.connection.ServerDescription.getDefaultMaxDocumentSize;
import static sources.mongo.com.mongodb.connection.ServerDescription.getDefaultMaxWireVersion;
import static sources.mongo.com.mongodb.connection.ServerDescription.getDefaultMinWireVersion;
import static sources.mongo.com.mongodb.connection.ServerType.REPLICA_SET_ARBITER;
import static sources.mongo.com.mongodb.connection.ServerType.REPLICA_SET_PRIMARY;
import static sources.mongo.com.mongodb.connection.ServerType.REPLICA_SET_SECONDARY;
import static sources.mongo.com.mongodb.connection.ServerType.SHARD_ROUTER;
import static sources.mongo.com.mongodb.connection.ServerType.STANDALONE;
import static sources.mongo.com.mongodb.connection.ServerType.UNKNOWN;

final class DescriptionHelper {

    static ConnectionDescription createConnectionDescription(final ConnectionId connectionId,
                                                             final BsonDocument isMasterResult,
                                                             final BsonDocument buildInfoResult) {
        return new ConnectionDescription(connectionId, getVersion(buildInfoResult), getServerType(isMasterResult),
                                         getMaxWriteBatchSize(isMasterResult), getMaxBsonObjectSize(isMasterResult),
                                         getMaxMessageSizeBytes(isMasterResult));

    }

    static ServerDescription createServerDescription(final ServerAddress serverAddress, final BsonDocument isMasterResult,
                                                     final ServerVersion serverVersion, final long roundTripTime) {
        return ServerDescription.builder()
                                .state(CONNECTED)
                                .version(serverVersion)
                                .address(serverAddress)
                                .type(getServerType(isMasterResult))
                                .canonicalAddress(isMasterResult.containsKey("me") ? isMasterResult.getString("me").getValue() : null)
                                .hosts(listToSet(isMasterResult.getArray("hosts", new BsonArray())))
                                .passives(listToSet(isMasterResult.getArray("passives", new BsonArray())))
                                .arbiters(listToSet(isMasterResult.getArray("arbiters", new BsonArray())))
                                .primary(getString(isMasterResult, "primary"))
                                .maxDocumentSize(getMaxBsonObjectSize(isMasterResult))
                                .tagSet(getTagSetFromDocument(isMasterResult.getDocument("tags", new BsonDocument())))
                                .setName(getString(isMasterResult, "setName"))
                                .minWireVersion(isMasterResult.getInt32("minWireVersion",
                                                                        new BsonInt32(getDefaultMinWireVersion())).getValue())
                                .maxWireVersion(isMasterResult.getInt32("maxWireVersion",
                                                                        new BsonInt32(getDefaultMaxWireVersion())).getValue())
                                .electionId(getElectionId(isMasterResult))
                                .roundTripTime(roundTripTime, NANOSECONDS)
                                .ok(CommandHelper.isCommandOk(isMasterResult)).build();
    }

    private static ObjectId getElectionId(final BsonDocument isMasterResult) {
        return isMasterResult.containsKey("electionId") ? isMasterResult.getObjectId("electionId").getValue() : null;
    }

    private static int getMaxMessageSizeBytes(final BsonDocument isMasterResult) {
        return isMasterResult.getInt32("maxMessageSizeBytes", new BsonInt32(getDefaultMaxMessageSize())).getValue();
    }

    private static int getMaxBsonObjectSize(final BsonDocument isMasterResult) {
        return isMasterResult.getInt32("maxBsonObjectSize", new BsonInt32(getDefaultMaxDocumentSize())).getValue();
    }

    private static int getMaxWriteBatchSize(final BsonDocument isMasterResult) {
        return isMasterResult.getInt32("maxWriteBatchSize", new BsonInt32(getDefaultMaxWriteBatchSize())).getValue();
    }

    private static String getString(final BsonDocument response, final String key) {
        if (response.containsKey(key)) {
            return response.getString(key).getValue();
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    static ServerVersion getVersion(final BsonDocument buildInfoResult) {
        List<BsonValue> versionArray = buildInfoResult.getArray("versionArray").subList(0, 3);

        return new ServerVersion(asList(versionArray.get(0).asInt32().getValue(),
                                        versionArray.get(1).asInt32().getValue(),
                                        versionArray.get(2).asInt32().getValue()));
    }

    private static Set<String> listToSet(final BsonArray array) {
        if (array == null || array.isEmpty()) {
            return Collections.emptySet();
        } else {
            Set<String> set = new HashSet<String>();
            for (BsonValue value : array) {
                set.add(value.asString().getValue());
            }
            return set;
        }
    }

    private static ServerType getServerType(final BsonDocument isMasterResult) {

        if (!CommandHelper.isCommandOk(isMasterResult)) {
            return UNKNOWN;
        }

        if (isReplicaSetMember(isMasterResult)) {
            if (isMasterResult.getBoolean("ismaster", BsonBoolean.FALSE).getValue()) {
                return REPLICA_SET_PRIMARY;
            }

            if (isMasterResult.getBoolean("secondary", BsonBoolean.FALSE).getValue()) {
                return REPLICA_SET_SECONDARY;
            }

            if (isMasterResult.getBoolean("arbiterOnly", BsonBoolean.FALSE).getValue()) {
                return REPLICA_SET_ARBITER;
            }

            if (isMasterResult.containsKey("setName") && isMasterResult.containsKey("hosts")) {
                return ServerType.REPLICA_SET_OTHER;
            }

            return ServerType.REPLICA_SET_GHOST;
        }

        if (isMasterResult.containsKey("msg") && isMasterResult.get("msg").equals(new BsonString("isdbgrid"))) {
            return SHARD_ROUTER;
        }

        return STANDALONE;
    }

    private static boolean isReplicaSetMember(final BsonDocument isMasterResult) {
        return isMasterResult.containsKey("setName") || isMasterResult.getBoolean("isreplicaset", BsonBoolean.FALSE).getValue();
    }

    private static TagSet getTagSetFromDocument(final BsonDocument tagsDocuments) {
        List<Tag> tagList = new ArrayList<Tag>();
        for (final Map.Entry<String, BsonValue> curEntry : tagsDocuments.entrySet()) {
            tagList.add(new Tag(curEntry.getKey(), curEntry.getValue().asString().getValue()));
        }
        return new TagSet(tagList);
    }

    private DescriptionHelper() {
    }
}

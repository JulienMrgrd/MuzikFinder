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

import sources.mongo.com.mongodb.MongoCredential;
import sources.mongo.org.bson.BsonArray;
import sources.mongo.org.bson.BsonBoolean;
import sources.mongo.org.bson.BsonDocument;
import sources.mongo.org.bson.BsonObjectId;
import sources.mongo.org.bson.BsonString;
import sources.mongo.org.bson.BsonValue;
import sources.mongo.org.bson.types.ObjectId;

import static sources.mongo.com.mongodb.internal.authentication.NativeAuthenticationHelper.createAuthenticationHash;

import java.util.Arrays;

final class UserOperationHelper {

    static BsonDocument asCommandDocument(final MongoCredential credential, final boolean readOnly, final String commandName) {
        BsonDocument document = new BsonDocument();
        document.put(commandName, new BsonString(credential.getUserName()));
        document.put("pwd", new BsonString(createAuthenticationHash(credential.getUserName(),
                                                                    credential.getPassword())));
        document.put("digestPassword", BsonBoolean.FALSE);
        document.put("roles", new BsonArray(Arrays.<BsonValue>asList(new BsonString(getRoleName(credential, readOnly)))));
        return document;
    }

    private static String getRoleName(final MongoCredential credential, final boolean readOnly) {
        return credential.getSource().equals("admin")
               ? (readOnly ? "readAnyDatabase" : "root") : (readOnly ? "read" : "dbOwner");
    }

    static BsonDocument asCollectionQueryDocument(final MongoCredential credential) {
        return new BsonDocument("user", new BsonString(credential.getUserName()));
    }

    static BsonDocument asCollectionUpdateDocument(final MongoCredential credential, final boolean readOnly) {
        return asCollectionQueryDocument(credential)
               .append("pwd", new BsonString(createAuthenticationHash(credential.getUserName(), credential.getPassword())))
               .append("readOnly", BsonBoolean.valueOf(readOnly));
    }

    static BsonDocument asCollectionInsertDocument(final MongoCredential credential, final boolean readOnly) {
        return asCollectionUpdateDocument(credential, readOnly)
               .append("_id", new BsonObjectId(new ObjectId()));
    }

    private UserOperationHelper() {
    }
}

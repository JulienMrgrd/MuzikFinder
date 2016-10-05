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

import sources.mongo.com.mongodb.bulk.DeleteRequest;
import sources.mongo.org.bson.BsonDocumentWrapper;
import sources.mongo.org.bson.codecs.Encoder;

class RemoveRequest extends WriteRequest {
    private final DBObject query;
    private final boolean multi;
    private final Encoder<DBObject> codec;

    public RemoveRequest(final DBObject query, final boolean multi, final Encoder<DBObject> codec) {
        this.query = query;
        this.multi = multi;
        this.codec = codec;
    }

    public DBObject getQuery() {
        return query;
    }

    public boolean isMulti() {
        return multi;
    }

    @Override
    sources.mongo.com.mongodb.bulk.WriteRequest toNew() {
        return new DeleteRequest(new BsonDocumentWrapper<DBObject>(query, this.codec)).multi(isMulti());
    }
}

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

import static sources.mongo.org.bson.BsonType.DOCUMENT;

import java.util.ArrayList;
import java.util.List;

import sources.mongo.org.bson.BsonArray;
import sources.mongo.org.bson.BsonDocumentWrapper;
import sources.mongo.org.bson.BsonReader;
import sources.mongo.org.bson.BsonType;
import sources.mongo.org.bson.BsonValue;
import sources.mongo.org.bson.codecs.BsonArrayCodec;
import sources.mongo.org.bson.codecs.Decoder;
import sources.mongo.org.bson.codecs.DecoderContext;
import sources.mongo.org.bson.codecs.configuration.CodecRegistry;

class CommandResultArrayCodec<T> extends BsonArrayCodec {
    private final Decoder<T> decoder;

    CommandResultArrayCodec(final CodecRegistry registry, final Decoder<T> decoder) {
        super(registry);
        this.decoder = decoder;
    }

    @Override
    public BsonArray decode(final BsonReader reader, final DecoderContext decoderContext) {
        reader.readStartArray();

        List<T> list = new ArrayList<T>();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            list.add(decoder.decode(reader, decoderContext));
        }
        reader.readEndArray();

        return new BsonArrayWrapper<T>(list);
    }

    @Override
    protected BsonValue readValue(final BsonReader reader, final DecoderContext decoderContext) {
        if (reader.getCurrentBsonType() == DOCUMENT) {
            return new BsonDocumentWrapper<T>(decoder.decode(reader, decoderContext), null);
        } else {
            return super.readValue(reader, decoderContext);
        }
    }
}

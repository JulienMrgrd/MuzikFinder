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

import java.util.HashMap;
import java.util.Map;

import sources.mongo.org.bson.BsonArray;
import sources.mongo.org.bson.BsonDocument;
import sources.mongo.org.bson.BsonValue;
import sources.mongo.org.bson.codecs.BsonArrayCodec;
import sources.mongo.org.bson.codecs.BsonBinaryCodec;
import sources.mongo.org.bson.codecs.BsonBooleanCodec;
import sources.mongo.org.bson.codecs.BsonDBPointerCodec;
import sources.mongo.org.bson.codecs.BsonDateTimeCodec;
import sources.mongo.org.bson.codecs.BsonDocumentCodec;
import sources.mongo.org.bson.codecs.BsonDoubleCodec;
import sources.mongo.org.bson.codecs.BsonInt32Codec;
import sources.mongo.org.bson.codecs.BsonInt64Codec;
import sources.mongo.org.bson.codecs.BsonJavaScriptCodec;
import sources.mongo.org.bson.codecs.BsonJavaScriptWithScopeCodec;
import sources.mongo.org.bson.codecs.BsonMaxKeyCodec;
import sources.mongo.org.bson.codecs.BsonMinKeyCodec;
import sources.mongo.org.bson.codecs.BsonNullCodec;
import sources.mongo.org.bson.codecs.BsonObjectIdCodec;
import sources.mongo.org.bson.codecs.BsonRegularExpressionCodec;
import sources.mongo.org.bson.codecs.BsonStringCodec;
import sources.mongo.org.bson.codecs.BsonSymbolCodec;
import sources.mongo.org.bson.codecs.BsonTimestampCodec;
import sources.mongo.org.bson.codecs.BsonUndefinedCodec;
import sources.mongo.org.bson.codecs.Codec;
import sources.mongo.org.bson.codecs.Decoder;
import sources.mongo.org.bson.codecs.configuration.CodecProvider;
import sources.mongo.org.bson.codecs.configuration.CodecRegistry;

class CommandResultCodecProvider<P> implements CodecProvider {
    private final Map<Class<?>, Codec<?>> codecs = new HashMap<Class<?>, Codec<?>>();
    private final Decoder<P> payloadDecoder;
    private final String fieldContainingPayload;

    /**
     * Construct a new instance. with the default codec for each BSON type.
     *
     * @param payloadDecoder the specific decoder to use on the field.
     * @param fieldContainingPayload the field name to be decoded with the payloadDecoder.
     */
    public CommandResultCodecProvider(final Decoder<P> payloadDecoder, final String fieldContainingPayload) {
        this.payloadDecoder = payloadDecoder;
        this.fieldContainingPayload = fieldContainingPayload;
        addCodecs();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry) {
        if (codecs.containsKey(clazz)) {
            return (Codec<T>) codecs.get(clazz);
        }

        if (clazz == BsonArray.class) {
            return (Codec<T>) new BsonArrayCodec(registry);
        }

        if (clazz == BsonDocument.class) {
            return (Codec<T>) new CommandResultDocumentCodec<P>(registry, payloadDecoder, fieldContainingPayload);
        }

        return null;
    }

    private void addCodecs() {
        addCodec(new BsonNullCodec());
        addCodec(new BsonBinaryCodec());
        addCodec(new BsonBooleanCodec());
        addCodec(new BsonDateTimeCodec());
        addCodec(new BsonDBPointerCodec());
        addCodec(new BsonDoubleCodec());
        addCodec(new BsonInt32Codec());
        addCodec(new BsonInt64Codec());
        addCodec(new BsonMinKeyCodec());
        addCodec(new BsonMaxKeyCodec());
        addCodec(new BsonJavaScriptCodec());
        addCodec(new BsonObjectIdCodec());
        addCodec(new BsonRegularExpressionCodec());
        addCodec(new BsonStringCodec());
        addCodec(new BsonSymbolCodec());
        addCodec(new BsonTimestampCodec());
        addCodec(new BsonUndefinedCodec());
        addCodec(new BsonJavaScriptWithScopeCodec(new BsonDocumentCodec()));
    }

    private <T extends BsonValue> void addCodec(final Codec<T> codec) {
        codecs.put(codec.getEncoderClass(), codec);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CommandResultCodecProvider<?> that = (CommandResultCodecProvider) o;

        if (!fieldContainingPayload.equals(that.fieldContainingPayload)) {
            return false;
        }
        if (!payloadDecoder.getClass().equals(that.payloadDecoder.getClass())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = payloadDecoder.getClass().hashCode();
        result = 31 * result + fieldContainingPayload.hashCode();
        return result;
    }
}

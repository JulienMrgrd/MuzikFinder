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

package sources.mongo.org.bson.codecs;

import java.util.HashMap;
import java.util.Map;

import sources.mongo.org.bson.codecs.configuration.CodecProvider;
import sources.mongo.org.bson.codecs.configuration.CodecRegistry;

/**
 * A Codec provider for dynamically-typed value classes.  Other providers are needed for containers for maps and arrays.  It provides the
 * following codecs:
 *
 * <ul>
 *     <li>{@link sources.mongo.org.bson.codecs.BinaryCodec}</li>
 *     <li>{@link sources.mongo.org.bson.codecs.BooleanCodec}</li>
 *     <li>{@link sources.mongo.org.bson.codecs.DateCodec}</li>
 *     <li>{@link sources.mongo.org.bson.codecs.DoubleCodec}</li>
 *     <li>{@link sources.mongo.org.bson.codecs.IntegerCodec}</li>
 *     <li>{@link sources.mongo.org.bson.codecs.LongCodec}</li>
 *     <li>{@link sources.mongo.org.bson.codecs.MinKeyCodec}</li>
 *     <li>{@link sources.mongo.org.bson.codecs.MaxKeyCodec}</li>
 *     <li>{@link sources.mongo.org.bson.codecs.CodeCodec}</li>
 *     <li>{@link sources.mongo.org.bson.codecs.ObjectIdCodec}</li>
 *     <li>{@link sources.mongo.org.bson.codecs.CharacterCodec}</li>
 *     <li>{@link sources.mongo.org.bson.codecs.StringCodec}</li>
 *     <li>{@link sources.mongo.org.bson.codecs.SymbolCodec}</li>
 *     <li>{@link sources.mongo.org.bson.codecs.UuidCodec}</li>
 *     <li>{@link sources.mongo.org.bson.codecs.ByteCodec}</li>
 *     <li>{@link sources.mongo.org.bson.codecs.ShortCodec}</li>
 *     <li>{@link sources.mongo.org.bson.codecs.ByteArrayCodec}</li>
 *     <li>{@link sources.mongo.org.bson.codecs.FloatCodec}</li>
 *     <li>{@link sources.mongo.org.bson.codecs.AtomicBooleanCodec}</li>
 *     <li>{@link sources.mongo.org.bson.codecs.AtomicIntegerCodec}</li>
 *     <li>{@link sources.mongo.org.bson.codecs.AtomicLongCodec}</li>
 * </ul>
 *
 * @since 3.0
 */
public class ValueCodecProvider implements CodecProvider {
    private final Map<Class<?>, Codec<?>> codecs = new HashMap<Class<?>, Codec<?>>();

    /**
     * A provider of Codecs for simple value types.
     */
    public ValueCodecProvider() {
        addCodecs();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry) {
        return (Codec<T>) codecs.get(clazz);
    }

    private void addCodecs() {
        addCodec(new BinaryCodec());
        addCodec(new BooleanCodec());
        addCodec(new DateCodec());
        addCodec(new DoubleCodec());
        addCodec(new IntegerCodec());
        addCodec(new LongCodec());
        addCodec(new MinKeyCodec());
        addCodec(new MaxKeyCodec());
        addCodec(new CodeCodec());
        addCodec(new ObjectIdCodec());
        addCodec(new CharacterCodec());
        addCodec(new StringCodec());
        addCodec(new SymbolCodec());
        addCodec(new UuidCodec());

        addCodec(new ByteCodec());
        addCodec(new PatternCodec());
        addCodec(new ShortCodec());
        addCodec(new ByteArrayCodec());
        addCodec(new FloatCodec());
        addCodec(new AtomicBooleanCodec());
        addCodec(new AtomicIntegerCodec());
        addCodec(new AtomicLongCodec());
    }

    private <T> void addCodec(final Codec<T> codec) {
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

        return true;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}

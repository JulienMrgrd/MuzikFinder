/*
 * Copyright 2015 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sources.mongo.org.bson.codecs.configuration;

import java.util.ArrayList;
import java.util.List;

import sources.mongo.org.bson.codecs.Codec;

import static java.util.Arrays.asList;

/**
 * A helper class for creating and combining codecs, codec providers, and codec registries
 *
 * @since 3.0
 */
public final class CodecRegistries {

    /**
     * Creates a {@code CodecRegistry} from the provided list of {@code Codec} instances.
     *
     * <p>This registry can then be used alongside other registries. Typically used when adding extra codecs to existing codecs with the
     * {@link #fromRegistries(CodecRegistry...)} )} helper.</p>
     *
     * @param codecs the {@code Codec} to create a registry for
     * @return a {@code CodecRegistry} for the given list of {@code Codec} instances.
     */
    public static CodecRegistry fromCodecs(final Codec<?>... codecs) {
        return fromCodecs(asList(codecs));
    }

    /**
     * Creates a {@code CodecRegistry} from the provided list of {@code Codec} instances.
     *
     * <p>This registry can then be used alongside other registries.  Typically used when adding extra codecs to existing codecs with the
     * {@link #fromRegistries(CodecRegistry...)} )} helper.</p>
     *
     * @param codecs the {@code Codec} to create a registry for
     * @return a {@code CodecRegistry} for the given list of {@code Codec} instances.
     */
    public static CodecRegistry fromCodecs(final List<? extends Codec<?>> codecs) {
        return fromProviders(new MapOfCodecsProvider(codecs));
    }

    /**
     * Creates a {@code CodecRegistry} from the provided list of {@code CodecProvider} instances.
     *
     * <p>The created instance can handle cycles of {@code Codec} dependencies, i.e when the construction of a {@code Codec} for class A
     * requires the construction of a {@code Codec} for class B, and vice versa.</p>
     *
     * @param providers the codec provider
     * @return a {@code CodecRegistry} with the ordered list of {@code CodecProvider} instances. The registry is also guaranteed to be an
     * instance of {code CodecProvider}, so that when one is passed to {@link #fromRegistries(CodecRegistry...)} or {@link
     * #fromRegistries(java.util.List)} it will be treated as a {@code CodecProvider} and properly resolve any dependencies between
     * registries.
     */
    public static CodecRegistry fromProviders(final CodecProvider... providers) {
        return fromProviders(asList(providers));
    }

    /**
     * Creates a {@code CodecRegistry} from the provided list of {@code CodecProvider} instances.
     *
     * <p>The created instance can handle cycles of {@code Codec} dependencies, i.e when the construction of a {@code Codec} for class A
     * requires the construction of a {@code Codec} for class B, and vice versa.</p>
     *
     * @param providers the codec provider
     * @return a {@code CodecRegistry} with the ordered list of {@code CodecProvider} instances. The registry is also guaranteed to be an
     * instance of {code CodecProvider}, so that when one is passed to {@link #fromRegistries(CodecRegistry...)} or {@link
     * #fromRegistries(java.util.List)} it will be treated as a {@code CodecProvider} and properly resolve any dependencies between
     * registries.
     */
    public static CodecRegistry fromProviders(final List<? extends CodecProvider> providers) {
        return new ProvidersCodecRegistry(providers);
    }

    /**
     * A {@code CodecRegistry} that combines the given {@code CodecRegistry} instances into a single registry.
     *
     * <p>The registries are checked in order until one returns a {@code Codec} for the requested {@code Class}.</p>
     *
     * <p>The created instance can handle cycles of {@code Codec} dependencies, i.e when the construction of a {@code Codec} for class A
     * requires the construction of a {@code Codec} for class B, and vice versa.</p>

     * <p>Any of the given registries that also implement {@code CodecProvider} will be treated as a {@code CodecProvider} instead of a
     * {@code CodecRegistry}, which will ensure proper resolution of any dependencies between registries.</p>
     *
     * @param registries the preferred registry for {@code Codec} lookups
     *
     * @return a {@code CodecRegistry} that combines the list of {@code CodecRegistry} instances into a single one
     */
    public static CodecRegistry fromRegistries(final CodecRegistry... registries) {
        return fromRegistries(asList(registries));
    }

    /**
     * A {@code CodecRegistry} that combines the given {@code CodecRegistry} instances into a single registry.
     *
     * <p>The registries are checked in order until one returns a {@code Codec} for the requested {@code Class}.</p>
     *
     * <p>The created instance can handle cycles of {@code Codec} dependencies, i.e when the construction of a {@code Codec} for class A
     * requires the construction of a {@code Codec} for class B, and vice versa.</p>

     * <p>Any of the given registries that also implement {@code CodecProvider} will be treated as a {@code CodecProvider} instead of a
     * {@code CodecRegistry}, which will ensure proper resolution of any dependencies between registries.</p>
     *
     * @param registries the preferred registry for {@code Codec} lookups
     *
     * @return a {@code CodecRegistry} that combines the list of {@code CodecRegistry} instances into a single one
     */
    public static CodecRegistry fromRegistries(final List<? extends CodecRegistry> registries) {
        List<CodecProvider> providers = new ArrayList<CodecProvider>();
        for (CodecRegistry registry : registries) {
            providers.add(providerFromRegistry(registry));
        }
        return new ProvidersCodecRegistry(providers);
    }

    private static CodecProvider providerFromRegistry(final CodecRegistry innerRegistry) {
        if (innerRegistry instanceof CodecProvider) {
            return (CodecProvider) innerRegistry;
        } else {
            return new CodecProvider() {
                @Override
                public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry outerRregistry) {
                    try {
                        return innerRegistry.get(clazz);
                    } catch (CodecConfigurationException e) {
                        return null;
                    }
                }
            };
        }
    }

    private CodecRegistries() {
    }
}

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

package sources.mongo.com.mongodb.client;

import java.util.concurrent.TimeUnit;

/**
 * Iterable for aggregate.
 *
 * @param <TResult> The type of the result.
 * @since 3.0
 */
public interface AggregateIterable<TResult> extends MongoIterable<TResult> {

    /**
     * Enables writing to temporary files. A null value indicates that it's unspecified.
     *
     * @param allowDiskUse true if writing to temporary files is enabled
     * @return this
     * @mongodb.driver.manual reference/command/aggregate/ Aggregation
     * @mongodb.server.release 2.6
     */
    AggregateIterable<TResult> allowDiskUse(final Boolean allowDiskUse);

    /**
     * Sets the number of documents to return per batch.
     *
     * @param batchSize the batch size
     * @return this
     * @mongodb.driver.manual reference/method/cursor.batchSize/#cursor.batchSize Batch Size
     */
    AggregateIterable<TResult> batchSize(final int batchSize);

    /**
     * Sets the maximum execution time on the server for this operation.
     *
     * @param maxTime  the max time
     * @param timeUnit the time unit, which may not be null
     * @return this
     * @mongodb.driver.manual reference/method/cursor.maxTimeMS/#cursor.maxTimeMS Max Time
     */
    AggregateIterable<TResult> maxTime(final long maxTime, final TimeUnit timeUnit);

    /**
     * Sets whether the server should use a cursor to return results.
     *
     * @param useCursor whether the server should use a cursor to return results
     * @return this
     * @mongodb.driver.manual reference/command/aggregate/ Aggregation
     * @mongodb.server.release 2.6
     */
    AggregateIterable<TResult> useCursor(final Boolean useCursor);

}

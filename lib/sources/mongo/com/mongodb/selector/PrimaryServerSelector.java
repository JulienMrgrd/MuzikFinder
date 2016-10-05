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

package sources.mongo.com.mongodb.selector;

import java.util.List;

import sources.mongo.com.mongodb.connection.ClusterDescription;
import sources.mongo.com.mongodb.connection.ServerDescription;

/**
 * A server selector that chooses servers that are primaries.
 *
 * @since 3.0
 */
public final class PrimaryServerSelector implements ServerSelector {

    @Override
    public List<ServerDescription> select(final ClusterDescription clusterDescription) {
        return clusterDescription.getPrimaries();
    }

    @Override
    public String toString() {
        return "PrimaryServerSelector";
    }
}

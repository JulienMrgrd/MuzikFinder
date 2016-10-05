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

package sources.mongo.com.mongodb.selector;

import static sources.mongo.com.mongodb.assertions.Assertions.notNull;

import java.util.List;

import sources.mongo.com.mongodb.ReadPreference;
import sources.mongo.com.mongodb.connection.ClusterConnectionMode;
import sources.mongo.com.mongodb.connection.ClusterDescription;
import sources.mongo.com.mongodb.connection.ServerDescription;

/**
 * A server selector that chooses based on a read preference.
 *
 * @since 3.0
 */
public class ReadPreferenceServerSelector implements ServerSelector {
    private final ReadPreference readPreference;

    /**
     * Gets the read preference.
     *
     * @param readPreference the read preference
     */
    public ReadPreferenceServerSelector(final ReadPreference readPreference) {
        this.readPreference = notNull("readPreference", readPreference);
    }

    /**
     * Gets the read preference.
     *
     * @return the read preference
     */
    public ReadPreference getReadPreference() {
        return readPreference;
    }

    @Override
    public List<ServerDescription> select(final ClusterDescription clusterDescription) {
        if (clusterDescription.getConnectionMode() == ClusterConnectionMode.SINGLE) {
            return clusterDescription.getAny();
        }
        return readPreference.choose(clusterDescription);
    }

    @Override
    public String toString() {
        return "ReadPreferenceServerSelector{"
               + "readPreference=" + readPreference
               + '}';
    }
}

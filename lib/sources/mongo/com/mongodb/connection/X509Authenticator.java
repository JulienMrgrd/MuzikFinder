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

package sources.mongo.com.mongodb.connection;

import static sources.mongo.com.mongodb.connection.CommandHelper.executeCommand;
import static sources.mongo.com.mongodb.connection.CommandHelper.executeCommandAsync;

import sources.mongo.com.mongodb.AuthenticationMechanism;
import sources.mongo.com.mongodb.MongoCommandException;
import sources.mongo.com.mongodb.MongoCredential;
import sources.mongo.com.mongodb.MongoSecurityException;
import sources.mongo.com.mongodb.async.SingleResultCallback;
import sources.mongo.org.bson.BsonDocument;
import sources.mongo.org.bson.BsonInt32;
import sources.mongo.org.bson.BsonString;

class X509Authenticator extends Authenticator {
    X509Authenticator(final MongoCredential credential) {
        super(credential);
    }

    @Override
    void authenticate(final InternalConnection connection, final ConnectionDescription connectionDescription) {
        try {
            BsonDocument authCommand = getAuthCommand(getCredential().getUserName());
            executeCommand(getCredential().getSource(), authCommand, connection);
        } catch (MongoCommandException e) {
            throw new MongoSecurityException(getCredential(), "Exception authenticating", e);
        }
    }

    @Override
    void authenticateAsync(final InternalConnection connection, final ConnectionDescription connectionDescription,
                           final SingleResultCallback<Void> callback) {
        executeCommandAsync(getCredential().getSource(), getAuthCommand(getCredential().getUserName()), connection,
                            new SingleResultCallback<BsonDocument>() {
                                @Override
                                public void onResult(final BsonDocument nonceResult, final Throwable t) {
                                    if (t != null) {
                                        callback.onResult(null, translateThrowable(t));
                                    } else {
                                        callback.onResult(null, null);
                                    }
                                }
                            });
    }

    private BsonDocument getAuthCommand(final String userName) {
        BsonDocument cmd = new BsonDocument();

        cmd.put("authenticate", new BsonInt32(1));
        cmd.put("user", new BsonString(userName));
        cmd.put("mechanism", new BsonString(AuthenticationMechanism.MONGODB_X509.getMechanismName()));

        return cmd;
    }

    private Throwable translateThrowable(final Throwable t) {
        if (t instanceof MongoCommandException) {
            return new MongoSecurityException(getCredential(), "Exception authenticating", t);
        } else {
            return t;
        }
    }
}

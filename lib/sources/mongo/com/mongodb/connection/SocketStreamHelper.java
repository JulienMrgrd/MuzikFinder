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

import javax.net.ssl.SSLSocket;

import sources.mongo.com.mongodb.MongoInternalException;
import sources.mongo.com.mongodb.ServerAddress;

import java.io.IOException;
import java.net.Socket;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static sources.mongo.com.mongodb.internal.connection.SslHelper.enableHostNameVerification;

final class SocketStreamHelper {
    static void initialize(final Socket socket, final ServerAddress address, final SocketSettings settings, final SslSettings sslSettings)
    throws IOException {
        socket.setTcpNoDelay(true);
        socket.setSoTimeout(settings.getReadTimeout(MILLISECONDS));
        socket.setKeepAlive(settings.isKeepAlive());
        if (settings.getReceiveBufferSize() > 0) {
            socket.setReceiveBufferSize(settings.getReceiveBufferSize());
        }
        if (settings.getSendBufferSize() > 0) {
            socket.setSendBufferSize(settings.getSendBufferSize());
        }
        if (sslSettings.isEnabled()) {
            if (!(socket instanceof SSLSocket)) {
                throw new MongoInternalException("SSL is enabled but the socket is not an instance of javax.net.ssl.SSLSocket");
            }
            if (!sslSettings.isInvalidHostNameAllowed()) {
                SSLSocket sslSocket = (SSLSocket) socket;
                sslSocket.setSSLParameters(enableHostNameVerification(sslSocket.getSSLParameters()));
            }
        }
        socket.connect(address.getSocketAddress(), settings.getConnectTimeout(MILLISECONDS));
    }

    private SocketStreamHelper() {
    }
}

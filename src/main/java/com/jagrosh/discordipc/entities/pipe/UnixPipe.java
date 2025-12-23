/*
 * Copyright 2017 John Grosh (john.a.grosh@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jagrosh.discordipc.entities.pipe;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.entities.Callback;
import com.jagrosh.discordipc.entities.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.util.HashMap;

public class UnixPipe extends Pipe
{

    private static final Logger LOGGER = LoggerFactory.getLogger(UnixPipe.class);
    private final SocketChannel channel;

    UnixPipe(IPCClient ipcClient, HashMap<String, Callback> callbacks, String location) throws IOException
    {
        super(ipcClient, callbacks);

        UnixDomainSocketAddress address = UnixDomainSocketAddress.of(Path.of(location));
        channel = SocketChannel.open(address);
        channel.configureBlocking(true);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public Packet read() throws IOException, JsonIOException
    {
        if(status==PipeStatus.DISCONNECTED)
            throw new IOException("Disconnected!");

        if(status==PipeStatus.CLOSED)
            return new Packet(Packet.OpCode.CLOSE, null);

        // Read the op and length. Both are signed ints
        ByteBuffer header = ByteBuffer.allocate(8);
        readFully(header);
        header.flip();
        int op = Integer.reverseBytes(header.getInt());
        int length = Integer.reverseBytes(header.getInt());

        ByteBuffer payload = ByteBuffer.allocate(length);
        readFully(payload);
        payload.flip();
        byte[] data = new byte[length];
        payload.get(data);

        Packet.OpCode opcode = Packet.OpCode.values()[op];
        Packet p = new Packet(opcode, JsonParser.parseString(new String(data)));
        LOGGER.debug("Received packet: {}", p);
        if(listener != null)
            listener.onPacketReceived(ipcClient, p);
        return p;
    }

    @Override
    public void write(byte[] b) throws IOException
    {
        ByteBuffer buffer = ByteBuffer.wrap(b);
        while(buffer.hasRemaining())
        {
            if(channel.write(buffer) == -1)
                throw new IOException("Disconnected!");
        }
    }

    @Override
    public void close() throws IOException
    {
        LOGGER.debug("Closing IPC pipe...");
        send(Packet.OpCode.CLOSE, new JsonObject(), null);
        status = PipeStatus.CLOSED;
        channel.close();
    }

    private void readFully(ByteBuffer buffer) throws IOException
    {
        while(buffer.hasRemaining())
        {
            int read = channel.read(buffer);
            if(read == -1)
            {
                status = PipeStatus.DISCONNECTED;
                throw new IOException("Disconnected!");
            }
        }
    }
}

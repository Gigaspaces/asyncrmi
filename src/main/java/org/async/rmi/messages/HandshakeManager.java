package org.async.rmi.messages;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.nio.ByteOrder;
import java.security.NoSuchAlgorithmException;

import static io.netty.buffer.Unpooled.buffer;

/**
 * Created by Barak Bar Orion
 * 12/4/14.
 */
public class HandshakeManager {
    private static final byte [] HANDSHAKE_PREFIX = new byte[]{97, 115, 121, 110, 99, 114, 109, 105};
    private byte challenge;


    /**
     * Called by the client to initiate protocol handshake.
     *
     * @return the handshake initial message of the server.
     */
    public ByteBuf handshakeRequest() {
        challenge = (byte) (Math.random() * Byte.MAX_VALUE);
        ByteBuf buffer = buffer(13);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.writeBytes(HANDSHAKE_PREFIX);
        buffer.writeByte(challenge);
        buffer.writeInt(0);
        return buffer;
    }

    /**
     * Called by the Server to verify client handshake initial message.
     *
     * @param request the client message
     * @param filters stack of network filters encoded as long.
     * @return The message that should be return to the client (the response).
     * @throws IOException if the verification fail.
     */
    public ByteBuf verifyRequest(ByteBuf request, int filters) throws NoSuchAlgorithmException, IOException {
        request.order(ByteOrder.BIG_ENDIAN);
        if (!isPrefixBytes(request.slice(0, 8))) {
            throw new IOException("Invalid protocol handshake request, prefix is not match");
        }
        int challenge = request.getByte(8);

        ByteBuf reply = buffer(13);
        reply.order(ByteOrder.BIG_ENDIAN);
        reply.writeBytes(HANDSHAKE_PREFIX);
        reply.writeByte((byte) (challenge + 1));
        reply.writeInt(filters);
        return reply;
    }

    private boolean isPrefixBytes(ByteBuf bytes) {
        for(int i = 0; i < 8; ++i){
            if(HANDSHAKE_PREFIX[i] != bytes.getByte(i)){
                return false;
            }
        }
        return true;
    }

    /**
     * Called by the client to verify that the server response is according to the protocol.
     *
     * @param response the response from the server.
     * @return required network filters encoded as int.
     */
    public int verifyResponse(ByteBuf response) throws IOException, NoSuchAlgorithmException {
        response.order(ByteOrder.BIG_ENDIAN);
        if (!isPrefixBytes(response.slice(0, 8))) {
            throw new IOException("Invalid protocol handshake request, prefix is not match");
        }
        byte challenge = response.getByte(8);
        if (challenge != (byte) (this.challenge + 1)) {
            throw new IOException("Invalid protocol handshake response, number is not match");

        }
        return response.getInt(9);
    }
}

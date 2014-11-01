package org.async.rmi.modules;

import io.netty.channel.EventLoopGroup;
import org.async.rmi.Configuration;
import org.async.rmi.client.RemoteRef;
import org.async.rmi.messages.Response;

import java.io.Closeable;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Barak Bar Orion
 * 12/10/14.
 */
public interface Transport extends Closeable {

    RemoteRef export(Remote impl, Class[] remoteInterfaces, Configuration configuration) throws UnknownHostException, InterruptedException;

    void addResponseFuture(long requestId, CompletableFuture<Response> responseFuture);

    void handleResponse(Response response);

    EventLoopGroup getClientEventLoopGroup();

    void listen() throws InterruptedException;

}
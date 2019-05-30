package com.cloudhopper.smpp.channel;

/*
 * #%L
 * ch-smpp
 * %%
 * Copyright (C) 2009 - 2015 Cloudhopper by Twitter
 * %%
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
 * #L%
 */

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.channel.ChannelHandler.Sharable;

/**
 * The default handler used just during a "connect" for a Channel when attempting
 * to bind into an SMSC.  The reason is that some handler must be added to create
 * a channel and then replaced before the actual "bind" is attempted.  This
 * handler basically does nothing.  Until a "bind" request is received by the SMSC,
 * nothing should actually be received so its safe to have a handler that does
 * nothing.
 *
 * @author joelauer (twitter: @jjlauer or <a href="http://twitter.com/jjlauer" target=window>http://twitter.com/jjlauer</a>)
 */
@Sharable
public class SmppClientConnector extends LoggingChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(SmppClientConnector.class);

    private ChannelGroup channels;

    public SmppClientConnector(ChannelGroup channels) {
        super(SmppClientConnector.class);
        this.channels = channels;
    }

    //TODO is channelActive is the same as channelConnected?
    // @trustin: Yes for client channels or accepted channels. For server channels, it is same with channelBound.
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // called every time a new channel connects
        channels.add(ctx.channel());
        super.channelActive(ctx);
    }

    //TODO is channelInactive is the same as channelDisconnected?
    // @trustin: Yes for client channels or accepted channels. For server channels, it is same with channelUnbound.
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // called every time a channel disconnects
        channels.remove(ctx.channel());
        super.channelInactive(ctx);
    }

    /**
     * Invoked when an exception was raised by an I/O thread or an upstream handler.
     * NOTE: Not implementing this causes annoying log statements to STDERR
     * TODO: do we need this anymore? This is the default impl.
     * /@trustin: Not in this case, because your handler merely forwards the exception to the next handler.
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // the client smpp implementation relies on this to catch errors upstream
        // however, during a connect sequence, we don't have any upstream handlers
        // yet and the framework logged the exceptions to STDERR causing issues
        // on the console.  So, we'll implement a default handling of it here
        // where we just pass it further upstream and basically discard it
        super.exceptionCaught(ctx, cause);
    }
}

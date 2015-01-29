/*
 * Copyright (C) 2013 Lekab Communication Systems AB
 */
package com.cloudhopper.smpp.channel;

import com.cloudhopper.smpp.pdu.Pdu;
import com.cloudhopper.smpp.transcoder.PduTranscoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andreas Asplund <a href="mailto:andreas.asplund@lekab.com">andreas.asplund@lekab.com</a>
 */
public class SmppSessionPduEncoder extends MessageToByteEncoder<Pdu> {
    private static final Logger logger = LoggerFactory.getLogger(SmppSessionPduDecoder.class);

    private final PduTranscoder transcoder;

    public SmppSessionPduEncoder(PduTranscoder transcoder) {
        this.transcoder = transcoder;
    }

    public SmppSessionPduEncoder(Class<? extends Pdu> outboundMessageType, PduTranscoder transcoder) {
        super(outboundMessageType);
        this.transcoder = transcoder;
    }

    public SmppSessionPduEncoder(Class<? extends Pdu> outboundMessageType, boolean preferDirect, PduTranscoder transcoder) {
        super(outboundMessageType, preferDirect);
        this.transcoder = transcoder;
    }

    public SmppSessionPduEncoder(boolean preferDirect, PduTranscoder transcoder) {
        super(preferDirect);
        this.transcoder = transcoder;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Pdu msg, ByteBuf out) throws Exception {
        out.writeBytes(transcoder.encode(msg));
    }
}

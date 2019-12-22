package hensg.netty.discard;

import io.netty.buffer.ByteBuf;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * Handles a server-side channel.
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private LinkedList<Channel> channels = new LinkedList();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        channels.add(ctx.channel());

        ByteBuf in = (ByteBuf) msg;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("Message received: ");
            while (in.isReadable()) {
                sb.append((char)in.readByte());
            }
            logger.info(sb.toString());
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}


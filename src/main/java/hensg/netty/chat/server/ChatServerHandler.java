package hensg.netty.chat.server;

import hensg.netty.chat.ChatMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatServerHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LogManager.getLogger();

    private final static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    int indexKeeper = 0;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.info("New channel connection: " + ctx.channel());
        channels.add(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object in) throws Exception {
        ChatMessage msg = (ChatMessage)in;
        logger.debug("Received message {} from client {}", msg, ctx.channel().remoteAddress());

        assert indexKeeper++ == Integer.parseInt(msg.getMessage());

        for (Channel ch : channels) {
            if (ch != ctx.channel()) {
                ch.writeAndFlush(msg);
            }
        }

        if ("exit".equals(msg)) {
           ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

package hensg.netty.chat.client;

import hensg.netty.chat.ChatMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatClientHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LogManager.getLogger();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) {
        ChatMessage msg = (ChatMessage)obj;
        logger.info("{} says: {}", msg.getOriginUser(), msg.getMessage());
    }
}

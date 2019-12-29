package hensg.netty.chat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class ChatMessageEncoder extends MessageToByteEncoder<ChatMessage> {

    protected void encode(ChannelHandlerContext ctx, ChatMessage msg, ByteBuf out) throws Exception {
        ByteBuf msgOriginUser = ByteBufUtil
                .encodeString(ctx.alloc(), CharBuffer.wrap(msg.getOriginUser()), Charset.defaultCharset());
        ByteBuf msgText = ByteBufUtil
                .encodeString(ctx.alloc(), CharBuffer.wrap(msg.getMessage()), Charset.defaultCharset());

        int sizeOriginUser = msgOriginUser.readableBytes();
        int sizeText = msgText.readableBytes();

        try {
            out.writeInt(sizeOriginUser);
            out.writeInt(sizeText);

            out.writeBytes(msgOriginUser);
            out.writeBytes(msgText);
        } finally {
            msgOriginUser.release();
            msgText.release();
        }

    }
}

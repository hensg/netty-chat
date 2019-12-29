package hensg.netty.chat;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class ChatMessageDecoder extends ByteToMessageDecoder {

    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) {
        if (buf.readableBytes() < 8)
            return;
        buf.markReaderIndex();

        int sizeOriginUser = buf.readInt();
        int sizeText = buf.readInt();

        if (buf.readableBytes() < sizeOriginUser + sizeText + 8) {
            buf.resetReaderIndex(); //buff not ready yet
            return;
        }

        ByteBuf originUser = buf.readBytes(sizeOriginUser);
        ByteBuf text = buf.readBytes(sizeText);

        try {
            ChatMessage msg = new ChatMessage(
                    text.readCharSequence(sizeText, Charset.defaultCharset()).toString(),
                    originUser.readCharSequence(sizeOriginUser, Charset.defaultCharset()).toString()
            );

            out.add(msg);
        } finally {
            text.release();
            originUser.release();
        }
    }
}

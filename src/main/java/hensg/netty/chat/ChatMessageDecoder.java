package hensg.netty.chat;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class ChatMessageDecoder extends ByteToMessageDecoder {

    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) {
        if (buf.readableBytes() < 4)
            return;
        buf.markReaderIndex();

        int sizeOriginUser = buf.readInt();
        if (buf.readableBytes() < sizeOriginUser + 4) {// 4: size of text
            buf.resetReaderIndex(); //buff not ready yet
            return;
        }
        ByteBuf originUser = buf.readBytes(sizeOriginUser);

        int sizeText = buf.readInt();
        if (buf.readableBytes() < sizeText) {
            buf.resetReaderIndex(); //buff not ready yet
            originUser.release();
            return;
        }
        ByteBuf text = buf.readBytes(sizeText);

        try {
            ChatMessage msg = new ChatMessage(
                    text.readCharSequence(sizeText, Charset.defaultCharset()).toString(),
                    originUser.readCharSequence(sizeOriginUser, Charset.defaultCharset()).toString()
            );

            out.add(msg);
        } finally {
            originUser.release();
            text.release();
        }
    }
}

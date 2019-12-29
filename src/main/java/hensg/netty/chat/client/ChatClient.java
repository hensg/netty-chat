package hensg.netty.chat.client;

import hensg.netty.chat.ChatMessage;
import hensg.netty.chat.ChatMessageDecoder;
import hensg.netty.chat.ChatMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

public class ChatClient {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws InterruptedException, IOException {
        String host = "localhost";

        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new ChatMessageDecoder());
                    pipeline.addLast(new ChatMessageEncoder());
                    pipeline.addLast(new ChatClientHandler());
                }
            })
            .option(ChannelOption.SO_KEEPALIVE, true);

            final UUID uuid = UUID.randomUUID();
            // Start the client.
            ChannelFuture client = b.connect(host, port).sync();
            client.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    logger.info("{} connected to server", uuid.toString());
                }
            });

            Channel ch = client.channel();

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while (!"exit".equals(line = br.readLine())) {
                ch.writeAndFlush(new ChatMessage(line, uuid.toString()));
            }
            // Wait until the connection is closed.
            ch.closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

}

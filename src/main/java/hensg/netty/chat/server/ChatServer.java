package hensg.netty.chat.server;

import hensg.netty.chat.ChatMessageDecoder;
import hensg.netty.chat.ChatMessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatServer {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws InterruptedException {
        int port = args.length == 1 ? Integer.parseInt(args[0]) : 8080;

        EventLoopGroup connectionGroup = new NioEventLoopGroup();
        EventLoopGroup channelGroup = new NioEventLoopGroup();
        ServerBootstrap server = new ServerBootstrap();
        try {
            server
                    .group(connectionGroup, channelGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast(new ChatMessageDecoder())
                                    .addLast(new ChatMessageEncoder())
                                    .addLast(new ChatServerHandler());
                        }
                    });

            logger.info("Starting server at port: " + port);
            // Bind and start to accept incoming connections.
            ChannelFuture f = server.bind(port).sync().addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    logger.info("Server started.");
                }
            });

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            connectionGroup.shutdownGracefully();
            channelGroup.shutdownGracefully();
        }
    }
}

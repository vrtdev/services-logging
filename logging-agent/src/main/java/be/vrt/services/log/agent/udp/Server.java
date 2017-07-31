package be.vrt.services.log.agent.udp;

import be.vrt.services.log.agent.log.LogBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private static final int RCVBUF_BYTES = 4096;

    private DefaultThreadFactory acceptFactory = new DefaultThreadFactory("accept");
    private NioEventLoopGroup acceptGroup = new NioEventLoopGroup(1, acceptFactory);

    private int port;
    private LogBuilder logBuilder;

    public Server(int port, LogBuilder logBuilder) {
        this.port = port;
        this.logBuilder = logBuilder;
    }

    public void run() throws Exception {
        new Bootstrap()
                .group(acceptGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_RCVBUF, RCVBUF_BYTES)
                .handler(new Handler())
                .bind(port).sync();
    }

    public void shutdown() {
        LOGGER.info("Shutting down.");
        acceptGroup.shutdownGracefully();
    }

    private class Handler extends SimpleChannelInboundHandler<DatagramPacket> {

        @Override
        public void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
            if (msg != null) {
                String message = msg.content().toString(CharsetUtil.UTF_8);
                logBuilder.addLog(message);
            }
        }

    }
}

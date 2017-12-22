package com.demo.java.io.telnet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * Simplistic telnet server.
 */
public final class TelnetServer {

    static final boolean SSL = System.getProperty("ssl") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", SSL? "8992" : "8023"));

    public static void main(String[] args) throws Exception {
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }
        //配置服务端的NIO线程组
        //NioEventLoopGroup类是个线程组，包含一组NIO线程，用于网络事件处理
        //实际上它就是Reactor线程组
        //创建2个线程组
        //一个用于服务端接收客户端的连接
        //一个用于进行SocketChannel的网络读写
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
        	//ServerBootstrap类，是启动NIO服务器的辅助启动类
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             //NIO socket管道
             .channel(NioServerSocketChannel.class)
             //服务器请求处理线程全满时，用于临时存放请求队列的最大长度
             .option(ChannelOption.SO_BACKLOG, 2)
             //建立连接后，2个小时没有数据传输
             .option(ChannelOption.SO_KEEPALIVE,true)
             .handler(new LoggingHandler(LogLevel.INFO))
             //处理逻辑
             .childHandler(new TelnetServerInitializer(sslCtx));
              
            b.bind(PORT).sync().channel().closeFuture().sync();
//            ChannelFuture channelFuture = b.bind(PORT).sync();
//            channelFuture.channel().close().sync();
        } finally {
        	//释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
package com.demo.java.io.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class SimpleServer {
	private int port;
	public SimpleServer(int port){
		this.port = port;
	}
	public void run() throws Exception{
		//接收客户端连接
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		//处理客户端连接
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try{			
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>(){

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new SimpleServerHandler());
				}
				
			})
			.option(ChannelOption.SO_BACKLOG, 128)
			.childOption(ChannelOption.SO_KEEPALIVE, true);
			//绑定端口开始接收连接
			ChannelFuture f = b.bind(port).sync();
			//等待服务器socket关闭
			f.channel().closeFuture().sync();			
		}finally{
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws Exception{
		new SimpleServer(9999).run();
	}
}

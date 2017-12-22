package com.demo.java.io.simple;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SimpleClientHandler extends ChannelInboundHandlerAdapter {
    //连接成功后向server发送消息
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		String msg = "客户端向服务端发送消息$$$";
		ByteBuf encoded = ctx.alloc().buffer();
		encoded.writeBytes(msg.getBytes());
		ctx.write(encoded);
		ctx.flush();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf result = (ByteBuf)msg;
		byte[] result1 = new byte[result.readableBytes()];
		result.readBytes(result1);
		System.out.println("接收服务端消息:"+new String(result1));
		result.release();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}

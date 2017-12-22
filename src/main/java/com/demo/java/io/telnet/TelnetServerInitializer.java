package com.demo.java.io.telnet;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;

/**
 * Creates a newly configured {@link ChannelPipeline} for a new channel.
 */
public class TelnetServerInitializer extends ChannelInitializer<SocketChannel> {

    private static final StringDecoder DECODER = new StringDecoder();
    private static final StringEncoder ENCODER = new StringEncoder();

    private static final TelnetServerHandler SERVER_HANDLER = new TelnetServerHandler();

    private final SslContext sslCtx;

    public TelnetServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
    	try{
            ChannelPipeline pipeline = ch.pipeline();

            if (sslCtx != null) {
                pipeline.addLast(sslCtx.newHandler(ch.alloc()));
            }

            // Add the text line codec combination first,
            pipeline.addLast(new DelimiterBasedFrameDecoder(10, Delimiters.lineDelimiter()));
            // the encoder and decoder are static as these are sharable
            //编码和解码
            pipeline.addLast(DECODER);
            pipeline.addLast(ENCODER);

            // and then business logic. 
            //业务逻辑处理
            pipeline.addLast(SERVER_HANDLER);	
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
}
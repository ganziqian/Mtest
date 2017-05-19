package com.zwg.socketdemo.netty;

/**
 * Created by Administrator on 2016/11/29.
 */
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class MyClientInitializer extends ChannelInitializer<SocketChannel> {
    private Context context;

    public MyClientInitializer(Context ctx) {
        this.context = ctx;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        /**
         * 这个地方的必须和服务端对应上。否则无法正常解码和编码
         */
        // pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192,
        // Delimiters.lineDelimiter())); //这个是解决沾包的问题，但是我发现加上这句话，就读取不到返回值，不知道为什么
        pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
        pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
        // 客户端的逻辑
        pipeline.addLast("handler", new MyClientHandler(context));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        Log.e("222222","---channelRead--- msg=" + msg);
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        Log.e("222222","---channelReadComplete---");
        super.channelReadComplete(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Log.e("MyClientInitializer", "---channelActive---");
        super.channelActive(ctx);
    }
}
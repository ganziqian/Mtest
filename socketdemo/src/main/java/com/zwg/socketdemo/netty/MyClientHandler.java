package com.zwg.socketdemo.netty;

/**
 * Created by Administrator on 2016/11/29.
 */
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MyClientHandler extends SimpleChannelInboundHandler<String> {
    private Context context;

    public MyClientHandler(Context context) {
        this.context = context;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg)
            throws Exception {
        Log.e("MyHelloClientHandler", "channelRead0->msg=" + msg);

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Log.e("chel_activity","Client active");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Log.e("chel_inactivity","Client close ");
        super.channelInactive(ctx);
    }

}
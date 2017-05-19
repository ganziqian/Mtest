package com.zwg.socketdemo.interfaces;

import com.im.client.callback.ClientCallback;
import com.im.client.struct.IMMessage;

/**
 * Created by Administrator on 2017/2/13.
 */

public abstract class LoginInterface implements ClientCallback {




    @Override
    public abstract void process(IMMessage imMessage) ;

    @Override
    public void resetCount() {

    }
}

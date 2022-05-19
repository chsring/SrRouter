package com.srwing.srrouter;

import android.app.Application;

import com.srwing.router.SrRouter;

/**
 * Description:
 * Created by srwing
 * Date: 2022/5/19
 * Email: 694177407@qq.com
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SrRouter.getInstance().init(this);
    }
}

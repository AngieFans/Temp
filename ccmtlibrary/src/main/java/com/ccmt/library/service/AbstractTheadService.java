package com.ccmt.library.service;

import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.ccmt.library.global.Global;
import com.ccmt.library.util.ThreadManager;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author myx
 *         by 2017-06-20
 */
public abstract class AbstractTheadService extends AbstractService {

    /**
     * 服务的线程是否结束运行
     */
    protected AtomicBoolean mIsExit = new AtomicBoolean(false);

    protected Runnable mRunnable;

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsExit.set(true);
    }

    public static void stopThread(Class<? extends AbstractService> cla) {
        AbstractService service = Global.allRunningServices.get(cla);
        if (service != null) {
            if (service instanceof AbstractTheadService) {
                ((AbstractTheadService) service).mIsExit.set(true);
            }
        }
    }

    @Override
    protected void doTask(Intent intent) {
        mIsExit.set(false);

        // 启动服务后,只有线程结束,mIsDoTaskable才被赋值为true,外部再启动服务时才能往下运行.
        // 否则外部无论启动多少次服务,都只有第1次启动时才会往下运行.
        mIsDoTaskable = null;

        executeAsyncTaskBefore();

        ThreadManager.executeAsyncTask(new Runnable() {
            @Override
            public void run() {
                Runnable runnable;
                while (true) {
                    SystemClock.sleep(getSleepTime());
                    if (mIsExit.get()) {
                        ThreadManager.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("MyLog", getClass() + "服务的线程结束运行");
                                mIsDoTaskable = true;
                            }
                        });
                        return;
                    }
                    runnable = getRunnable();
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            }
        });
    }

    /**
     * 子类要在子线程运行的任务
     *
     * @return
     */
    @SuppressWarnings("JavaDoc")
    protected abstract Runnable getRunnable();

    /**
     * 获取子线程每次运行1次任务后的休眠时间
     *
     * @return
     */
    @SuppressWarnings("JavaDoc")
    protected abstract long getSleepTime();

    /**
     * 该方法会在主线程运行
     */
    protected abstract void executeAsyncTaskBefore();

}

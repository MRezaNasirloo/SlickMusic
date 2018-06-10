package com.mrezanasirloo.slickmusic.presentation;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.mrezanasirloo.slickmusic.presentation.di.ComponentApp;
import com.mrezanasirloo.slickmusic.presentation.di.ComponentMain;
import com.mrezanasirloo.slickmusic.presentation.di.DaggerComponentApp;
import com.mrezanasirloo.slickmusic.presentation.di.ModuleMain;
import com.mrezanasirloo.slickmusic.presentation.di.ModuleApp;
import com.mrezanasirloo.slickmusic.presentation.di.ModuleScheduler;
import com.xwray.groupie.GroupAdapter;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.Callable;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * @author : M.Reza.Nasirloo@gmail.com
 *         Created on: 2018-06-09
 */
public class App extends Application {
    public static final String TAG = App.class.getSimpleName();

    private static App app;
    private ComponentApp componentApp;
    private ComponentMain componentMain;

    @Override
    public void onCreate() {
        super.onCreate();
        new GroupAdapter();
        app = this;
        componentApp = prepareDi().build();

        Observable.just(1).
                doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {

                    }
                })
                .doOnEach(new Consumer<Notification<Integer>>() {
                    @Override
                    public void accept(Notification<Integer> integerNotification) throws Exception {

                    }
                }).
        subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {

            }
        });

        RxJavaPlugins.setErrorHandler(e -> {
            if (e instanceof UndeliverableException) {
                e = e.getCause();
            }
            if ((e instanceof IOException) || (e instanceof SocketException)) {
                // fine, irrelevant network problem or API that throws on cancellation
                return;
            }
            if (e instanceof InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
                return;
            }
            if ((e instanceof NullPointerException) || (e instanceof IllegalArgumentException)) {
                // that's likely a bug in the application
                Thread.currentThread().getUncaughtExceptionHandler()
                        .uncaughtException(Thread.currentThread(), e);
                return;
            }
            if (e instanceof IllegalStateException) {
                // that's a bug in RxJava or in a custom operator
                Thread.currentThread().getUncaughtExceptionHandler()
                        .uncaughtException(Thread.currentThread(), e);
                return;
            }
            Log.w(TAG, "Undeliverable exception received, not sure what to do", e);
        });
    }
    @NonNull
    protected DaggerComponentApp.Builder prepareDi() {
        return DaggerComponentApp.builder()
                .moduleApp(new ModuleApp(this))
                // .moduleDatabase(new ModuleDatabase())
//                .moduleNetwork(new ModuleNetwork())
                .moduleScheduler(new ModuleScheduler());
    }

    public static ComponentMain componentMain() {
        if (app.componentMain == null) {
            app.componentMain = app.componentMainBuilder().build();
        }
        return app.componentMain;
    }

    protected ComponentMain.Builder componentMainBuilder() {
        if (componentApp == null) {
            componentApp = prepareDi().build();
        }
        return componentApp.plus().mainModule(new ModuleMain());
    }

    public static void disposeComponentMain() {
        app.componentMain = null;
    }

    public static void disposeComponentApp() {
        app.componentApp = null;
    }
}

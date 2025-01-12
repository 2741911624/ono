package moe.ono.startup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import moe.ono.loader.hookapi.IHookBridge;
import moe.ono.loader.hookapi.ILoaderService;

public class StartupInfo {

    private StartupInfo() {
        throw new AssertionError("No instance for you!");
    }

    private static String modulePath;

    private static Boolean isInitMethod;
    private static Application hostApp;
    @SuppressLint("StaticFieldLeak")
    private static Activity splashActivity;
    private static ILoaderService loaderService;
    private static IHookBridge hookBridge;

    private static Boolean inHostProcess = null;


    public static String getModulePath() {
        if (modulePath == null) {
            throw new IllegalStateException("Module path is null");
        }
        return modulePath;
    }

    public static Application getHostApp() {
        if (hostApp == null) {
            throw new IllegalStateException("hostApp is null");
        }
        return hostApp;
    }


    public static Activity getSplashActivity() {
        if (splashActivity == null) {
            throw new IllegalStateException("SplashActivity is null");
        }
        return splashActivity;
    }

    public static void setHostApp(Application hostApp) {
        Objects.requireNonNull(hostApp);
        StartupInfo.hostApp = hostApp;
    }

    public static void setSplashActivity(Activity activity) {
        Objects.requireNonNull(activity);
        StartupInfo.splashActivity = activity;
    }


    public static void setModulePath(@NonNull String modulePath) {
        Objects.requireNonNull(modulePath);
        StartupInfo.modulePath = modulePath;
    }


    public static Boolean isInitMethod(){
        if (isInitMethod == null){
            return false;
        }
        return isInitMethod;
    }


    public static void setIsInitMethod(Boolean b){
        Objects.requireNonNull(b);
        StartupInfo.isInitMethod = b;
    }

    @NonNull
    public static ILoaderService getLoaderService() {
        return loaderService;
    }

    public static void setLoaderService(@NonNull ILoaderService loaderService) {
        Objects.requireNonNull(loaderService);
        StartupInfo.loaderService = loaderService;
    }

    public static boolean isInHostProcess() {
        if (inHostProcess == null) {
            throw new IllegalStateException("Host process status is not initialized");
        }
        return inHostProcess;
    }

    public static void setInHostProcess(boolean inHostProcess) {
        if (StartupInfo.inHostProcess != null) {
            throw new IllegalStateException("Host process status is already initialized");
        }
        StartupInfo.inHostProcess = inHostProcess;
    }

    @Nullable
    public static IHookBridge getHookBridge() {
        return hookBridge;
    }

    public static void setHookBridge(@Nullable IHookBridge hookBridge) {
        StartupInfo.hookBridge = hookBridge;
    }


}

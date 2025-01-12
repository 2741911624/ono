package moe.ono.util.hookstatus;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.HashMap;

import io.github.libxposed.service.XposedService;
import io.github.libxposed.service.XposedServiceHelper;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlowKt;
import moe.ono.BuildConfig;
import moe.ono.LoaderExtensionHelper;
import moe.ono.R;
import moe.ono.util.HostInfo;
import moe.ono.util.Logger;
import moe.ono.util.SyncUtils;

/**
 * This class is only intended to be used in module process, not in host process.
 */
public class HookStatus {

    private HookStatus() {
    }

    private static boolean sExpCpCalled = false;
    private static boolean sExpCpResult = false;
    private static final MutableStateFlow<XposedService> sXposedService = StateFlowKt.MutableStateFlow(null);
    private static boolean sXposedServiceListenerRegistered = false;
    private static final XposedServiceHelper.OnServiceListener sXposedServiceListener = new XposedServiceHelper.OnServiceListener() {

        @Override
        public void onServiceBind(@NonNull XposedService service) {
            Logger.d("on XPOSED ServiceBind");
            sXposedService.setValue(service);
        }

        @Override
        public void onServiceDied(@NonNull XposedService service) {
            Logger.d("on XPOSED ServiceDied");
            sXposedService.setValue(null);
        }

    };

    public enum HookType {
        /**
         * No hook.
         */
        NONE,
        /**
         * Taichi, BugHook(not implemented), etc.
         */
        APP_PATCH,
        /**
         * Legacy Xposed, EdXposed, LSPosed, Dreamland, etc.
         */
        ZYGOTE,
    }

    @Nullable
    public static String getZygoteHookProvider() {
        return HookStatusImpl.sZygoteHookProvider;
    }

    public static boolean isLsposedDexObfsEnabled() {
        return HookStatusImpl.sIsLsposedDexObfsEnabled;
    }

    public static boolean isZygoteHookMode() {
        return HookStatusImpl.sZygoteHookMode;
    }

    public static boolean isLegacyXposed() {
        try {
            ClassLoader.getSystemClassLoader().loadClass("de.robv.android.xposed.XposedBridge");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isElderDriverXposed() {
        return new File("/system/framework/edxp.jar").exists();
    }

    public static boolean callTaichiContentProvider(@NonNull Context context) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            Uri uri = Uri.parse("content://me.weishu.exposed.CP/");
            Bundle result = new Bundle();
            try {
                result = contentResolver.call(uri, "active", null, null);
            } catch (RuntimeException e) {
                // TaiChi is killed, try invoke
                try {
                    Intent intent = new Intent("me.weishu.exp.ACTION_ACTIVE");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {
                    return false;
                }
            }
            if (result == null) {
                result = contentResolver.call(uri, "active", null, null);
            }
            if (result == null) {
                return false;
            }
            return result.getBoolean("active", false);
        } catch (Exception e) {
            return false;
        }
    }

    public static void init(@NonNull Context context) {
        if (context.getPackageName().equals(BuildConfig.APPLICATION_ID)) {
            if (!sXposedServiceListenerRegistered) {
                XposedServiceHelper.registerListener(sXposedServiceListener);
                sXposedServiceListenerRegistered = true;
            }
            SyncUtils.async(() -> {
                sExpCpCalled = callTaichiContentProvider(context);
                sExpCpResult = sExpCpCalled;
            });
        } else {
            // in host process???
            try {
                initHookStatusImplInHostProcess();
            } catch (LinkageError ignored) {
            }
        }
    }

    @NonNull
    public static MutableStateFlow<XposedService> getXposedService() {
        return sXposedService;
    }

    public static HookType getHookType() {
        if (isZygoteHookMode()) {
            return HookType.ZYGOTE;
        }
        return sExpCpResult ? HookType.APP_PATCH : HookType.NONE;
    }

    private static void initHookStatusImplInHostProcess() throws LinkageError {
        Class<?> xposedClass = LoaderExtensionHelper.getXposedBridgeClass();
        boolean dexObfsEnabled = false;
        if (xposedClass != null) {
            dexObfsEnabled = !"de.robv.android.xposed.XposedBridge".equals(xposedClass.getName());
        }
        String hookProvider = null;
        if (dexObfsEnabled) {
            HookStatusImpl.sIsLsposedDexObfsEnabled = true;
            hookProvider = "LSPosed";
        } else {
            String bridgeTag = null;
            if (xposedClass != null) {
                try {
                    bridgeTag = (String) xposedClass.getDeclaredField("TAG").get(null);
                } catch (ReflectiveOperationException ignored) {
                }
            }
            if (bridgeTag != null) {
                if (bridgeTag.startsWith("LSPosed")) {
                    hookProvider = "LSPosed";
                } else if (bridgeTag.startsWith("EdXposed")) {
                    hookProvider = "EdXposed";
                } else if (bridgeTag.startsWith("PineXposed")) {
                    hookProvider = "Dreamland";
                }
            }
        }
        if (hookProvider != null) {
            HookStatusImpl.sZygoteHookProvider = hookProvider;
        }
    }

    public static String getHookProviderNameForLegacyApi() {
        if (isZygoteHookMode()) {
            String name = getZygoteHookProvider();
            if (name != null) {
                return name;
            }
            if (isLegacyXposed()) {
                return "Legacy Xposed";
            }
            if (isElderDriverXposed()) {
                return "EdXposed";
            }
            return "Unknown(Zygote)";
        }
        if (sExpCpResult) {
            return "Taichi";
        }
        return "None";
    }

    public static boolean isTaiChiInstalled(@NonNull Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            pm.getPackageInfo("me.weishu.exp", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isModuleEnabled() {
        return getHookType() != HookType.NONE;
    }

    public static HashMap<String, String> getHostABI() {
        CharSequence[] scope = HostInfo.getApplication().getResources().getTextArray(R.array.xposedscope);
        HashMap<String, String> result = new HashMap<>(4);
        for (CharSequence s : scope) {
            String abi = AbiUtils.getApplicationActiveAbi(s.toString());
            if (abi != null) {
                result.put(s.toString(), abi);
            }
        }
        return result;
    }
}

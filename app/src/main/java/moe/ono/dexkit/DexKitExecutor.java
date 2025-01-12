package moe.ono.dexkit;

import org.luckypray.dexkit.DexKitBridge;

import moe.ono.util.Logger;

public class DexKitExecutor {
    private final String apkPath;
    private final ClassLoader classLoader;

    public DexKitExecutor(String apkPath, ClassLoader classLoader) {
        this.apkPath = apkPath;
        this.classLoader = classLoader;
    }

    public void execute(DexKitTask task) {
        try (DexKitBridge bridge = DexKitBridge.create(apkPath)) {
            task.execute(bridge, classLoader);
        } catch (Exception e) {
            Logger.e("DexKitExecutor", e);
        }
    }

}

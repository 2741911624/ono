package moe.ono.dexkit;

import org.luckypray.dexkit.DexKitBridge;

@FunctionalInterface
public interface DexKitTask {
    void execute(DexKitBridge bridge, ClassLoader classLoader) throws Exception;
}

package moe.ono.dexkit;

import org.luckypray.dexkit.DexKitBridge;
import org.luckypray.dexkit.query.FindMethod;
import org.luckypray.dexkit.query.matchers.MethodMatcher;
import org.luckypray.dexkit.result.MethodData;

import java.lang.reflect.Method;

public class DexKitFinder implements AutoCloseable {
    private final DexKitBridge bridge;
    private final ClassLoader classLoader;

    public DexKitFinder(String apkPath, ClassLoader classLoader) throws Exception {
        this.bridge = DexKitBridge.create(apkPath);
        this.classLoader = classLoader;
    }

    public Method findMethod(MethodMatcher matcher) throws Exception {
        MethodData methodData = bridge.findMethod(
                FindMethod.create().matcher(matcher)
        ).single();
        return methodData.getMethodInstance(classLoader);
    }

    @Override
    public void close() {
        if (bridge != null) {
            bridge.close();
        }
    }
}

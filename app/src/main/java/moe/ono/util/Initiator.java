package moe.ono.util;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;

import mqq.app.AppRuntime;

public class Initiator {
    private static ClassLoader sHostClassLoader;
    private static ClassLoader sPluginParentClassLoader;
    private static Class<?> kQQAppInterface = null;
    private static final HashMap<String, Class<?>> sClassCache = new HashMap<>(16);


    private Initiator() {
        throw new AssertionError("No instance for you!");
    }

    public static void init(ClassLoader classLoader) {
        sHostClassLoader = classLoader;
        sPluginParentClassLoader = Initiator.class.getClassLoader();
    }

    public static ClassLoader getPluginClassLoader() {
        return Initiator.class.getClassLoader();
    }

    public static ClassLoader getHostClassLoader() {
        return sHostClassLoader;
    }

    /**
     * Load a class, if the class is not found, null will be returned.
     *
     * @param className The class name.
     * @return The class, or null if not found.
     */
    @Nullable
    public static Class<?> load(String className) {
        if (sPluginParentClassLoader == null || className == null || className.isEmpty()) {
            return null;
        }
        if (className.endsWith(";") || className.contains("/")) {
            className = className.replace('/', '.');
            if (className.endsWith(";")) {
                if (className.charAt(0) == 'L') {
                    className = className.substring(1, className.length() - 1);
                } else {
                    className = className.substring(0, className.length() - 1);
                }
            }
        }
        try {
            return sHostClassLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Nullable
    public static Class<?> findClassWithSynthetics(@NonNull String className1, @NonNull String className2) {
        Class<?> clazz = load(className1);
        if (clazz != null) {
            return clazz;
        }
        return load(className2);
    }

    @Nullable
    public static Class<?> findClassWithSynthetics(@NonNull String className1, @NonNull String className2,
                                                   @NonNull String className3, int... index) {
        String cacheKey = className1;
        Class<?> cache = sClassCache.get(cacheKey);
        if (cache != null) {
            return cache;
        }
        Class<?> clazz = findClassWithSyntheticsImpl(className1, index);
        if (clazz != null) {
            sClassCache.put(cacheKey, clazz);
            return clazz;
        }
        clazz = findClassWithSyntheticsImpl(className2, index);
        if (clazz != null) {
            sClassCache.put(cacheKey, clazz);
            return clazz;
        }
        clazz = findClassWithSyntheticsImpl(className3, index);
        if (clazz != null) {
            sClassCache.put(cacheKey, clazz);
            return clazz;
        }
        Logger.e("Initiator/E class " + className1 + " not found");
        return null;
    }

    public static Class<?> _ThemeUtil() {
        return findClassWithSynthetics("com/tencent/mobileqq/theme/ThemeUtil", "com.tencent.mobileqq.vas.theme.api.ThemeUtil");
    }

    /**
     * Load a class, if the class is not found, a ClassNotFoundException will be thrown.
     *
     * @param className The class name.
     * @return The class.
     * @throws ClassNotFoundException If the class is not found.
     */
    @NonNull
    public static Class<?> loadClass(String className) throws ClassNotFoundException {
        Class<?> ret = load(className);
        if (ret == null) {
            throw new ClassNotFoundException(className);
        }
        return ret;
    }

    @NonNull
    public static Class<?> loadClassEither(@NonNull String... classNames) throws ClassNotFoundException {
        for (String className : classNames) {
            Class<?> ret = load(className);
            if (ret != null) {
                return ret;
            }
        }
        throw new ClassNotFoundException("Class not found for names: " + Arrays.toString(classNames));
    }

    @SuppressWarnings("unchecked")
    public static Class<? extends AppRuntime> _QQAppInterface() {
        if (kQQAppInterface == null) {
            kQQAppInterface = load("com/tencent/mobileqq/app/QQAppInterface");
            if (kQQAppInterface == null) {
                Class<?> ref = load("com/tencent/mobileqq/app/QQAppInterface$1");
                if (ref != null) {
                    try {
                        kQQAppInterface = ref.getDeclaredField("this$0").getType();
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        return (Class<? extends AppRuntime>) kQQAppInterface;
    }

    @Nullable
    private static Class<?> findClassWithSyntheticsImpl(@NonNull String className, int... index) {
        Class<?> clazz = load(className);
        if (clazz != null) {
            return clazz;
        }
        if (index != null && index.length > 0) {
            for (int i : index) {
                Class<?> cref = load(className + "$" + i);
                if (cref != null) {
                    try {
                        return cref.getDeclaredField("this$0").getType();
                    } catch (ReflectiveOperationException ignored) {
                    }
                }
            }
        }
        return null;
    }


    @Nullable
    private static Class<?> findClassWithSyntheticsSilently(@NonNull String className, int... index) {
        Class<?> cache = sClassCache.get(className);
        if (cache != null) {
            return cache;
        }
        Class<?> clazz = load(className);
        if (clazz != null) {
            sClassCache.put(className, clazz);
            return clazz;
        }
        clazz = findClassWithSyntheticsImpl(className, index);
        if (clazz != null) {
            sClassCache.put(className, clazz);
            return clazz;
        }
        return null;
    }

    @Nullable
    public static Class<?> findClassWithSynthetics0(@NonNull String className1, @NonNull String className2, int... index) {
        String cacheKey = className1;
        Class<?> cache = sClassCache.get(cacheKey);
        if (cache != null) {
            return cache;
        }
        Class<?> clazz = findClassWithSyntheticsImpl(className1, index);
        if (clazz != null) {
            sClassCache.put(cacheKey, clazz);
            return clazz;
        }
        clazz = findClassWithSyntheticsImpl(className2, index);
        if (clazz != null) {
            sClassCache.put(cacheKey, clazz);
            return clazz;
        }
        return null;
    }

    @Nullable
    public static Class<?> findClassWithSynthetics(@NonNull String className1, @NonNull String className2, int... index) {
        Class<?> ret = findClassWithSynthetics0(className1, className2, index);
        logErrorIfNotFound(ret, className1);
        return ret;
    }


    @Nullable
    public static Class<?> findClassWithSynthetics(@NonNull String className, int... index) {
        Class<?> cache = sClassCache.get(className);
        if (cache != null) {
            return cache;
        }
        Class<?> clazz = load(className);
        if (clazz != null) {
            sClassCache.put(className, clazz);
            return clazz;
        }
        clazz = findClassWithSyntheticsImpl(className, index);
        if (clazz != null) {
            sClassCache.put(className, clazz);
            return clazz;
        }
        Logger.e("Initiator/E class " + className + " not found");
        return null;
    }

    private static void logErrorIfNotFound(@Nullable Class<?> c, @NonNull String name) {
        if (c == null) {
            Logger.e("Initiator/E class " + name + " not found");
        }
    }


    public static Class<?> _TroopPicEffectsController() {
        return findClassWithSynthetics("com/tencent/mobileqq/trooppiceffects/TroopPicEffectsController", 2);
    }

    public static Class<?> _BannerManager() {
        return findClassWithSynthetics("com.tencent.mobileqq.activity.recent.BannerManager", 38, 39, 40, 41, 42);
    }

    public static Class<?> _PttItemBuilder() {
        return findClassWithSynthetics("com/tencent/mobileqq/activity/aio/item/PttItemBuilder", 2);
    }

    public static Class<?> _ReplyItemBuilder() {
        return load("com.tencent.mobileqq.activity.aio.item.ReplyTextItemBuilder");
    }

    public static Class<?> _TroopGiftAnimationController() {
        return findClassWithSynthetics("com.tencent.mobileqq.troopgift.TroopGiftAnimationController", 1);
    }

    public static Class<?> _FavEmoRoamingHandler() {
        return findClassWithSynthetics("com/tencent/mobileqq/app/FavEmoRoamingHandler", 1);
    }

    public static Class<?> _StartupDirector() {
        return findClassWithSyntheticsSilently("com/tencent/mobileqq/startup/director/StartupDirector", 1);
    }

    private static boolean isNtStartupDirector(Class<?> klass) {
        if (klass == null || klass == _StartupDirector()) {
            return false;
        }
        if (!android.os.Handler.Callback.class.isAssignableFrom(klass)) {
            return false;
        }
        // have a static instance field
        boolean hasStaticInstance = false;
        for (Field field : klass.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType() == klass) {
                hasStaticInstance = true;
                break;
            }
        }
        if (!hasStaticInstance) {
            return false;
        }
        return true;
    }

    private static Class<?> skNtStartupDirector = null;
    private static boolean sNotHaveNtStartupDirector = false;

    public static Class<?> _NtStartupDirector() {
        if (skNtStartupDirector != null) {
            return skNtStartupDirector;
        }
        if (sNotHaveNtStartupDirector) {
            return null;
        }
        String[] candidates = new String[]{
                "com.tencent.mobileqq.startup.director.a",
                "com.tencent.mobileqq.h3.a.a",
                "com.tencent.mobileqq.g3.a.a",
                "com.tencent.mobileqq.i3.a.a",
                "com.tencent.mobileqq.j3.a.a"
        };
        for (String candidate : candidates) {
            Class<?> klass = load(candidate);
            if (isNtStartupDirector(klass)) {
                skNtStartupDirector = klass;
                return klass;
            }
        }
        sNotHaveNtStartupDirector = true;
        return null;
    }

    public static Class<?> _BaseQQMessageFacade() {
        return load("com/tencent/imcore/message/BaseQQMessageFacade");
    }

    public static Class<?> _QQMessageFacade() {
        return findClassWithSynthetics("com/tencent/mobileqq/app/message/QQMessageFacade",
                "com/tencent/imcore/message/QQMessageFacade");
    }

    @SuppressWarnings("unchecked")
    public static <T extends Parcelable> Class<T> _SessionInfo() {
        return (Class<T>) load("com/tencent/mobileqq/activity/aio/SessionInfo");
    }

    public static Class<?> _BaseChatPie() {
        return findClassWithSynthetics("com/tencent/mobileqq/activity/aio/core/BaseChatPie", "com.tencent.mobileqq.activity.BaseChatPie");
    }

    public static Class<?> _TroopMemberInfo() {
        return findClassWithSynthetics("com.tencent.mobileqq.data.troop.TroopMemberInfo", "com.tencent.mobileqq.data.TroopMemberInfo");
    }

    public static Class<?> _TroopInfo() {
        return findClassWithSynthetics("com.tencent.mobileqq.data.troop.TroopInfo", "com.tencent.mobileqq.data.TroopInfo");
    }

    public static Class<?> _Conversation() {
        return findClassWithSynthetics("com/tencent/mobileqq/activity/home/Conversation", "com/tencent/mobileqq/activity/Conversation", 5);
    }

    public static Class<?> _ChatMessage() {
        return load("com.tencent.mobileqq.data.ChatMessage");
    }

    public static Class<?> _MessageRecord() {
        return load("com/tencent/mobileqq/data/MessageRecord");
    }


}
package moe.ono.common;

import android.content.Context;

public class TransitClassLoader extends ClassLoader {

    private static final ClassLoader sSystem = Context.class.getClassLoader();
    private static final ClassLoader sCurrent = TransitClassLoader.class.getClassLoader();

    public TransitClassLoader() {
        super(sSystem);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (name != null && name.startsWith("moe.ono.loader.")) {
            return sCurrent.loadClass(name);
        }
        return super.findClass(name);
    }

}

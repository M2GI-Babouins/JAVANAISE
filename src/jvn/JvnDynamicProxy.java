package jvn;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JvnDynamicProxy implements InvocationHandler {

    private JvnObject jo;
    private JvnDynamicProxy(JvnObject jo) {
        this.jo = jo;
    }

    public static JvnObject newProxy(JvnObject jo) throws JvnException {
        return (JvnObject) java.lang.reflect.Proxy.newProxyInstance(
                JvnObject.class.getClassLoader(),
                new Class [] {JvnObject.class},
                new JvnDynamicProxy(jo));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}

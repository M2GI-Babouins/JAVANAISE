package jvn;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import irc.Action;

public class JvnDynamicProxy implements InvocationHandler, Serializable {

    private JvnObject jo;
    JvnDynamicProxy(JvnObject jo) {
        this.jo = jo;
    }

    public static Object newProxy(JvnObject jo) throws JvnException {
        return java.lang.reflect.Proxy.newProxyInstance(
                jo.jvnGetSharedObject().getClass().getClassLoader(),
                jo.jvnGetSharedObject().getClass().getInterfaces(),
                new JvnDynamicProxy(jo));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result;

        System.out.println("In proxy for :"+method.getName());

        if(method.isAnnotationPresent(Action.class)){
            Action a = method.getAnnotation(Action.class);

            if(a.name().equals("read"))
                jo.jvnLockRead();
            if(a.name().equals("write"))
                jo.jvnLockWrite();
        }



            result = method.invoke(jo.jvnGetSharedObject(),args);

        if(method.isAnnotationPresent(Action.class)){
            jo.jvnUnLock();
        }

        return result;
    }
}

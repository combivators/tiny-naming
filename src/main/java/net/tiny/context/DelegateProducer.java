package net.tiny.context;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class DelegateProducer implements InvocationHandler
{
	private Class<?>[] classes;
	private DelegateProducer(Class<?>... beanClasses)
	{
		this.classes = beanClasses;
	}

	@SuppressWarnings("unchecked")
	public static <I>I bind(Class<I> interfaceClass, Class<?>... beanClasses)
	{
		return  (I)Proxy.newProxyInstance(interfaceClass.getClassLoader(),
				new Class[]{interfaceClass}, new DelegateProducer(beanClasses));
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result = null;
		Method mbegin=null,mimpl=null,mend=null;

		for(int j=0;j<classes.length;j++)
		{
			Method[] methods = classes[j].getMethods();
			for(int i=0;i<methods.length;i++) {
				boolean flag = methods[i].isAnnotationPresent(Delegate.class);
				if(flag) {
					Delegate delegate = methods[i].getAnnotation(Delegate.class);
					Class<?> classType = Class.forName(delegate.interfaceName());
					if(isDelegateInstance(proxy, method, delegate, classType, methods[i])) {
						DelegatePolicy policy = delegate.policy();
						switch(policy) {
						case Begin:
							mbegin = methods[i];
							break;
						case Implements:
							mimpl = methods[i];
							break;
						case End:
							mend = methods[i];
							break;
						}
					}
				}
			}
		}

		if(mimpl!=null) {
			if(mbegin!=null)
				mbegin.invoke(mbegin.getDeclaringClass().newInstance(), args);
			result = mimpl.invoke(mimpl.getDeclaringClass().newInstance(), args);
			if(mend!=null)
				mend.invoke(mend.getDeclaringClass().newInstance(), args);
		}
		return result;
	}

	private boolean isDelegateInstance(Object proxy, Method method, Delegate delegate, Class<?> classType, Method proxyMethod) {
		return classType.isAssignableFrom(proxy.getClass()) &&
		delegate.methodName().equals(method.getName()) &&
		Arrays.equals(method.getParameterTypes(), proxyMethod.getParameterTypes()) &&
		method.getReturnType().equals(proxyMethod.getReturnType());
	}
}
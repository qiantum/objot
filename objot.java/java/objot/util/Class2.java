package objot.util;

import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;


public class Class2
{
	protected Class2()
	{
		throw new AbstractMethodError();
	}

	/**
	 * Primitive class to box class.
	 * 
	 * @param boxVoid whether box void.class to Void.class
	 * @return the box class
	 * @throws ClassCastException if this class is not primitive, or if this class is void
	 *             and boxVoid is false
	 */
	public static Class<?> box(Class<?> c, boolean boxVoid)
	{
		if (c == int.class)
			return Integer.class;
		else if (c == boolean.class)
			return Boolean.class;
		else if (c == long.class)
			return Long.class;
		else if (c == byte.class)
			return Byte.class;
		else if (c == char.class)
			return Character.class;
		else if (c == short.class)
			return Short.class;
		else if (c == float.class)
			return Float.class;
		else if (c == double.class)
			return Double.class;
		else if (c == void.class && boxVoid)
			return Void.class;
		else
			throw new ClassCastException();
	}

	/**
	 * Box class to primitive class.
	 * 
	 * @param unboxVoid whether unbox Void.class to void.class
	 * @return the primitive class
	 * @throws ClassCastException if this is not primitive box class, or if this is Void
	 *             class and unboxVoid is false
	 */
	public static Class<?> unbox(Class<?> c, boolean unboxVoid)
	{
		if (c == Integer.class)
			return int.class;
		else if (c == Boolean.class)
			return boolean.class;
		else if (c == Long.class)
			return long.class;
		else if (c == Byte.class)
			return byte.class;
		else if (c == Character.class)
			return char.class;
		else if (c == Short.class)
			return short.class;
		else if (c == Float.class)
			return float.class;
		else if (c == Double.class)
			return double.class;
		else if (c == Void.class && unboxVoid)
			return void.class;
		else
			throw new ClassCastException();
	}

	/** @return class name without package. */
	public static String selfName(Class<?> c)
	{
		return selfName(c.getName());
	}

	/** @return class name without package. */
	public static String selfName(String className)
	{
		return className.substring(className.lastIndexOf('.') + 1);
	}

	public static String packageName(Class<?> c)
	{
		return packageName(c.getName());
	}

	public static String packageName(String className)
	{
		int dot = className.lastIndexOf('.');
		return dot > 0 ? className.substring(0, dot) : "";
	}

	public static String pathName(Class<?> c)
	{
		return pathName(c.getName());
	}

	public static String pathName(String className)
	{
		return className.replace('.', '/');
	}

	public static String resourceName(Class<?> c)
	{
		return resourceName(c.getName());
	}

	public static String resourceName(String className)
	{
		return '/' + pathName(className) + ".class";
	}

	public static String descript(Class<?> c)
	{
		return descript(c.getName());
	}

	public static String descript(String className)
	{
		if (className.equals("int"))
			return "I";
		if (className.equals("boolean"))
			return "Z";
		if (className.equals("byte"))
			return "B";
		if (className.equals("short"))
			return "S";
		if (className.equals("char"))
			return "C";
		if (className.equals("long"))
			return "J";
		if (className.equals("float"))
			return "F";
		if (className.equals("double"))
			return "D";
		if (className.equals("void"))
			return "V";
		if (className.charAt(0) == '[')
			return pathName(className);
		return 'L' + pathName(className) + ';';
	}

	public static String descript(Field f)
	{
		return descript(f.getType());
	}

	public static String descript(Method m)
	{
		StringBuilder d = new StringBuilder(31);
		d.append('(');
		for (Class<?> a: m.getParameterTypes())
			d.append(descript(a));
		d.append(')');
		d.append(descript(m.getReturnType()));
		return d.toString();
	}

	/** @param m be checked if follows the rules of getter/setter name and parameters */
	public static String propertyName(Method m, boolean get)
	{
		String n = m.getName();
		if (get && ( !n.startsWith("get") || m.getParameterTypes().length > 0 //
		|| m.getReturnType() == void.class))
			throw new RuntimeException("invalid getter: " + m);
		if ( !get && ( !n.startsWith("set") || m.getParameterTypes().length != 1 //
		|| m.getReturnType() != void.class))
			throw new RuntimeException("invalid setter: " + m);
		if (n.length() > 4 && Character.isUpperCase(n.charAt(3))
			&& Character.isUpperCase(n.charAt(4)))
			return n.substring(3);
		else
			return Character.toLowerCase(n.charAt(3)) + n.substring(4);
	}

	public static Class<?> typeParamClass(Type t, int paramIndex, Class<?> Default)
	{
		if (t instanceof ParameterizedType)
		{
			t = ((ParameterizedType)t).getActualTypeArguments()[paramIndex];
			if (t instanceof Class)
				Default = (Class<?>)t;
		}
		return Default;
	}

	public static final Exception exception(Throwable e) throws Error
	{
		if (e instanceof Error)
			throw (Error)e;
		return e instanceof Exception ? (Exception)e : new Exception(e);
	}

	public static final RuntimeException runtimeException(Throwable e) throws Error
	{
		if (e instanceof Error)
			throw (Error)e;
		return e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e);
	}

	public static Field field(Class<?> c, String name)
	{
		try
		{
			return c.getField(name);
		}
		catch (NoSuchFieldException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static Field declaredField(Class<?> c, String name)
	{
		try
		{
			return c.getDeclaredField(name);
		}
		catch (NoSuchFieldException e)
		{
			throw new RuntimeException(e);
		}
	}

	/** excludes {@link Mod2.P#INITER} */
	@SuppressWarnings("cast")
	public static Method method(Class<?> c, String name, Class<?>... paramTypes)
	{
		try
		{
			return c.getMethod(name, (Class<?>[])paramTypes);
		}
		catch (NoSuchMethodException e)
		{
			throw new RuntimeException(e);
		}
	}

	/** excludes {@link Mod2.P#INITER} */
	public static Method method1(Class<?> c, String name)
	{
		for (Method m: c.getMethods())
			if (m.getName().equals(name))
				return m;
		throw new RuntimeException(new NoSuchMethodException(c.getName() + '.' + name));
	}

	/** excludes {@link Mod2.P#INITER} */
	@SuppressWarnings("cast")
	public static Method declaredMethod(Class<?> c, String name, Class<?>... paramTypes)
	{
		try
		{
			return c.getDeclaredMethod(name, (Class<?>[])paramTypes);
		}
		catch (NoSuchMethodException e)
		{
			throw new RuntimeException(e);
		}
	}

	/** excludes {@link Mod2.P#INITER} */
	public static Method declaredMethod1(Class<?> c, String name)
	{
		for (Method m: c.getDeclaredMethods())
			if (m.getName().equals(name))
				return m;
		throw new RuntimeException(new NoSuchMethodException(c.getName() + '.' + name));
	}

	public static <T extends AccessibleObject>T accessible(final T o)
	{
		return AccessController.doPrivileged(new PrivilegedAction<T>()
		{
			public T run()
			{
				o.setAccessible(true);
				return o;
			}
		});
	}

	public static Bytes classFile(Class<?> c) throws IOException
	{
		return new Bytes(c.getResourceAsStream(Class2.resourceName(c)), true);
	}

	public static final Method DEFINE_CLASS = accessible(declaredMethod(ClassLoader.class,
		"defineClass", String.class, byte[].class, int.class, int.class));

	@SuppressWarnings("unchecked")
	public static final <T>Class<T> load(ClassLoader l, String name, final byte[] bytecode,
		final int begin, final int end1) throws Exception
	{
		try
		{
			l.loadClass(name);
			throw new Exception("duplicate class " + name);
		}
		catch (ClassNotFoundException e)
		{
		}
		try
		{
			return (Class<T>)DEFINE_CLASS.invoke(l, name, bytecode, begin, end1 - begin);
		}
		catch (InvocationTargetException e)
		{
			throw exception(e.getCause());
		}
	}

	public static final <T>Class<T> load(ClassLoader l, String name, byte[] bytecode)
		throws Exception
	{
		return load(l, name, bytecode, 0, bytecode.length);
	}

	public static final <T>Class<T> load(ClassLoader l, String name, Bytes bytecode)
		throws Exception
	{
		return load(l, name, bytecode.bytes, bytecode.beginBi, bytecode.end1Bi);
	}
}

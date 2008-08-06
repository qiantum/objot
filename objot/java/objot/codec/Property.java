//
// Copyright 2007-2008 Qianyan Cai
// Under the terms of the GNU Library General Public License version 2
//
package objot.codec;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import objot.util.Class2;


public abstract class Property
{
	protected Class<?> out;
	protected Field field;
	protected Method method;
	/** for {@link Clazz} subclass */
	public String name;
	protected Class<?> cla;
	protected Class<?> listElem;

	/** @param fm field or method */
	protected Property(AccessibleObject fm, boolean enc)
	{
		if (fm instanceof Field)
		{
			field = (Field)fm;
			out = field.getDeclaringClass();
			name = field.getName();
			cla = field.getType();
		}
		else
		{
			method = (Method)fm;
			out = method.getDeclaringClass();
			name = Class2.propertyOrName(method, enc);
			cla = enc ? method.getReturnType() : method.getParameterTypes()[0];
		}
		if (Collection.class.isAssignableFrom(cla))
			listElem = Class2.typeParamClass(field != null ? field.getGenericType() : //
				enc ? method.getGenericReturnType() : method.getGenericParameterTypes()[0],
				0, Object.class);
	}

	/**
	 * add this to a map
	 * 
	 * @throws RuntimeException if there is a exist property with same name
	 */
	protected void into(Map<String, Property> map)
	{
		Property p = map.get(name);
		if (p != null)
			throw new RuntimeException("duplicate name " + name + ", see " + p);
		map.put(name, this);
	}

	public abstract boolean allowEnc(Object ruleKey) throws Exception;

	public abstract boolean allowDec(Object ruleKey) throws Exception;

	int index;
	boolean clob;
	static final Field F_name = Class2.declaredField(Property.class, "name");
	static final Method M_allowEnc = Class2.declaredMethod1(Property.class, "allowEnc");
}

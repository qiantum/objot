//
// Copyright 2007 Qianyan Cai
// Under the terms of The GNU General Public License version 2
//
package chat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/** service in signed or unsigned session, and in signed session if no this annotation */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SignAny
{
}

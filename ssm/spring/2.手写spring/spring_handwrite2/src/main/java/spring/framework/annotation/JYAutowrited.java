package spring.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = { ElementType.FIELD })  
@Retention(value = RetentionPolicy.RUNTIME) // 只对运行时有效
public @interface JYAutowrited {
String name() default "";
}

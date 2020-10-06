package test.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target(value = { ElementType.TYPE })  
@Retention(value = RetentionPolicy.RUNTIME) // 只对运行时有效
public @interface JYController {

	

}

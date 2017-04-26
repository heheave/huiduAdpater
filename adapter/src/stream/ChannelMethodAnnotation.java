package stream;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ChannelMethodAnnotation {

	public static final String UNKNOWN = "UNKNOWN";

	String name() default UNKNOWN;
}

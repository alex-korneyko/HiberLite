package ua.in.korneiko.hiberlite.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinColumn {

    String tsbleName() default "";

    FetchMode fetch() default FetchMode.INSTANTLY;

    boolean createIfAbsent() default false;
}

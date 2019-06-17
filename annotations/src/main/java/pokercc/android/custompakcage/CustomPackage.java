package pokercc.android.custompakcage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记用来生成一个特定包名的类，继承自该类
 *
 * @author pokercc
 * 2019-6-17 21:23:43
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface CustomPackage {
    /**
     * can not be null
     *
     * @return the Generate class's package
     */
    String packageName() default "";

    /**
     * same as {@link #packageName()}
     *
     * @return the Generate class's package
     */
    String value();
}

package pokercc.android.custompakcage;

/**
 * 标记用来生成一个特定包名的类，继承自该类
 *
 * @author pokercc <pokercc@sina.com>
 * @date 2019-6-17 21:23:43
 */
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
     * @return
     */
    String value();
}

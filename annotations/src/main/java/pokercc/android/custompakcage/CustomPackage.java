package pokercc.android.custompakcage;

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

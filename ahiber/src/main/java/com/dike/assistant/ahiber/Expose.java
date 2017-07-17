package com.dike.assistant.ahiber;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that indicates this member should be exposed for AHiber
 * persist or depersist.
 * <p>
 * <p>
 * <p>Here is an example of how this annotation is meant to be used:
 * <p><pre>
 * public class User {
 *   &#64Expose private String firstName;
 *   &#64Expose(persistence = false) private String lastName;
 *   &#64Expose (persistence = false, depersistence = false) private String emailAddress;
 *   private String password;
 * }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Expose
{
    /**
     * If {@code true}, the field marked with this annotation is written out in the AHiber while
     * persistence. If {@code false}, the field marked with this annotation is skipped from the
     * persistence output. Defaults to {@code true}.
     *
     * @since 1.4
     */
    public boolean persistence() default true;

    /**
     * If {@code true}, the field marked with this annotation is depersistence from the AHiber.
     * If {@code false}, the field marked with this annotation is skipped during depersistence.
     * Defaults to {@code true}.
     *
     * @since 1.4
     */
    public boolean depersistence() default true;
}



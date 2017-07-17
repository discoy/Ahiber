package com.dike.assistant.ahiber;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that indicates this member should be persist to db with
 * the provided name value as its field name.
 * <p>
 * <p>Here is an example of how this annotation is meant to be used:</p>
 * <pre>
 * public class SomeClassWithFields {
 *   &#64SerializedName("name") private final String someField;
 *   private final String someOtherField;
 *
 *   public SomeClassWithFields(String a, String b) {
 *     this.someField = a;
 *     this.someOtherField = b;
 *   }
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PersistenceName
{

    /**
     * @return the desired name of the field when it is persisted
     */
    String value();
}


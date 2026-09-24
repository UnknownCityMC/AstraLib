package de.unknowncity.astralib.common.configuration.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @deprecated since 0.8.2, use ocular by Eldoria instead
 */
@Deprecated(since = "0.8.2", forRemoval = true)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Config {
    String targetFile();
}
package eu.stamp_project.descartes.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Target;

@Target({METHOD, TYPE})
public @interface DoNotMutate {
  String[] operators() default {};
}

package eu.stamp_project.descartes.annotations;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Target;

@Target({METHOD, TYPE})
public @interface DoNotMutate {
  String[] operators() default {};
}

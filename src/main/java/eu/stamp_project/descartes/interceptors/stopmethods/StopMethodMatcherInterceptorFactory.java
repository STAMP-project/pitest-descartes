package eu.stamp_project.descartes.interceptors.stopmethods;

import static eu.stamp_project.descartes.interceptors.stopmethods.StopMethodMatchers.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.MutationInterceptorFactory;
import org.pitest.plugin.Feature;
import org.pitest.plugin.FeatureParameter;

public class StopMethodMatcherInterceptorFactory implements MutationInterceptorFactory {

  private final FeatureParameter exceptParameter;

  private final Map<String, MethodMatcher> availableMatchers;

  public StopMethodMatcherInterceptorFactory() {

    availableMatchers = new HashMap<>();
    availableMatchers.put("empty", IS_EMPTY_VOID);
    availableMatchers.put("enum", IS_ENUM_GENERATED);
    availableMatchers.put("to_string", IS_TO_STRING);
    availableMatchers.put("hash_code", IS_HASH_CODE);
    availableMatchers.put("deprecated", IS_DEPRECATED);
    availableMatchers.put("synthetic", IS_SYNTEHTIC);
    availableMatchers.put("getter", IS_SIMPLE_GETTER);
    availableMatchers.put("setter", IS_SIMPLE_SETTER);
    availableMatchers.put("constant", RETURNS_CONSTANT);
    availableMatchers.put("delegate", IS_DELEGATE);
    availableMatchers.put("clinit", IS_STATIC_INITIALIZER);
    availableMatchers.put("empty_array", RETURNS_EMPTY_ARRAY);
    availableMatchers.put("null_return", RETURNS_NULL);
    availableMatchers.put("return_this", RETURNS_THIS);
    availableMatchers.put("return_param", RETURNS_PARAMETER);
    availableMatchers.put("kotlin_setter", IS_KOTLIN_GENERATED_SETTER);

    String description =
        "Allows to reinsert some stop methods into the analysis. Possible values are: ";
    description += String.join(", ", availableMatchers.keySet());
    exceptParameter = FeatureParameter.named("except").withDescription(description);
  }

  @Override
  public MutationInterceptor createInterceptor(InterceptorParameters interceptorParameters) {
    Set<String> matchers = availableMatchers.keySet();
    List<String> exclusions = interceptorParameters.getList(exceptParameter);
    if (exclusions != null) {
      exclusions.forEach(matchers::remove);
    }

    return new StopMethodInterceptor(
        MethodMatcher.matcherForAnyOf(
            matchers.stream().map(availableMatchers::get).collect(Collectors.toList())));
  }

  @Override
  public Feature provides() {
    return Feature.named("STOP_METHODS")
        .withOnByDefault(true)
        .withDescription("Filters out mutations in methods that are generally of no interest")
        .withParameter(exceptParameter);
  }

  @Override
  public String description() {
    return "Stop methods filter";
  }
}

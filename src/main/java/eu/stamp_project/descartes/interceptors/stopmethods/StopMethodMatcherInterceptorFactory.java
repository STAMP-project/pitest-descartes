package eu.stamp_project.descartes.interceptors.stopmethods;

import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.MutationInterceptorFactory;
import org.pitest.plugin.Feature;
import org.pitest.plugin.FeatureParameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static eu.stamp_project.descartes.interceptors.stopmethods.StopMethodMatchers.*;

public class StopMethodMatcherInterceptorFactory implements MutationInterceptorFactory {

    private FeatureParameter EXCEPT;

    private Map<String, StopMethodMatcher> availableMatchers;

    public StopMethodMatcherInterceptorFactory() {

        availableMatchers = new HashMap<>();
        availableMatchers.put("empty", isEmptyVoid());
        availableMatchers.put("enum", isEnumGenerated());
        availableMatchers.put("to_string", isToString());
        availableMatchers.put("hash_code", isHashCode());
        availableMatchers.put("deprecated", isDeprecated());
        availableMatchers.put("synthetic", isSynthetic());
        availableMatchers.put("getter", isSimpleGetter());
        availableMatchers.put("setter", isSimpleSetter());
        availableMatchers.put("constant", returnsAConstant());
        availableMatchers.put("delegate", isDelegate());
        availableMatchers.put("clinit", isStaticInitializer());
        availableMatchers.put("empty_array", returnsAnEmptyArray());
        availableMatchers.put("null_return", returnsNull());
        availableMatchers.put("return_this", returnsThis());
        availableMatchers.put("return_param", returnsAParameter());
        availableMatchers.put("kotlin_setter", isKotlinGeneratedSetter());


        String description = "Allows to reinsert some stop methods into the analysis. Possible values are: ";
        description += String.join(", ", availableMatchers.keySet());
        EXCEPT = FeatureParameter.named("except").withDescription(description);
    }

    @Override
    public MutationInterceptor createInterceptor(InterceptorParameters interceptorParameters) {
        Set<String> matchers = availableMatchers.keySet();
        List<String> exclusions = interceptorParameters.getList(EXCEPT);
        if(exclusions != null)
            matchers.removeAll(exclusions);

        return new StopMethodInterceptor(
                StopMethodMatcher.any(
                        matchers.stream()
                                .map(key -> availableMatchers.get(key))
                                .collect(Collectors.toList())
                )
        );

    }

    @Override
    public Feature provides() {
        return Feature
                .named("STOP_METHODS")
                .withOnByDefault(true)
                .withDescription("Filters out mutations in methods that are generally of no interest")
                .withParameter(EXCEPT);

    }

    @Override
    public String description() {
        return "Stop methods filter";
    }


}

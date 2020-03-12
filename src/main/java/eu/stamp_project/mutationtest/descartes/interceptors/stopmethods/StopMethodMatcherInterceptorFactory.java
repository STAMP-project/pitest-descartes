package eu.stamp_project.mutationtest.descartes.interceptors.stopmethods;

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

import static eu.stamp_project.mutationtest.descartes.interceptors.stopmethods.StopMethodMatchers.*;

public class StopMethodMatcherInterceptorFactory implements MutationInterceptorFactory {

    private FeatureParameter EXCEPT;

    private Map<String, StopMethodMatcher> availabeMatchers;

    public StopMethodMatcherInterceptorFactory() {

        availabeMatchers = new HashMap<>();
        availabeMatchers.put("empty", isEmptyVoid());
        availabeMatchers.put("enum", isEnumGenerated());
        availabeMatchers.put("to_string", isToString());
        availabeMatchers.put("hash_code", isHashCode());
        availabeMatchers.put("deprecated", isDeprecated());
        availabeMatchers.put("synthetic", isSynthetic());
        availabeMatchers.put("getter", isSimpleGetter());
        availabeMatchers.put("setter", isSimpleSetter());
        availabeMatchers.put("constant", returnsAConstant());
        availabeMatchers.put("delegate", isDelegate());
        availabeMatchers.put("clinit", isStaticInitializer());
        availabeMatchers.put("empty_array", returnsAnEmptyArray());
        availabeMatchers.put("null_return", returnsNull());
        availabeMatchers.put("return_this", returnsThis());
        availabeMatchers.put("return_param", returnsAParameter());
        availabeMatchers.put("kotlin_setter", isKotlinGeneratedSetter());


        String description = "Allows to reinsert some stop methods into the analysis. Possible values are: ";
        description += availabeMatchers.keySet().stream().collect(Collectors.joining(", "));
        EXCEPT = FeatureParameter.named("except").withDescription(description);
    }

    @Override
    public MutationInterceptor createInterceptor(InterceptorParameters interceptorParameters) {
        Set<String> matchers = availabeMatchers.keySet();
        List<String> exclusions = interceptorParameters.getList(EXCEPT);
        if(exclusions != null)
            matchers.removeAll(exclusions);

        return new StopMethodInterceptor(
                StopMethodMatcher.any(
                        matchers.stream()
                                .map(key -> availabeMatchers.get(key))
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

package eu.stamp_project.mutationtest.descartes;

import java.util.*;
import java.util.stream.Collectors;
import eu.stamp_project.mutationtest.descartes.operators.MutationOperator;
import org.pitest.functional.prelude.Prelude;
import org.pitest.mutationtest.EngineArguments;
import org.pitest.reloc.asm.commons.Method;

import org.pitest.functional.predicate.*;

import org.pitest.mutationtest.MutationEngineFactory;
import org.pitest.mutationtest.engine.MutationEngine;
import org.pitest.util.Glob;


public class DescartesEngineFactory implements MutationEngineFactory{


    @Override
    public MutationEngine createEngine(EngineArguments engineArguments) {

        Collection<String> operators = engineArguments.mutators();
        if(operators.isEmpty())
            operators = getDefaultOperators();

        return createEngine(
                globsToPredicate(engineArguments.excludedMethods()),
                operators.stream()
                        .map(MutationOperator::fromID)
                        .collect(Collectors.toList()));
    }

    public MutationEngine createEngine(Predicate<Method> excludedMethods,
                                       Collection<MutationOperator> operators) {
        return new DescartesMutationEngine(excludedMethods, operators);
    }

    public static Collection<String> getDefaultOperators() {
        return Arrays.asList(
                "void",
                "null",
                "empty",
                "true", "false",
                "0", "1",
                "(short)0", "(short)1",
                "(byte)0", "(byte)1",
                "0L", "1L",
                "0.0", "1.0",
                "0.0f", "1.0f",
                "'\\40'", "'A'",
                "\"\"", "\"A\"");
    }


    public static Predicate<Method> globsToPredicate(Collection<String> globs) {

        Predicate<String> excludedNames = Prelude.or(Glob.toGlobPredicates(globs));

        return new Predicate<Method>() {
            @Override
            public Boolean apply(Method method) {
                return excludedNames.apply(method.getName());
            }
        };

    }

    public String name() {
        return "descartes";
    }

    public String description() {
        return "Engine for extreme mutation operators";
    }
}

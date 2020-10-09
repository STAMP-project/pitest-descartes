package eu.stamp_project.test.input;

import eu.stamp_project.descartes.annotations.DoNotMutate;

@DoNotMutate
public class SkipMutationTarget {

    public void methodToSkip() {
        throw new IllegalArgumentException();
    }

}




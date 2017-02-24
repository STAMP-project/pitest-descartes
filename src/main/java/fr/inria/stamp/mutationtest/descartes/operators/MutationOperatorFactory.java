package fr.inria.stamp.mutationtest.descartes.operators;


public final class MutationOperatorFactory {

    private MutationOperatorFactory(){}

    public static MutationOperator fromID(String id) {
        //TODO: Until I finish my struggle with jFlex, ANTLR and PIT this will be a naive implementation
        if(id.toLowerCase().equals("void"))//Case insensitive
            return VoidMutationOperator.get();
        try{
            int value = Integer.parseInt(id);
            return new ConstantMutationOperator(id, value);
        } catch(NumberFormatException exc) {
            throw new WrongOperatorException("Invalid integer specification: " + exc.getMessage(), exc);
        }
    }


}

package edu.hm.cs.interpreter.util;

import java.util.List;

/**
 * Collection of constants for the description of model names.
 */
public class ModelConstants {

    public static final String PAGE = "Page";
    public static final String CONDITION = "Condition";
    public static final String TRANSITION = "Transition";
    public static final String FORWARDING = "Forwarding";
    public static final String CONDITIONAL_TRANSITION = "ConditionalTransition";

    public static final String EQUALS = "==";
    public static final String UNEQUALS = "!=";
    public static final String LESS_THAN = "<";
    public static final String LESS_THAN_OR_EQUALS = "<=";
    public static final String GREATER_THAN = ">";
    public static final String GREATER_THAN_OR_EQUALS = ">=";
    public static final List<String> COMPARATORS = List.of(EQUALS, UNEQUALS, LESS_THAN, LESS_THAN_OR_EQUALS, GREATER_THAN, GREATER_THAN_OR_EQUALS);

}

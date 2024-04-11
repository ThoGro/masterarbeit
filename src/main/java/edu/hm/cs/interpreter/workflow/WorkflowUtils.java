package edu.hm.cs.interpreter.workflow;

import edu.hm.cs.interpreter.model.Forwarding;
import edu.hm.cs.interpreter.model.Navigationelement;
import edu.hm.cs.interpreter.model.Page;
import edu.hm.cs.interpreter.model.Transition;
import edu.hm.cs.interpreter.util.ModelConstants;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.*;

/**
 * Contains methods to support the process flow.
 */
public class WorkflowUtils {

    /**
     * Searchs for the start page in the list of all navigation elements.
     * @param navigationelements list of all navigation elements
     * @return the start page
     * @throws Exception if there is more than one possible start page
     */
    public static Page findStart(List<Navigationelement> navigationelements) throws Exception {
        Set<Page> startPages = new HashSet<>();
        Set<Page> endPages = new HashSet<>();
        for (Navigationelement navigationelement : navigationelements) {
            if (navigationelement.getTransition().isEmpty()) {
                endPages.add((Page) navigationelement);
            }
        }
        for (Page page : endPages) {
            Navigationelement start = page;
            while (findPredecessor(navigationelements, start) != null) {
                start = findPredecessor(navigationelements, start);
            }
            startPages.add((Page) start);
        }
        if (startPages.size() != 1) {
            throw new Exception("Keine gültige Anzahl an Startseiten. Es muss eine spezifische Startseite existieren.");
        }
        return (Page) startPages.toArray()[0];
    }

    /**
     * Checks whether a page is a start page.
     * @param navigationelements list of all navigation elements
     * @param potentialStart page that is checked
     * @return true if it is a start page otherwise false
     * @throws Exception if the start is not clear
     */
    public static boolean isStart(List<Navigationelement> navigationelements, Page potentialStart) throws Exception {
        return findStart(navigationelements).getName().equals(potentialStart.getName());
    }

    /**
     * Searchs for the next page in the process.
     * @param navigationelements list of all navigation elements
     * @param page current page
     * @param memory memory for the evaluation of conditions
     * @return the next page of the process
     * @throws Exception if there is no next page
     */
    public static Page getNextPage(List<Navigationelement> navigationelements, Page page, Map<String, String> memory) throws Exception {
        if (!page.getTransition().isEmpty()) {
            if (page.getTransition().get(0).getNext() instanceof Page) {
                for (Navigationelement navigationelement : navigationelements) {
                    if (navigationelement.getName().equals(page.getTransition().get(0).getNext().getName())) {
                        return (Page) navigationelement;
                    }
                }
            } else {
                for (Navigationelement navigationelement : navigationelements) {
                    if (navigationelement.getName().equals(page.getTransition().get(0).getNext().getName())) {
                        Page nextPage = null;
                        for (Transition transition : navigationelement.getTransition()) {
                            Forwarding forwarding = (Forwarding) transition;
                            String condition = forwarding.getCondition();
                            try {
                                String operator = findOperator(condition);
                                String left = condition.split(operator)[0];
                                String right = condition.split(operator)[1];
                                Expression leftExpression = new ExpressionBuilder(left)
                                        .variables(findVariables(left, memory))
                                        .build()
                                        .setVariables(setVariableValues(left, memory));
                                Expression rightExpression = new ExpressionBuilder(right)
                                        .variables(findVariables(right, memory))
                                        .build()
                                        .setVariables(setVariableValues(right, memory));
                                double leftResult = leftExpression.evaluate();
                                double rightResult = rightExpression.evaluate();
                                switch (operator) {
                                    case ModelConstants.EQUALS:
                                        if (leftResult == rightResult) {
                                            nextPage = getPageAfterCondition(navigationelements, transition);
                                        }
                                        break;
                                    case ModelConstants.UNEQUALS:
                                        if (leftResult != rightResult) {
                                            nextPage = getPageAfterCondition(navigationelements, transition);
                                        }
                                        break;
                                    case ModelConstants.LESS_THAN:
                                        if (leftResult < rightResult) {
                                            nextPage = getPageAfterCondition(navigationelements, transition);
                                        }
                                        break;
                                    case ModelConstants.LESS_THAN_OR_EQUALS:
                                        if (leftResult <= rightResult) {
                                            nextPage = getPageAfterCondition(navigationelements, transition);
                                        }
                                        break;
                                    case ModelConstants.GREATER_THAN:
                                        if (leftResult > rightResult) {
                                            nextPage = getPageAfterCondition(navigationelements, transition);
                                        }
                                        break;
                                    case ModelConstants.GREATER_THAN_OR_EQUALS:
                                        if (leftResult >= rightResult) {
                                            nextPage = getPageAfterCondition(navigationelements, transition);
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (nextPage == null) {
                            for (Transition transition : navigationelement.getTransition()) {
                                if (((Forwarding) transition).isDefaultTransition()) {
                                    for (Navigationelement element : navigationelements) {
                                        if (element.getName().equals(transition.getNext().getName())) {
                                            return (Page) element;
                                        }
                                    }
                                }
                            }
                        }
                        return nextPage;
                    }
                }
            }
        }
        throw new Exception("Keine weitere Seite gefunden.");
    }

    /**
     * Returns the corresponding page for the given transition.
     * @param navigationelements list of all navigation elements
     * @param transition transition for which the corresponding page should be found
     * @return the corresponding page for the given transition
     */
    private static Page getPageAfterCondition(List<Navigationelement> navigationelements, Transition transition) {
        for (Navigationelement element : navigationelements) {
            if (element.getName().equals(transition.getNext().getName())) {
                return (Page) element;
            }
        }
        return null;
    }

    /**
     * Finds the previous page for the given page.
     * @param navigationelements list of all navigation elements
     * @param page current page
     * @return the previous page in the process
     */
    public static Page getPreviousPage(List<Navigationelement> navigationelements, Page page) {
        Navigationelement previousPage = findPredecessor(navigationelements, page);
        while (!(previousPage instanceof Page)) {
            previousPage = findPredecessor(navigationelements, previousPage);
        }
        return (Page) previousPage;
    }

    /**
     * Finds the predecessor for the given navigation element
     * @param navigationelements list of all navigation elements
     * @param navigationelement current navigation element
     * @return the predecessor for the given navigation element if available otherwise null
     */
    public static Navigationelement findPredecessor(List<Navigationelement> navigationelements, Navigationelement navigationelement) {
        for (Navigationelement element : navigationelements) {
            for (Transition transition : element.getTransition()) {
                if (transition.getNext().getName().equals(navigationelement.getName())) {
                    return element;
                }
            }
        }
        return null;
    }

    /**
     * Extracs the operator from an expression string
     * @param expression string from which the operator is to be extracted
     * @return the operator as string
     * @throws Exception if no valid operator is found
     */
    private static String findOperator(String expression) throws Exception {
        for (String operator : ModelConstants.COMPARATORS) {
            if (expression.contains(operator)) {
                if (operator.equals(ModelConstants.LESS_THAN) || operator.equals(ModelConstants.GREATER_THAN)) {
                    if (expression.contains(ModelConstants.GREATER_THAN_OR_EQUALS)) {
                        return ModelConstants.GREATER_THAN_OR_EQUALS;
                    } else if (expression.contains(ModelConstants.LESS_THAN_OR_EQUALS)) {
                        return ModelConstants.LESS_THAN_OR_EQUALS;
                    }
                }
                return operator;
            }
        }
        throw new Exception("Kein gültiger Operator gefunden");
    }

    /**
     * Searches for global variables in an expression string.
     * @param expression string that is searched for variables
     * @param memory the global variables
     * @return a set of variables which a found in the expression
     */
    private static Set<String> findVariables(String expression, Map<String, String> memory) {
        Set<String> variables = new HashSet<>();
        for(Map.Entry<String, String> entry : memory.entrySet()) {
            if (expression.contains(entry.getKey())) {
                variables.add(entry.getKey());
            }
        }
        return variables;
    }

    /**
     * Finds for all variables in the expression the value of the variable.
     * @param expression string that is searched for variables
     * @param memory the global variables
     * @return key-value pairs of the variables and their values
     */
    private static Map<String, Double> setVariableValues(String expression, Map<String, String> memory) {
        Map<String, Double> variableValues = new HashMap<>();
        Set<String> variables = findVariables(expression, memory);
        variables.forEach(variable -> {
            variableValues.put(variable, Double.parseDouble(memory.get(variable)));
        });
        return variableValues;
    }

}

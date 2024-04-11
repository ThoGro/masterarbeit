package edu.hm.cs.interpreter.parser;

import edu.hm.cs.interpreter.model.*;
import edu.hm.cs.interpreter.util.AttributeConstants;
import edu.hm.cs.interpreter.util.ModelConstants;
import edu.hm.cs.interpreter.util.NodeConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Containts methods to support the parsing.
 */
public class ParserUtils {

    /**
     * Creates the navigation elements from the XML document.
     * @param document the XML document
     * @return list of all navigation elements from the xml document
     */
    public static List<Navigationelement> createNavigationelements(Document document) {
        List<Navigationelement> navigationElements = new ArrayList<>();
        for (Node node : iterable(document.getElementsByTagName(NodeConstants.INSTANCE))) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (element.getAttribute(AttributeConstants.CLASS).equals(ModelConstants.PAGE)) {
                    Page page = new Page();
                    page.setName(element.getAttribute(AttributeConstants.NAME));
                    page.setDescription(getAttribute(element, AttributeConstants.DESCRIPTION));
                    page.setVariables(getRecord(element, AttributeConstants.VARIABLES));
                    navigationElements.add(page);
                } else if (element.getAttribute(AttributeConstants.CLASS).equals(ModelConstants.CONDITION)) {
                    Condition condition = new Condition();
                    condition.setName(element.getAttribute(AttributeConstants.NAME));
                    navigationElements.add(condition);
                }
            }
        }
        return navigationElements;
    }

    /**
     * Creates the memory from the XML document.
     * @param document the XML document
     * @return the global variables of the memory as key-value pairs
     */
    public static Map<String, String> createMemory(Document document) {
        return getRecord((Element) document.getElementsByTagName(NodeConstants.MODELATTRIBUTES).item(0), AttributeConstants.MEMORY);
    }

    /**
     * Creates the transitions from the XML document.
     * @param document the XML document
     * @return list of all transitions from the XML document
     */
    public static List<Transition> createTransitions(Document document) {
        List<Navigationelement> navigationElements = createNavigationelements(document);
        List<Transition> transitions = new ArrayList<>();
        for (Node node : iterable(document.getElementsByTagName(NodeConstants.CONNECTOR))) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (element.getAttribute(AttributeConstants.CLASS).equals(ModelConstants.TRANSITION) ||
                        element.getAttribute(AttributeConstants.CLASS).equals(ModelConstants.CONDITIONAL_TRANSITION)) {
                    Transition transition = new Transition();
                    transition.setName(getAttribute(element, AttributeConstants.NAME));
                    transition.setNext(getTo(element, navigationElements));
                    transitions.add(transition);
                } else if (element.getAttribute(AttributeConstants.CLASS).equals(ModelConstants.FORWARDING)) {
                    Forwarding forwarding = new Forwarding();
                    forwarding.setName(getAttribute(element, AttributeConstants.NAME));
                    forwarding.setDefaultTransition(getAttribute(element, AttributeConstants.DEFAULT).equals("1"));
                    forwarding.setCondition(getAttribute(element, AttributeConstants.CONDITION));
                    forwarding.setNext(getTo(element, navigationElements));
                    transitions.add(forwarding);
                }
            }
        }
        return transitions;
    }

    /**
     * Links all transitions to the corresponding navigation element.
     * @param document the XML document
     * @param navigationElements list of the navigation elements from which the corresponding element is found
     * @param transitions list of transitions that are linked
     * @return list of all navigation elements after linking with the transitions
     */
    public static List<Navigationelement> linkTransitionToNavigationselement(Document document, List<Navigationelement> navigationElements, List<Transition> transitions) {
        for (Node node : iterable(document.getElementsByTagName(NodeConstants.CONNECTOR))) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                for (Navigationelement navigationelement : navigationElements) {
                    if (navigationelement.getName().equals(((Element) element.getElementsByTagName(NodeConstants.FROM).item(0)).getAttribute(AttributeConstants.INSTANCE))) {
                        for (Transition transition : transitions) {
                            if (transition.getName().equals(getAttribute(element, AttributeConstants.NAME))) {
                                navigationelement.getTransition().add(transition);
                            }
                        }
                    }
                }
            }
        }
        return navigationElements;
    }

    /**
     * Extracts the corresponding navigation element for the XML node TO.
     * @param element XML node
     * @param navigationElements list of the navigation elements
     * @return navigation element to which the XML node points
     */
    private static Navigationelement getTo(Element element, List<Navigationelement> navigationElements) {
        for (Navigationelement navigationelement : navigationElements) {
            if (navigationelement.getName().equals(((Element) element.getElementsByTagName(NodeConstants.TO).item(0)).getAttribute(AttributeConstants.INSTANCE))) {
                return navigationelement;
            }
        }
        return null;
    }

    /**
     * Extracts an attribute with given attribute name from an XML node.
     * @param element XML node
     * @param attributeName attribute name for which the value should be found
     * @return value of the extracted attribute
     */
    private static String getAttribute(Element element, String attributeName) {
        for (Node attribute : iterable(element.getElementsByTagName(NodeConstants.ATTRIBUTE))) {
            if (attribute.getNodeType() == Node.ELEMENT_NODE) {
                if (((Element) attribute).getAttribute(AttributeConstants.NAME).equals(attributeName)) {
                    return attribute.getTextContent();
                }
            }
        }
        return null;
    }

    /**
     * Extracts a record with the given record name from an XML node.
     * @param element XML node
     * @param recordName record name for which the values should be found
     * @return values of the extracted record as key-value pairs
     */
    private static Map<String, String> getRecord(Element element, String recordName) {
        Map<String, String> variables = new HashMap<>();
        for (Node record : iterable(element.getElementsByTagName(NodeConstants.RECORD))) {
            if (record.getNodeType() == Node.ELEMENT_NODE && ((Element) record).getAttribute(AttributeConstants.NAME).equals(recordName)) {
                for (Node row : iterable(((Element) record).getElementsByTagName(NodeConstants.ROW))) {
                    variables.put(((Element) row).getElementsByTagName(NodeConstants.ATTRIBUTE).item(0).getTextContent().toLowerCase(),
                            ((Element) row).getElementsByTagName(NodeConstants.ATTRIBUTE).item(1).getTextContent());
                }
            }
        }
        return variables;
    }

    /**
     * Converts a {@link NodeList} to an iterable of nodes.
     * @param nodeList {@link NodeList} to convert
     * @return iterable of nodes
     */
    private static Iterable<Node> iterable(final NodeList nodeList) {
        return () -> new Iterator<Node>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < nodeList.getLength();
            }

            @Override
            public Node next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                return nodeList.item(index++);
            }
        };
    }

}

package edu.hm.cs.interpreter.parser;

import edu.hm.cs.interpreter.model.*;
import edu.hm.cs.interpreter.util.TestUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for methods of {@link ParserUtils}
 */
public class ParserUtilsTest {

    @Test
    public void testLinkTransitionToNavigationselement() {
        List<Navigationelement> result = ParserUtils.linkTransitionToNavigationselement(TestUtils.getTestDocument(),
                ParserUtils.createNavigationelements(TestUtils.getTestDocument()), ParserUtils.createTransitions(TestUtils.getTestDocument()));
        assertEquals(6, result.size());
        assertEquals(5, (int) result.stream().filter(element -> element instanceof Page).count());
        assertEquals(1, (int) result.stream().filter(element -> element instanceof Condition).count());
        result.forEach(element -> assertNotNull(element.getName()));
        assertEquals(2, (int) result.stream().filter(element -> element.getTransition().isEmpty()).count());
        assertEquals(1, (int) result.stream().filter(element -> element.getTransition().size() > 1).count());
    }

    @Test
    public void testCreateTransitions() {
        List<Transition> result = ParserUtils.createTransitions(TestUtils.getTestDocument());
        assertEquals(5, result.size());
        assertEquals(2, (int) result.stream().filter(element -> element instanceof Forwarding).count());
        result.forEach(element -> assertNotNull(element.getName()));
    }

    @Test
    public void testCreateMemory() {
        Map<String, String> memory = ParserUtils.createMemory(TestUtils.getTestDocument());
        assertEquals(1, memory.size());
        assertTrue(memory.containsKey("alter"));
        assertEquals("", memory.get("alter"));
    }

    @Test
    public void testCreateNavigationelements() {
        List<Navigationelement> result = ParserUtils.createNavigationelements(TestUtils.getTestDocument());
        assertEquals(6, result.size());
        assertEquals(5, (int) result.stream().filter(element -> element instanceof Page).count());
        assertEquals(1, (int) result.stream().filter(element -> element instanceof Condition).count());
        result.forEach(element -> assertNotNull(element.getName()));
    }

}

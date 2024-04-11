package edu.hm.cs.interpreter.workflow;

import edu.hm.cs.interpreter.model.Condition;
import edu.hm.cs.interpreter.model.Navigationelement;
import edu.hm.cs.interpreter.model.Page;
import edu.hm.cs.interpreter.parser.ParserUtils;
import edu.hm.cs.interpreter.util.TestUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for methods of {@link WorkflowUtils}
 */
public class WorkflowUtilsTest {

    @Test
    public void testFindStart() throws Exception {
        Page result = WorkflowUtils.findStart(TestUtils.getTestNavigationelements());
        assertNotNull(result);
        assertEquals("Start", result.getName());
    }

    @Test
    public void testFindStartException() {
        Exception exception = assertThrows(Exception.class, () -> {
            WorkflowUtils.findStart(TestUtils.getTestNavigationelements("two_starts.xml"));
        });

        assertEquals("Keine gültige Anzahl an Startseiten. Es muss eine spezifische Startseite existieren.",
                exception.getMessage());
    }

    @Test
    public void testIsStart() throws Exception {
        List<Navigationelement> navigationelements = TestUtils.getTestNavigationelements();
        Page start = (Page) navigationelements.stream().filter(element -> element.getName().equals("Start")).collect(Collectors.toList()).get(0);
        Page noStart = (Page) navigationelements.stream().filter(element -> element.getName().equals("Ende des Antrags")).collect(Collectors.toList()).get(0);
        assertTrue(WorkflowUtils.isStart(navigationelements, start));
        assertFalse(WorkflowUtils.isStart(navigationelements, noStart));
    }

    @Test
    public void testGetNextPage() throws Exception {
        List<Navigationelement> navigationelements = TestUtils.getTestNavigationelements();
        Page start = (Page) navigationelements.stream().filter(element -> element.getName().equals("Start")).collect(Collectors.toList()).get(0);
        Page result = WorkflowUtils.getNextPage(navigationelements, start, ParserUtils.createMemory(TestUtils.getTestDocument()));
        assertNotNull(result);
        assertEquals("Eingabe Alter", result.getName());
    }
    @Test
    public void testGetNextPageAfterCondition() throws Exception {
        List<Navigationelement> navigationelements = TestUtils.getTestNavigationelements();
        Page page = (Page) navigationelements.stream().filter(element -> element.getName().equals("Eingabe Alter")).collect(Collectors.toList()).get(0);
        Map<String, String> memory = ParserUtils.createMemory(TestUtils.getTestDocument());
        memory.put("alter", "27");
        Page result = WorkflowUtils.getNextPage(navigationelements, page, memory);
        assertNotNull(result);
        assertEquals("Kein Kindergeld", result.getName());
    }

    @Test
    public void testGetNextPageException() {
        List<Navigationelement> navigationelements = TestUtils.getTestNavigationelements();
        Page end = (Page) navigationelements.stream().filter(element -> element.getName().equals("Ende des Antrags")).collect(Collectors.toList()).get(0);
        Exception exception = assertThrows(Exception.class, () -> {
            WorkflowUtils.getNextPage(navigationelements, end, ParserUtils.createMemory(TestUtils.getTestDocument()));
        });

        assertEquals("Keine weitere Seite gefunden.",
                exception.getMessage());
    }

    @Test
    public void testGetPreviousPage() {
        List<Navigationelement> navigationelements = TestUtils.getTestNavigationelements();
        Page page = (Page) navigationelements.stream().filter(element -> element.getName().equals("Eingabe Alter")).collect(Collectors.toList()).get(0);
        Page result = WorkflowUtils.getPreviousPage(navigationelements, page);
        assertNotNull(result);
        assertEquals("Start", result.getName());
    }

    @Test
    public void testFindPredecessor() {
        List<Navigationelement> navigationelements = TestUtils.getTestNavigationelements();
        Navigationelement navigationelement = navigationelements.stream().filter(element -> element.getName().equals("Kein Kindergeld")).collect(Collectors.toList()).get(0);
        Navigationelement result = WorkflowUtils.findPredecessor(navigationelements, navigationelement);
        assertNotNull(result);
        assertTrue(result instanceof Condition);
        assertEquals("Volljährig?", result.getName());
    }

}

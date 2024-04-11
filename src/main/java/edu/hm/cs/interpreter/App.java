package edu.hm.cs.interpreter;

import edu.hm.cs.interpreter.model.Navigationelement;
import edu.hm.cs.interpreter.model.Page;
import edu.hm.cs.interpreter.parser.ParserUtils;
import edu.hm.cs.interpreter.workflow.Option;
import edu.hm.cs.interpreter.workflow.WorkflowUtils;
import lombok.Data;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

@Data
public class App {

    private static Map<String, String> memory;
    private static List<Navigationelement> navigationelements;

    /**
     * Main Method. Reads the XML representation of a model und handles the execution of the process.
     * @param args The command line arguments. Expects the file name to be read
     */
    public static void main(String[] args) {
        try {
            Document document = App.parseModel("models/" + args[0]);

            //Create Memory
            memory = (ParserUtils.createMemory(document));

            //Connect Navigationelements with Transition
            navigationelements = ParserUtils.linkTransitionToNavigationselement(document,
                    ParserUtils.createNavigationelements(document), ParserUtils.createTransitions(document));

            //Start
            Page start = WorkflowUtils.findStart(navigationelements);

            App.run(start);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses the XML file of the model.
     * @param fileName name of the XML file
     * @return the XML representation as Document object
     * @throws Exception if the parsing fails
     */
    public static Document parseModel(String fileName) throws Exception {
        File inputFile = new File(fileName);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        return documentBuilder.parse(inputFile);
    }

    /**
     * Executes the current page and handles the available options.
     * @param currentPage The current page to execute
     * @throws Exception Throws Exception if the execution logic encounters inconsistencies
     */
    private static void run(Page currentPage) throws Exception {
        List<Option> options = App.printPage(currentPage, WorkflowUtils.isStart(navigationelements, currentPage));
        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.next();
        for (Option option : options) {
            if (option.getValue().equals(userInput)) {
                if (option.getVariableName() != null) {
                    App.printVariableChange(option);
                    userInput = scanner.next();
                    if (option.isMemory()) {
                        memory.put(option.getVariableName(), userInput);
                    } else {
                        currentPage.getVariables().put(option.getVariableName(), userInput);
                    }
                    run(currentPage);
                } else if (option.isNext()){
                    run(WorkflowUtils.getNextPage(navigationelements, currentPage, memory));
                } else if (option.isBack()) {
                    run(WorkflowUtils.getPreviousPage(navigationelements, currentPage));
                } else {
                    System.out.println("Programm beendet");
                }
            }
        }
    }

    /**
     * Prints a page on the console and evaluates the available options on this page.
     * @param page Page to be printed
     * @param start Value that determines whether it is a start page
     * @return List of Options available on this page. Also displays the content of the page on the console
     */
    private static List<Option> printPage(Page page, boolean start) {
        List<Option> options = new ArrayList<>();
        System.out.println(page.getName().toUpperCase());
        System.out.println(page.getDescription());
        System.out.println("-------------------------");
        System.out.println("Optionen:");
        int i = 1;
        for(Map.Entry<String, String> entry : memory.entrySet()) {
            options.add(new Option(String.valueOf(i), entry.getKey(), true, false, false));
            System.out.println(i + ": globale Variable " + entry.getKey().substring(0, 1).toUpperCase()
                    + entry.getKey().substring(1)
                    + " ändern. Aktueller Wert: " + entry.getValue());
            i++;
        }
        for(Map.Entry<String, String> entry : page.getVariables().entrySet()) {
            options.add(new Option(String.valueOf(i), entry.getKey(), false, false, false));
            System.out.println(i + ": lokale Variable " + entry.getKey().substring(0, 1).toUpperCase()
                    + entry.getKey().substring(1)
                    + " ändern. Aktueller Wert: " + entry.getValue());
            i++;
        }
        if (!start) {
            options.add(new Option(String.valueOf(i), null, false, false, true));
            System.out.println(i + ": Zurück");
            i++;
        }
        if (!page.getTransition().isEmpty()) {
            options.add(new Option(String.valueOf(i), null, false, true, false));
            System.out.println(i + ": Weiter");
        } else {
            options.add(new Option(String.valueOf(i), null, false, false, false));
            System.out.println(i + ": Programm beenden");
        }
        return options;
    }

    /**
     * Prints the option to change a variable.
     * @param option Option to print
     */
    private static void printVariableChange(Option option) {
        System.out.println("Geben Sie den neuen Wert für die " + (option.isMemory() ? "globale" : "lokale")
                + " Variable " + option.getVariableName() + " ein:");
    }

}

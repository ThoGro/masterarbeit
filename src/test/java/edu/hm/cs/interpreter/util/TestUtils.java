package edu.hm.cs.interpreter.util;

import edu.hm.cs.interpreter.App;
import edu.hm.cs.interpreter.model.Navigationelement;
import edu.hm.cs.interpreter.parser.ParserUtils;
import org.w3c.dom.Document;

import java.util.List;

public class TestUtils {

    private static final String PATH_TO_TEST_MODEL = "src/test/resources/";
    private static final String DEFAULT_FILENAME = "test.xml";

    public static Document getTestDocument() {
        Document document = null;
        try {
            document = App.parseModel(PATH_TO_TEST_MODEL + DEFAULT_FILENAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }

    public static Document getTestDocument(String filename) {
        Document document = null;
        try {
            document = App.parseModel(PATH_TO_TEST_MODEL + filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }

    public static List<Navigationelement> getTestNavigationelements() {
        Document document = getTestDocument();
        return ParserUtils.linkTransitionToNavigationselement(document,
                ParserUtils.createNavigationelements(document), ParserUtils.createTransitions(document));
    }

    public static List<Navigationelement> getTestNavigationelements(String filename) {
        Document document = getTestDocument(filename);
        return ParserUtils.linkTransitionToNavigationselement(document,
                ParserUtils.createNavigationelements(document), ParserUtils.createTransitions(document));
    }

}

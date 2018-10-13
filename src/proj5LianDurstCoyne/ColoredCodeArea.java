/*
    File: ColoredCodeArea.java
    CS361 Project 5
    Names: Iris Lian, Robert Durst, and Michael Coyne
    Date: 10/12/18
*/

package proj5LianDurstCoyne;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class inherits CodeArea class, adding keyword
 * highlighting and syntax recognition.
 * It has the constructor of ColoredCodeArea and has a helper method
 * computeHighlighting which handles the highlighting of the codeArea.
 * It also contains the java keywords and patterns to be colored in the CodeArea.
 *
 *  @author Yi Feng
 *  @author Iris Lian
 */
public class ColoredCodeArea extends CodeArea
{
    /**
     * Keywords which need to be highlighted
     */
    private static final String[] KEYWORDS = new String[]{
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while", "var"
    };

    /**
     * Constituent patterns used in field PATTERN below which
     * determines syntax recognition.
     */
    private static final String KEYWORD_PATTERN =
            "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
    private static final String INTCON_PATTERN = "[0-9]";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                    + "|(?<INTCON>" + INTCON_PATTERN + ")"
    );

    /**
     * Helper method which highlights the keywords, integers and symbols, as defined
     * in the PATTERN field.
     *
     * @param text String that is in the code area
     */
    private static StyleSpans<Collection<String>> computeHighlighting(String text)
    {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find())
        {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" : matcher.group("PAREN") != null ? "paren" :
                            matcher.group("BRACE") != null ? "brace" :
                                    matcher.group("BRACKET") != null ? "bracket" :
                                            matcher.group("SEMICOLON") != null ? "semicolon" :
                                                    matcher.group("STRING") != null ? "string" :
                                                            matcher.group("COMMENT") != null ? "comment" :
                                                                    matcher.group("INTCON") != null ? "intcon" :
                                                                            null; /* never happens */
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    /**
     * Constructor which creates and sets up a colored code area.
     */
    public ColoredCodeArea()
    {
        super();

        this.setParagraphGraphicFactory(LineNumberFactory.get(this));

        //  recompute the syntax highlighting 500 ms after user stops editing area
        Subscription cleanupWhenNoLongerNeedIt = this

                //  plain changes = ignore style changes that are emitted when
                //      syntax highlighting is reapplied
                //  multi plain changes = save computation by not rerunning
                //      the code multiple times
                //  when making multiple changes (e.g. renaming a method
                //      at multiple parts in file)
                .multiPlainChanges()

                //  do not emit an event until 500 ms have passed since
                //      the last emission of previous stream
                .successionEnds(Duration.ofMillis(500))

                //  run the following code block when previous stream emits an event
                .subscribe(ignore -> this.setStyleSpans(0,
                        computeHighlighting(this.getText())));

    }
}

package de.turingfx.view;

import de.turingfx.controller.TuringMachine;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import lombok.extern.slf4j.Slf4j;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lam, Le (msg systems ag) 2018
 */
@Slf4j
public class CodeEditorDialog extends Alert {

    private static final String[] KEYWORDS = new String[] {
            "START", "END", "R", "L", "N"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String BLANK_PATTERN = "#";
    private static final String TRANSITION_PATTERN = "->";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<BLANK>" + BLANK_PATTERN + ")"
                    + "|(?<TRANSITION>" + TRANSITION_PATTERN + ")"
    );

    private static final int CODE_EDITOR_MIN_WIDTH = 350;
    private static final int CODE_EDITOR_MIN_HEIGTH = 450;

    private CodeArea codeArea;
    private ExecutorService executor;

    public CodeEditorDialog() {
        super(AlertType.NONE);
        initCodeStyleSheet();
        executor = Executors.newSingleThreadExecutor();

        GridPane content = createContent();
        super.setTitle("</turing-code>");
        super.getDialogPane().setContent(content);
        super.getDialogPane().getButtonTypes().add(ButtonType.OK);
        super.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
        super.getDialogPane().setStyle("-fx-background-color: #2E9AFE;");
    }

    private GridPane createContent() {
        GridPane content = new GridPane();
        Label labelStart = new Label("{");
        labelStart.setStyle("-fx-font-weight: bold; -fx-font-size: 18px");
        Label labelEnd = new Label("}");
        labelEnd.setStyle("-fx-font-weight: bold; -fx-font-size: 18px");
        Pane emptyCell = new Pane();
        emptyCell.setMinWidth(15);

        codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

        Subscription cleanupWhenDone = codeArea.multiPlainChanges()
                .successionEnds(Duration.ofMillis(500))
                .supplyTask(this::computeHighlightingAsync)
                .awaitLatest(codeArea.multiPlainChanges())
                .filterMap(t -> {
                    if(t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(this::applyHighlighting);

        VirtualizedScrollPane<StyleClassedTextArea> vsPane = new VirtualizedScrollPane<>(codeArea);
        vsPane.setMinWidth(CODE_EDITOR_MIN_WIDTH);
        vsPane.setMinHeight(CODE_EDITOR_MIN_HEIGTH);
        content.add(labelStart, 0, 0);
        content.add(emptyCell, 0, 1);
        content.add(vsPane, 1, 1);
        content.add(labelEnd, 0, 2);
        return content;
    }

    private void initCodeStyleSheet() {
        super.getDialogPane().getStylesheets()
                .add(getClass().getResource("/fxml/css/code-keywords.css")
                        .toExternalForm());
    }

    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        codeArea.setStyleSpans(0, highlighting);
    }

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = codeArea.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
                return computeHighlighting(text);
            }
        };
        executor.execute(task);
        return task;
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("BLANK") != null ? "blank" :
                                    matcher.group("TRANSITION") != null ? "transition" :
                                            null;
            /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    public void setCode(String code) {
        codeArea.replaceText(0, 0, code);
    }

    public void showAndWaitForResult() {
        Optional<ButtonType> result = super.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            log.debug("Set code");
            TuringMachine.getInstance().setCodeStr(codeArea.getText());
        } else {
            log.debug("discard code changes");
            // ... user chose CANCEL or closed the dialog
        }
        executor.shutdown();
    }
}
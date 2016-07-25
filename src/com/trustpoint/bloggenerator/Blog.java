package com.trustpoint.bloggenerator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

/**
 * Handle a single blog
 *
 * @author zli
 *
 */
public class Blog extends JFrame
{
    private static final long serialVersionUID = 131029660448982933L;

    // GUI elements
    private JFrame blogFrame;
    private JTextArea editor;
    private JTextArea lineNumber;

    private JTextField fileNameInput;
    private JTextField titleInput;
    private JTextField dateInput;
    private JComboBox<String> authorInput;
    private JTextField categoriesInput;
    private JComboBox<String> categoryList;
    private JTextField addAbbrInputShort;
    private JTextField addAbbrInputFull;

    private FlowLayout inputFlowLayout;
    private JPanel abbrPanel;
    private Dimension buttonDimension;
    private Dimension abbrRecordShortDimension;
    private Dimension abbrRecordFullDimension;
    private List<JTextField> abbrShortList;
    private List<JTextField> abbrFullList;

    // User inputs
    private String oldFileName;
    private String fileName;
    private String title;
    private String date;
    private Author author;
    private List<String> categories;
    private String excerptImg;
    private String excerptImgAlt;
    private Abbr abbr;

    // Parse elements
    private List<Paragraph> paragraphs;
    private File file;
    private XWPFDocument document;
    private XWPFParagraph currentParagraph;
    private XWPFRun currentRun;
    private ParagraphStyle lastParagraphStyle;
    private ParagraphStyle currentParagraphStyle;
    private RunStyle lastRunStyle;
    private RunStyle currentRunStyle;

    public Blog()
    {
        this.oldFileName = "";
        this.fileName = "";
        this.title = "";
        this.date = "";
        this.author = new Author();
        this.categories = new ArrayList<String>();
        this.excerptImg = "";
        this.excerptImgAlt = "";
        this.abbr = new Abbr();

        this.paragraphs = new ArrayList<Paragraph>();
    }

    public void initFromDocxFile(File file)
    {
        this.file = file;

        // Build GUI
        initBlogFrame();

        // Parse DOCS file
        parseDOCXFile();

        // Compact runs
        compactRuns();

        // Format content
        trimSpace();
        replaceHTMLChars();

        // Set or generate inputs
        initInput();

        // Display the generated blog in editor
        displayBlog();
    }

    /**
     * Build the main frame
     */
    private void initBlogFrame()
    {
        blogFrame = new JFrame(Value.TITLE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        blogFrame.setSize(screenSize.width, screenSize.height);
        blogFrame.setLocationRelativeTo(null);
        blogFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        blogFrame.setLayout(new GridLayout(1, 2));
        blogFrame.addComponentListener(new ResizeEventListener());

        // The text editor on the left side of the frame
        // ----------START editoralPanel---------- //
        JPanel editorPanel = new JPanel(new FlowLayout());

        // 3 digits for line number is enough
        lineNumber = new MyTextArea(Value.EDITOR_INITIAL_LINES, 3);

        // Create a monospaced font
        Font font = new Font("monospaced", Font.PLAIN, lineNumber.getFont().getSize());

        lineNumber.setFont(font);
        lineNumber.setEditable(false);
        editorPanel.add(lineNumber);

        editor = new MyTextArea(Value.EDITOR_INITIAL_LINES, Value.LINE_LENGTH + 10);
        editor.setFont(font);
        editor.getDocument().addDocumentListener(new EditorListener());
        editorPanel.add(editor);

        // Update line number
        updateLineNumber();

        JScrollPane editorScroller = new JScrollPane(editorPanel);
        blogFrame.add(editorScroller);
        // -----------END editoralPanel----------- //

        // The operation panel on the right side of the frame
        // ----------START operationPanel---------- //
        JPanel operationPanel = new JPanel(new BorderLayout());

        // The input panel on the top right side of the frame
        // ----------START inputPanel---------- //
        JPanel inputPanel = new JPanel(new GridLayout(8, 1));

        inputFlowLayout = new FlowLayout(FlowLayout.LEFT);
        inputFlowLayout.setHgap(Value.FLOWLAYOUT_GAP);

        JPanel inputPanel1 = new JPanel(inputFlowLayout);
        JPanel inputPanel2 = new JPanel(inputFlowLayout);
        JPanel inputPanel3 = new JPanel(inputFlowLayout);
        JPanel inputPanel4 = new JPanel(inputFlowLayout);

        // Labels
        JLabel categoriesLabel = new JLabel("Categories:", JLabel.RIGHT);
        Dimension labelDimension = categoriesLabel.getMinimumSize();
        categoriesLabel.setPreferredSize(labelDimension);
        JLabel fileNameLabel = new JLabel("File Name:", JLabel.RIGHT);
        fileNameLabel.setPreferredSize(labelDimension);
        JLabel titleLabel = new JLabel("Title:", JLabel.RIGHT);
        titleLabel.setPreferredSize(labelDimension);
        JLabel authorLabel = new JLabel("Author:", JLabel.RIGHT);
        authorLabel.setPreferredSize(labelDimension);
        JLabel dateLabel = new JLabel("Date:", JLabel.RIGHT);
        dateLabel.setPreferredSize(labelDimension);

        // Inputs
        fileNameInput = new JTextField();
        Dimension inputDimension = new Dimension(
                (screenSize.width) / 2 - Value.FLOWLAYOUT_GAP * 3 - labelDimension.width,
                fileNameInput.getPreferredSize().height);
        Dimension inputDimensionHalf = new Dimension(
                ((screenSize.width) / 2 - Value.FLOWLAYOUT_GAP * 5 - labelDimension.width * 2) / 2,
                fileNameInput.getPreferredSize().height);
        fileNameInput.setPreferredSize(inputDimension);
        titleInput = new JTextField();
        titleInput.getDocument().addDocumentListener(new FileNameListener());
        titleInput.setPreferredSize(inputDimension);
        authorInput = new JComboBox<String>(AuthorList.nameList.toArray((new String[0])));
        authorInput.setPreferredSize(inputDimensionHalf);
        dateInput = new JTextField();
        dateInput.getDocument().addDocumentListener(new FileNameListener());
        dateInput.setPreferredSize(inputDimensionHalf);
        categoriesInput = new JTextField();
        categoriesInput.setPreferredSize(inputDimensionHalf);
        categoryList = new JComboBox<String>(CategoryList.categoryList.toArray((new String[0])));
        categoryList.setPreferredSize(inputDimensionHalf);
        JButton addCategoryButton = new JButton("Add Category");
        addCategoryButton.addActionListener(new AddCategoryButtonListener());
        buttonDimension = addCategoryButton.getMinimumSize();
        Dimension categoryListDimension = new Dimension(
                (screenSize.width) / 2 - Value.FLOWLAYOUT_GAP * 5 - labelDimension.width
                        - inputDimensionHalf.width - buttonDimension.width,
                inputDimension.height);
        categoryList.setPreferredSize(categoryListDimension);

        inputPanel1.add(fileNameLabel);
        inputPanel1.add(fileNameInput);
        inputPanel.add(inputPanel1);
        inputPanel2.add(titleLabel);
        inputPanel2.add(titleInput);
        inputPanel.add(inputPanel2);
        inputPanel3.add(dateLabel);
        inputPanel3.add(dateInput);
        inputPanel3.add(authorLabel);
        inputPanel3.add(authorInput);
        inputPanel.add(inputPanel3);
        inputPanel4.add(categoriesLabel);
        inputPanel4.add(categoriesInput);
        inputPanel4.add(categoryList);
        inputPanel4.add(addCategoryButton);
        inputPanel.add(inputPanel4);

        // Add an empty line
        inputPanel.add(new JLabel(""));

        // Abbreviation section
        JPanel inputPanel5 = new JPanel(inputFlowLayout);
        JPanel inputPanel6 = new JPanel(inputFlowLayout);
        JPanel inputPanel7 = new JPanel(inputFlowLayout);

        JLabel abbrLabel = new JLabel("Abbreviation:", JLabel.LEFT);
        Dimension abbrLabelDimension = abbrLabel.getMinimumSize();
        abbrLabelDimension.width = buttonDimension.width;
        addAbbrInputShort = new JTextField();
        abbrRecordShortDimension = new Dimension(buttonDimension.width,
                addAbbrInputShort.getPreferredSize().height);
        abbrRecordFullDimension = new Dimension(
                (screenSize.width) / 2 - Value.FLOWLAYOUT_GAP * 4 - buttonDimension.width * 2,
                addAbbrInputShort.getPreferredSize().height);
        addAbbrInputShort.setPreferredSize(abbrRecordShortDimension);
        addAbbrInputFull = new JTextField();
        addAbbrInputFull.setPreferredSize(abbrRecordFullDimension);
        JButton addAbbrButton = new JButton("Add Abbr");
        addAbbrButton.setPreferredSize(buttonDimension);
        addAbbrButton.addActionListener(new AddAbbrButtonListener());
        JLabel abbrShortLabel = new JLabel("Short Form:", JLabel.LEFT);
        abbrShortLabel.setPreferredSize(abbrLabelDimension);
        JLabel abbrFullLabel = new JLabel("Full Form:", JLabel.LEFT);

        inputPanel5.add(abbrLabel);
        inputPanel.add(inputPanel5);
        inputPanel6.add(abbrShortLabel);
        inputPanel6.add(abbrFullLabel);
        inputPanel.add(inputPanel6);
        inputPanel7.add(addAbbrInputShort);
        inputPanel7.add(addAbbrInputFull);
        inputPanel7.add(addAbbrButton);
        inputPanel.add(inputPanel7);

        operationPanel.add(inputPanel, BorderLayout.PAGE_START);
        // -----------END inputPanel----------- //

        // The abbreviation panel on the center right side of the frame
        abbrPanel = new JPanel();
        operationPanel.add(abbrPanel, BorderLayout.CENTER);

        // The button panel on the bottom right side of the frame
        // ----------START bottonPanel---------- //
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));

        FlowLayout buttonFlowLayout = new FlowLayout(FlowLayout.CENTER);

        JPanel buttonPanel1 = new JPanel(buttonFlowLayout);
        JPanel buttonPanel2 = new JPanel(buttonFlowLayout);

        // Buttons
        JButton uploadImgButton = new JButton("Upload Image");
        uploadImgButton.addActionListener(new UploadImgButtonListener());
        JButton addImgExcerptButton = new JButton("Add/Update Excerpt Image");
        addImgExcerptButton.addActionListener(new AddImgExcerptButtonListener());
        buttonPanel1.add(addImgExcerptButton);
        JButton addImgBlogButton = new JButton("Add Image To Blog");
        addImgBlogButton.addActionListener(new AddImgBlogButtonListener());
        JButton updateChangesButton = new JButton("Update Changes");
        updateChangesButton.addActionListener(new UpdateChangesButtonListener());
        JButton writeButton = new JButton("Write to File");
        writeButton.addActionListener(new WriteButtonListener());

        buttonPanel1.add(uploadImgButton);
        buttonPanel1.add(addImgExcerptButton);
        buttonPanel1.add(addImgBlogButton);
        buttonPanel.add(buttonPanel1);
        buttonPanel2.add(updateChangesButton);
        buttonPanel2.add(writeButton);
        buttonPanel.add(buttonPanel2);

        operationPanel.add(buttonPanel, BorderLayout.PAGE_END);
        // -----------END buttonPanel----------- //

        blogFrame.add(operationPanel);
        // -----------END operationPanel----------- //

        blogFrame.setVisible(true);
    }

    /**
     * Build the panel for abbreviation
     */
    private void initAbbrPanel()
    {
        abbrPanel.removeAll();
        abbrPanel.setLayout(inputFlowLayout);

        abbrShortList = new ArrayList<JTextField>();
        abbrFullList = new ArrayList<JTextField>();

        for (Map.Entry<String, String> abbrRecord : abbr.getList().entrySet()) {
            JTextField abbrRecordShort = new JTextField(abbrRecord.getKey());
            abbrRecordShort.setPreferredSize(abbrRecordShortDimension);
            abbrRecordShort.setEditable(false);
            abbrShortList.add(abbrRecordShort);

            JTextField abbrRecordFull = new JTextField(abbrRecord.getValue());
            abbrRecordFull.setPreferredSize(abbrRecordFullDimension);
            abbrFullList.add(abbrRecordFull);

            JButton deleteAbbrButton = new JButton("Delete Abbr");
            deleteAbbrButton.setPreferredSize(buttonDimension);
            deleteAbbrButton.addActionListener(new DeleteAbbrButtonListener());
            deleteAbbrButton.setActionCommand(abbrRecord.getKey());

            abbrPanel.add(abbrRecordShort);
            abbrPanel.add(abbrRecordFull);
            abbrPanel.add(deleteAbbrButton);

            int emptyWidth = blogFrame.getSize().width / 2 - abbrRecordShortDimension.width
                    - abbrRecordFullDimension.width - buttonDimension.width
                    - Value.FLOWLAYOUT_GAP * 4;

            // Fill the empty space on the right of each line
            if (emptyWidth > 0) {
                JLabel emptyLabel = new JLabel();
                emptyLabel.setPreferredSize(new Dimension(emptyWidth - Value.FLOWLAYOUT_GAP,
                        emptyLabel.getPreferredSize().height));
                abbrPanel.add(emptyLabel);
            }
        }

        blogFrame.revalidate();
        blogFrame.repaint();
    }

    /**
     * Parse DOCX file
     */
    private void parseDOCXFile()
    {
        // Parse each paragraph
        try {
            FileInputStream fis = new FileInputStream(file);
            document = new XWPFDocument(fis);
            List<XWPFParagraph> docxParagraphs = document.getParagraphs();
            lastParagraphStyle = new ParagraphStyle();
            lastParagraphStyle.setDefaultStyle();
            currentParagraphStyle = new ParagraphStyle();
            for (int i = 0; i < docxParagraphs.size(); i++) {
                currentParagraph = docxParagraphs.get(i);
                String paragraphText = currentParagraph.getParagraphText();
                if (!StringUtils.isBlank(paragraphText)) {
                    currentParagraphStyle.setDefaultStyle();
                    Paragraph paragraph = new Paragraph();
                    paragraph.runs = getParagraphRunsAndStyle();
                    setParagraphTags();
                    switch (currentParagraphStyle.style) {
                    case Value.PARAGRAPH_STYLE_BULLET:
                    case Value.PARAGRAPH_STYLE_DECIMAL:
                        paragraph.openTag = Value.HTML_LIST_OPEN;
                        paragraph.closeTag = Value.HTML_LIST_CLOSE;
                        break;
                    case Value.PARAGRAPH_STYLE_HEADER:
                        paragraph.openTag = Value.HTML_HEADER_OPEN;
                        paragraph.closeTag = Value.HTML_HEADER_CLOSE;
                        break;
                    case Value.PARAGRAPH_STYLE_PLAIN:
                        paragraph.openTag = Value.HTML_PARAGRAPH_OPEN;
                        paragraph.closeTag = Value.HTML_PATAGRAPH_CLOSE;
                        break;
                    }
                    paragraphs.add(paragraph);
                    updateParagraphStyle();
                }
            }
            setLastParagraphTags();
            document.close();
        } catch (Exception e) {
            Error error = new Error();
            error.initErrorFrame("Exception parsing DOCX file.\n" + e.getMessage());
        }
    }

    /**
     * Compact runs without tags
     */
    private void compactRuns()
    {
        for (int i = 0; i < paragraphs.size(); i++) {
            if (paragraphs.get(i).runs.size() > 1) {
                int runIndex = 1;
                while (runIndex < paragraphs.get(i).runs.size()) {
                    if (paragraphs.get(i).runs.get(runIndex - 1).closeTag.equals("")
                            && paragraphs.get(i).runs.get(runIndex).openTag.equals("")) {
                        paragraphs.get(i).runs.get(runIndex - 1).text += paragraphs.get(i).runs
                                .get(runIndex).text;
                        paragraphs.get(i).runs.get(runIndex - 1).closeTag = paragraphs.get(i).runs
                                .get(runIndex).closeTag;
                        paragraphs.get(i).runs.remove(runIndex);
                    } else {
                        runIndex++;
                    }
                }
            }
        }
    }

    /**
     * Close the tag of the last paragraph and open the tag of current paragraph according to their
     * styles
     */
    private void setParagraphTags()
    {
        if (!lastParagraphStyle.style.equals(currentParagraphStyle.style)) {
            if (lastParagraphStyle.style.equals(Value.PARAGRAPH_STYLE_BULLET)) {
                Paragraph bulletClose = new Paragraph();
                bulletClose.closeTag = Value.HTML_BULLET_LIST_CLOSE;
                paragraphs.add(bulletClose);
            } else if (lastParagraphStyle.style.equals(Value.PARAGRAPH_STYLE_DECIMAL)) {
                Paragraph numberClose = new Paragraph();
                numberClose.closeTag = Value.HTML_NUMBER_LIST_CLOSE;
                paragraphs.add(numberClose);
            }
            if (currentParagraphStyle.style.equals(Value.PARAGRAPH_STYLE_BULLET)) {
                Paragraph bulletOpen = new Paragraph();
                bulletOpen.openTag = Value.HTML_BULLET_LIST_OPEN;
                paragraphs.add(bulletOpen);
            } else if (currentParagraphStyle.style.equals(Value.PARAGRAPH_STYLE_DECIMAL)) {
                Paragraph numberOpen = new Paragraph();
                numberOpen.openTag = Value.HTML_NUMBER_LIST_OPEN;
                paragraphs.add(numberOpen);
            }
        }
    }

    /**
     * Set the style of lastParagraphSytle to current paragraph's style for parsing next paragraph
     */
    private void updateParagraphStyle()
    {
        lastParagraphStyle.style = currentParagraphStyle.style;
    }

    /**
     * CLose the tag of the last paragraph
     */
    private void setLastParagraphTags()
    {
        if (lastParagraphStyle.style.equals(Value.PARAGRAPH_STYLE_BULLET)) {
            Paragraph bulletClose = new Paragraph();
            bulletClose.closeTag = Value.HTML_BULLET_LIST_CLOSE;
            paragraphs.add(bulletClose);
        } else if (lastParagraphStyle.style.equals(Value.PARAGRAPH_STYLE_DECIMAL)) {
            Paragraph numberClose = new Paragraph();
            numberClose.closeTag = Value.HTML_NUMBER_LIST_CLOSE;
            paragraphs.add(numberClose);
        }
    }

    /**
     * Get the style and content of current paragraph
     *
     * @return List<Run> A list of generated runs in that paragraph
     */
    private List<Run> getParagraphRunsAndStyle()
    {
        List<XWPFRun> docxRuns = currentParagraph.getRuns();

        // Set paragraph style
        String numfmt = currentParagraph.getNumFmt();
        if (numfmt != null) {
            currentParagraphStyle.style = numfmt;
        } else if (docxRuns.get(0).isBold()) {
            currentParagraphStyle.style = Value.PARAGRAPH_STYLE_HEADER;
        } else {
            currentParagraphStyle.style = Value.PARAGRAPH_STYLE_PLAIN;
        }

        // Set paragraph runs
        List<Run> tempRuns = new ArrayList<Run>();
        lastRunStyle = new RunStyle();
        lastRunStyle.setDefaultStyle();
        currentRunStyle = new RunStyle();
        currentRunStyle.setDefaultStyle();
        for (int i = 0; i < docxRuns.size(); i++) {
            currentRun = docxRuns.get(i);
            getRunStyle();
            Run newRun = new Run();
            tempRuns.add(newRun);
            if (i == 0) {
                setFirstRunTags(tempRuns.get(i));
            } else {
                setRunTags(tempRuns.get(i - 1), tempRuns.get(i));
            }
            if (currentRun.toString() != null) {
                if (currentParagraphStyle.equals(Value.PARAGRAPH_STYLE_HEADER)) {
                    newRun.text = capitalize(currentRun.toString());
                }
                else {
                    newRun.text = currentRun.toString();
                }
            }
            updateRunStyle();
        }
        setLastRunTags(tempRuns.get(tempRuns.size() - 1));

        return tempRuns;
    }

    /**
     * Get the style of current run
     */
    private void getRunStyle()
    {
        if (currentRun instanceof XWPFHyperlinkRun) {
            currentRunStyle.isHyperLink = true;
            currentRunStyle.url = ((XWPFHyperlinkRun) currentRun).getHyperlink(document).getURL();
        } else {
            currentRunStyle.isHyperLink = false;
            currentRunStyle.url = "";
        }

        currentRunStyle.script = currentRun.getSubscript();
    }

    /**
     * Set the style of first run in the paragraph
     *
     * @param run
     *            The run to set style
     */
    private void setFirstRunTags(Run run)
    {
        if (currentRunStyle.isHyperLink) {
            run.openTag += Value.HTML_LINK_OPEN(currentRunStyle.url);
        }

        if (currentRunStyle.script.equals(VerticalAlign.SUBSCRIPT)) {
            run.openTag += Value.HTML_SUB_SCRIPT_OPEN;
        } else if (currentRunStyle.script.equals(VerticalAlign.SUPERSCRIPT)) {
            run.openTag += Value.HTML_SUPER_SCRIPT_OPEN;
        }
    }

    /**
     * Close the tag of last run and open the tag of current run according to their styles
     *
     * @param lastRun
     * @param currentRun
     */
    private void setRunTags(Run lastRun, Run currentRun)
    {
        if (!lastRunStyle.script.equals(currentRunStyle.script)) {
            if (lastRunStyle.script.equals(VerticalAlign.SUBSCRIPT)) {
                lastRun.closeTag += Value.HTML_SUB_SCRIPT_CLOSE;
            } else if (lastRunStyle.script.equals(VerticalAlign.SUPERSCRIPT)) {
                lastRun.closeTag += Value.HTML_SUPER_SCRIPT_CLOSE;
            }
        }

        if (lastRunStyle.isHyperLink != currentRunStyle.isHyperLink) {
            if (lastRunStyle.isHyperLink) {
                lastRun.closeTag += Value.HTML_LINK_CLOSE;
            }

            if (currentRunStyle.isHyperLink) {
                currentRun.openTag += Value.HTML_LINK_OPEN(currentRunStyle.url);
            }
        }

        if (!lastRunStyle.script.equals(currentRunStyle.script)) {
            if (currentRunStyle.script.equals(VerticalAlign.SUBSCRIPT)) {
                currentRun.openTag += Value.HTML_SUB_SCRIPT_OPEN;
            } else if (currentRunStyle.script.equals(VerticalAlign.SUPERSCRIPT)) {
                currentRun.openTag += Value.HTML_SUPER_SCRIPT_OPEN;
            }
        }
    }

    /**
     * Set lastRunStyle to current run's style to parse next run
     */
    private void updateRunStyle()
    {
        lastRunStyle.isHyperLink = currentRunStyle.isHyperLink;
        lastRunStyle.url = currentRunStyle.url;
        lastRunStyle.script = currentRunStyle.script;
    }

    /**
     * CLose tag of last run in the paragraph
     *
     * @param run
     *            The run to close tag
     */
    private void setLastRunTags(Run run)
    {
        if (!lastRunStyle.script.equals(VerticalAlign.BASELINE)) {
            if (lastRunStyle.script.equals(VerticalAlign.SUBSCRIPT)) {
                run.closeTag += Value.HTML_SUB_SCRIPT_CLOSE;
            } else if (lastRunStyle.script.equals(VerticalAlign.SUPERSCRIPT)) {
                run.closeTag += Value.HTML_SUPER_SCRIPT_CLOSE;
            }
        }

        if (lastRunStyle.isHyperLink) {
            run.closeTag += Value.HTML_LINK_CLOSE;
        }
    }

    /**
     * Set or generate all possible inputs after DOCX file is parsed
     */
    private void initInput()
    {
        title = getTitleFromFile();
        titleInput.setText(title);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        date = dateFormat.format(currentDate);
        dateInput.setText(date);

        getCategoriesInput();

        getAbbrInput();
        initAbbrPanel();
        setAbbrInput();
    }

    /**
     * Parse the generated blog to get abbreviatons
     */
    private void getAbbrInput()
    {
        for (int i = 0; i < paragraphs.size(); i++) {
            for (int j = 0; j < paragraphs.get(i).runs.size(); j++) {
                String[] list = StringUtils.split(paragraphs.get(i).runs.get(j).text);
                for (int k = 0; k < list.length; k++) {
                    String word = list[k];

                    // Abbreviations are like "IT", "M2M", "IoT", "ECC", "U.S."
                    // Abbreviation must have a length equal or greater than 2
                    // The word may have leading or trailing punctuation
                    while (word.length() > 1 && !Character.isLetter(word.charAt(0))) {
                        word = word.substring(1);
                    }
                    while (word.length() > 1
                            && !Character.isLetter(word.charAt(word.length() - 1))) {
                        word = word.substring(0, word.length() - 1);
                    }
                    if (word.length() > 1 && Character.isUpperCase(word.charAt(0))
                            && Character.isUpperCase(word.charAt(word.length() - 1))) {
                        if (word.contains(".")) {
                            word += ".";
                        }
                        abbr.add(word);
                    }
                }
            }
        }
    }

    /**
     * Set abbreviations in blog according this.abbr
     */
    private void setAbbrInput()
    {
        for (int i = 0; i < paragraphs.size(); i++) {
            int runIndex = 0;
            while (runIndex < paragraphs.get(i).runs.size()) {
                Run tempRun = paragraphs.get(i).runs.get(runIndex);
                // If it is already in abbr tag, delete or update
                if (tempRun.closeTag.equals(Value.HTML_ABBR_CLOSE)) {
                    if (abbr.contains(tempRun.text)) {
                        tempRun.openTag = Value.HTML_ABBR_OPEN(abbr.fullForm(tempRun.text));
                    } else {
                        tempRun.openTag = "";
                        tempRun.closeTag = "";
                    }
                }
                // Else add abbr tag if text contains short form
                else {
                    String originalRunText = tempRun.text;
                    int abbrIndex = tempRun.text.length();
                    String abbrToAdd = "";
                    // Get the first abbr in text
                    for (Map.Entry<String, String> abbrRecord : abbr.getList().entrySet()) {
                        int pos = originalRunText.indexOf(abbrRecord.getKey());
                        if (pos != -1 && pos < abbrIndex) {
                            abbrIndex = pos;
                            abbrToAdd = abbrRecord.getKey();
                        }
                    }
                    if (!abbrToAdd.equals("")) {
                        tempRun.text = originalRunText.substring(0, abbrIndex);

                        Run newContentRun = new Run();
                        newContentRun.closeTag = tempRun.closeTag;
                        tempRun.closeTag = "";
                        newContentRun.text = originalRunText
                                .substring(abbrIndex + abbrToAdd.length());

                        Run abbrRun = new Run();
                        abbrRun.openTag = Value.HTML_ABBR_OPEN(abbr.fullForm(abbrToAdd));
                        abbrRun.text = abbrToAdd;
                        abbrRun.closeTag = Value.HTML_ABBR_CLOSE;

                        // Add new runs to paragraph
                        paragraphs.get(i).runs.add(runIndex + 1, abbrRun);
                        paragraphs.get(i).runs.add(runIndex + 2, newContentRun);

                        // Skip the added abbr run
                        runIndex++;
                    }
                }
                runIndex++;
            }
        }
    }

    /**
     * Generate categories tag from blog content
     */
    private void getCategoriesInput()
    {
        categories.clear();

        for (Map.Entry<String, Integer> catRecord : CategoryList.categoryCount.entrySet()) {
            catRecord.setValue(0);
        }

        for (int i = 0; i < paragraphs.size(); i++) {
            for (int j = 0; j < paragraphs.get(i).runs.size(); j++) {
                String text = paragraphs.get(i).runs.get(j).text;
                for (Map.Entry<String, Integer> catRecord : CategoryList.categoryCount.entrySet()) {
                    if (StringUtils.containsIgnoreCase(text, catRecord.getKey())) {
                        catRecord.setValue(catRecord.getValue() + 1);
                    }
                }
            }
        }

        for (Map.Entry<String, Integer> catRecord : CategoryList.categoryCount.entrySet()) {
            if (catRecord.getValue() > 0 && !categories.contains(catRecord.getKey())) {
                categories.add(catRecord.getKey());
            }
        }

        Collections.sort(categories);
        categoriesInput.setText(StringUtils.join(categories, " "));
    }

    /**
     * Get title string from file name
     *
     * @return A string representing the title
     */
    private String getTitleFromFile()
    {
        String docxFileName = file.getName();
        int extPos = docxFileName.lastIndexOf('.');
        if (extPos != -1) {
            docxFileName = docxFileName.substring(0, extPos);
        }
        docxFileName = docxFileName.replaceAll("\\s+", " ");
        docxFileName = StringUtils.trim(docxFileName);
        return capitalize(docxFileName);
    }

    /**
     * Capitalize the first letter in words in given string if the word in not in LowercaseWordList
     *
     * @param str
     *            The String containing words
     * @return Capitalized string
     */
    private String capitalize(String str)
    {
        String result = "";
        if (!StringUtils.isBlank(str)) {
            if (str.charAt(0) == ' ') {
                result += " ";
            }
            String[] list = StringUtils.split(str);
            for (int i = 0; i < list.length; i++) {
                if (!LowercaseWordList.lowercaseWordList.contains(list[i])) {
                    list[i] = list[i].substring(0, 1).toUpperCase() + list[i].substring(1);
                }
                result += list[i] + " ";
            }
            if (str.charAt(str.length() - 1) != ' ') {
                result = StringUtils.stripEnd(result, " ");
            }
        }
        return result;
    }

    /**
     * Get rid of tab and duplicated spaces
     */
    private void trimSpace()
    {
        for (int i = 0; i < paragraphs.size(); i++) {
            for (int j = 0; j < paragraphs.get(i).runs.size(); j++) {
                // Trim out tab or duplicated spaces
                paragraphs.get(i).runs.get(j).text = paragraphs.get(i).runs.get(j).text
                        .replaceAll("\\s+", " ");

                // Trim out leading spaces
                if (j == 0 || !paragraphs.get(i).runs.get(j).openTag.equals("")) {
                    paragraphs.get(i).runs.get(j).text = StringUtils
                            .stripStart(paragraphs.get(i).runs.get(j).text, " ");
                }

                // Trim out trailing spaces
                if (j == paragraphs.get(i).runs.size() - 1
                        || !paragraphs.get(i).runs.get(j).closeTag.equals("")) {
                    paragraphs.get(i).runs.get(j).text = StringUtils
                            .stripEnd(paragraphs.get(i).runs.get(j).text, " ");
                }
            }
        }
    }

    /**
     * Replace special chars with HTML chars
     */
    private void replaceHTMLChars()
    {
        for (int i = 0; i < paragraphs.size(); i++) {
            for (int j = 0; j < paragraphs.get(i).runs.size(); j++) {
                String text = paragraphs.get(i).runs.get(j).text;
                // Need to manually replace '&' first because other chars contains '&'
                // Add an extra space is to make sure that we don't replace
                text = text.replaceAll("& ", "&amp; ");
                for (Map.Entry<String, String> htmlChar : Value.htmlCharsTable.entrySet()) {
                    text = text.replaceAll(htmlChar.getKey(), htmlChar.getValue());
                }
                paragraphs.get(i).runs.get(j).text = text;
            }
        }
    }

    /**
     * Display generated blog in editor. Break line if length longer than Value.LINE_LENGTH NB:
     * Cannot add or leave out spaces when breaking lines
     */
    private void displayBlog()
    {
        editor.setText("");

        // Generate excerpt
        int excerptIndex = 0;
        for (int i = 0; i < paragraphs.size(); i++) {
            if (paragraphs.get(i).openTag.equals(Value.HTML_PARAGRAPH_OPEN)) {
                excerptIndex = i;
                break;
            }
        }

        List<String> excerptLines = displayParagraph(Value.LINE_PREFIX, Value.LINE_LENGTH,
                paragraphs.get(excerptIndex));

        // Add header
        editor.append(Value.HEADER_START + "\n");
        editor.append(Value.HEADER_TITLE + title + "\n");
        editor.append(Value.HEADER_DATE + date + Value.HEADER_TIME + "\n");
        editor.append(Value.HEADER_AUTHOR + author.getCode() + "\n");
        editor.append(Value.HEADER_CATEGORIES + StringUtils.join(categories, " ") + "\n");
        editor.append(Value.HEADER_EXCERPT + "\n");
        for (int i = 0; i < excerptLines.size() - 1; i++) {
            editor.append(excerptLines.get(i) + "\n");
        }

        // Insert the "&hellip;" char in the last line of excerpt
        String excerptLastLine = excerptLines.get(excerptLines.size() - 1);
        if ((excerptLastLine.length() + Value.HTML_CHAR_HELLIP.length() + 1) > Value.LINE_LENGTH) {
            editor.append(excerptLastLine.substring(0,
                    excerptLastLine.length() - Value.HTML_PATAGRAPH_CLOSE.length()) + "\n");
            editor.append(Value.HTML_CHAR_HELLIP + Value.HTML_PATAGRAPH_CLOSE);
        } else {
            editor.append(excerptLastLine.substring(0,
                    excerptLastLine.length() - Value.HTML_PATAGRAPH_CLOSE.length()) + " "
                    + Value.HTML_CHAR_HELLIP + Value.HTML_PATAGRAPH_CLOSE + "\n");
        }

        if (!excerptImg.equals("") && !excerptImgAlt.equals("")) {
            editor.append(Value.HEADER_IMAGE + excerptImg + "\n");
            editor.append(Value.HEADER_IMAGE_ALT + excerptImgAlt + "\n");
        }
        editor.append(Value.HEADER_END + "\n");

        // Add blog content
        for (int i = 0; i < paragraphs.size(); i++) {
            if (!(paragraphs.get(i).openTag.equals("")
                    || paragraphs.get(i).openTag.equals(Value.HTML_LIST_OPEN))) {
                editor.append("\n");
            }

            List<String> lines;
            if (paragraphs.get(i).openTag.equals(Value.HTML_LIST_OPEN)) {
                lines = displayParagraph(Value.LINE_PREFIX, Value.LINE_LENGTH, paragraphs.get(i));
            } else {
                lines = displayParagraph("", Value.LINE_LENGTH, paragraphs.get(i));
            }

            for (int j = 0; j < lines.size(); j++) {
                editor.append(lines.get(j) + "\n");
            }
        }
    }

    /**
     * Format the content of a paragraph to a list of strings to display. Break line if length
     * longer than Value.LINE_LENGTH. NB: Cannot add or leave out spaces when breaking lines
     *
     * @param prefix
     *            The prefix of each line
     * @param lineLength
     *            Line length of each line
     * @param paragraph
     *            The paragraph to format
     * @return A list of strings
     */
    private List<String> displayParagraph(String prefix, int lineLength, Paragraph paragraph)
    {
        int contentLength = lineLength - prefix.length();
        List<String> result = new ArrayList<String>();
        String line = paragraph.openTag;
        int breakPoint = line.length();
        boolean tagOnly = true;

        for (int i = 0; i < paragraph.runs.size(); i++) {
            Run tempRun = paragraph.runs.get(i);
            if (!tempRun.openTag.equals("")) {
                if ((line.length() + tempRun.openTag.length()) > contentLength
                        && breakPoint != -1) {
                    String lineToAdd = line.substring(0, breakPoint);
                    String lineRemain = line.substring(breakPoint).trim();
                    result.add(prefix + lineToAdd);
                    line = lineRemain + tempRun.openTag;
                    if (lineRemain.length() == 0) {
                        tagOnly = true;
                        breakPoint = line.length();
                    } else {
                        tagOnly = false;
                        breakPoint = -1;
                    }
                } else {
                    line += tempRun.openTag;
                    if (tagOnly) {
                        breakPoint = line.length();
                    }
                }
            }
            if (!StringUtils.isBlank(tempRun.text)) {
                String[] wordList = StringUtils.split(tempRun.text);
                for (int k = 0; k < wordList.length; k++) {
                    if (!(k == 0 && tempRun.text.charAt(0) != ' ')) {
                        line += ' ';
                        tagOnly = false;
                        breakPoint = line.length() - 1;
                    }
                    if ((line.length() + wordList[k].length()) > contentLength
                            && breakPoint != -1) {
                        String lineToAdd = line.substring(0, breakPoint);
                        String lineRemain = line.substring(breakPoint).trim();
                        result.add(prefix + lineToAdd);
                        line = lineRemain + wordList[k];
                        tagOnly = false;
                        breakPoint = -1;
                    } else {
                        line += wordList[k];
                    }
                }
                if (tempRun.text.charAt(tempRun.text.length() - 1) == ' ') {
                    line += ' ';
                    tagOnly = false;
                    breakPoint = line.length() - 1;
                }
            }
            if (!tempRun.closeTag.equals("")) {
                if ((line.length() + tempRun.closeTag.length()) > contentLength
                        && breakPoint != -1) {
                    String lineToAdd = line.substring(0, breakPoint);
                    String lineRemain = line.substring(breakPoint).trim();
                    result.add(prefix + lineToAdd);
                    line = lineRemain + tempRun.closeTag;
                    if (lineRemain.length() == 0) {
                        tagOnly = true;
                        breakPoint = line.length();
                    } else {
                        tagOnly = false;
                        breakPoint = -1;
                    }
                } else {
                    line += tempRun.closeTag;
                    if (tagOnly) {
                        breakPoint = line.length();
                    }
                }
            }
        }
        if ((line.length() + paragraph.closeTag.length()) > contentLength && breakPoint != -1) {
            String lineToAdd = line.substring(0, breakPoint);
            String lineRemain = line.substring(breakPoint).trim();
            result.add(prefix + lineToAdd);
            line = lineRemain + paragraph.closeTag;
        } else {
            line += paragraph.closeTag;
        }
        result.add(prefix + line);
        return result;
    }

    /**
     * Validate and get user's inputs
     *
     * @return
     */
    private boolean validateAndGetInput()
    {
        if (StringUtils.isBlank(fileNameInput.getText())) {
            Error error = new Error();
            error.initErrorFrame("File name cannot be blank.");
            return false;
        } else {
            this.fileName = fileNameInput.getText();
        }

        if (StringUtils.isBlank(titleInput.getText())) {
            Error error = new Error();
            error.initErrorFrame("Title cannot be blank.");
            return false;
        } else {
            this.title = titleInput.getText();
        }

        if (StringUtils.isBlank(dateInput.getText())) {
            Error error = new Error();
            error.initErrorFrame("Date cannot be blank.");
            return false;
        } else {
            this.date = dateInput.getText();
        }

        if (StringUtils.isBlank(authorInput.getSelectedItem().toString())) {
            Error error = new Error();
            error.initErrorFrame("Author cannot be blank.");
            return false;
        } else {
            this.author.setName(authorInput.getSelectedItem().toString());
        }

        if (StringUtils.isBlank(categoriesInput.getText())) {
            Error error = new Error();
            error.initErrorFrame("Categories cannot be blank.");
            return false;
        } else {
            this.categories.clear();
            this.categories.addAll(Arrays.asList(StringUtils.split(categoriesInput.getText())));
            Collections.sort(categories);
        }

        for (int i = 0; i < abbrShortList.size(); i++) {
            String shortForm = abbrShortList.get(i).getText();
            String fullForm = abbrFullList.get(i).getText();
            if (StringUtils.isBlank(shortForm) || StringUtils.isBlank(fullForm)) {
                Error error = new Error();
                error.initErrorFrame("Abbreviation cannot be blank.");
                return false;
            } else {
                this.abbr.add(shortForm, fullForm);
            }
        }

        return true;
    }

    private String getImaAlt(String imgName)
    {
        String result = imgName;
        int extPos = result.lastIndexOf('.');
        if (extPos != -1) {
            result = result.substring(0, extPos);
        }
        result = result.replaceAll("[^A-Za-z0-9]", " ");
        result = result.replaceAll("\\s+", " ");
        result = StringUtils.trim(result);
        return capitalize(result);
    }

    // ---------- START Methods used by listeners ---------- //

    /**
     * Update line number if content in editor changes
     */
    private void updateLineNumber()
    {
        int lineCount = editor.getLineCount();
        lineNumber.setText("");
        for (int i = 1; i <= lineCount; i++) {
            lineNumber.append(Integer.toString(i));
            if (i != lineCount) {
                lineNumber.append("\n");
            }
        }
    }

    /**
     * Update file name to be saved according to blog title and date
     */
    private void updateFileName()
    {
        String tempTitle = titleInput.getText();
        tempTitle = tempTitle.replaceAll("[^A-Za-z0-9]", " ");
        tempTitle = tempTitle.replaceAll("\\s+", " ");
        String tempDate = dateInput.getText();
        String[] list = StringUtils.split(tempTitle);
        for (int i = 0; i < list.length; i++) {
            list[i] = list[i].toLowerCase();
        }
        fileNameInput.setText(tempDate + "-" + StringUtils.join(list, "-") + ".html");
    }

    /**
     * Set categoriesInput if user add category
     */
    private void addCategory()
    {
        String input = categoryList.getSelectedItem().toString();
        if (StringUtils.isBlank(input)) {
            Error error = new Error();
            error.initErrorFrame("Please choose a category to add.");
        } else {
            categories.clear();
            categories.addAll(Arrays.asList(StringUtils.split(categoriesInput.getText())));
            if (categories.contains(input)) {
                Error error = new Error();
                error.initErrorFrame("Category: \"" + input + "\" has already been added.");
            } else {
                categories.add(input);
            }
            Collections.sort(categories);
            categoriesInput.setText(StringUtils.join(categories, " "));
        }
        categoryList.setSelectedIndex(0);
    }

    /**
     * Add abbreviation to input
     */
    private void addAbbr()
    {
        String inputShortForm = addAbbrInputShort.getText();
        String inputFullForm = addAbbrInputFull.getText();
        if (StringUtils.isBlank(inputShortForm)) {
            Error error = new Error();
            error.initErrorFrame("The short form of an abbreviation cannot be empty.");
        } else {
            abbr.addAbbr(inputShortForm, inputFullForm);
            initAbbrPanel();
        }
        addAbbrInputShort.setText("");
        addAbbrInputFull.setText("");
    }

    /**
     * Delete an abbreviation
     *
     * @param shortForm
     */
    private void deleteAbbr(String shortForm)
    {
        abbr.removeAbbr(shortForm);
        initAbbrPanel();
    }

    private void uploadImg()
    {
        Path targetDir = Paths.get(Value.BASE_DIR + Value.IMAGE_SOURCE_DIR);
        if (Files.exists(targetDir) && Files.isDirectory(targetDir)) {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image only", "jpg", "png",
                    "gif");
            fileChooser.setFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                Path copyTo = Paths.get(targetDir.toString() + "/" + selectedFile.getName());
                if (Files.exists(copyTo)) {
                    Error error = new Error();
                    error.initErrorFrame(selectedFile.getName() + " already exists.");
                } else {
                    try {
                        Files.copy(selectedFile.toPath(), copyTo);
                    } catch (IOException e) {
                        Error error = new Error();
                        error.initErrorFrame("Exception copying file: " + e.toString());
                    }
                }
            }
        } else {
            Error error = new Error();
            error.initErrorFrame(targetDir.toString() + " does not exists.");
        }
    }

    private void addImgBlog()
    {
        Path targetDir = Paths.get(Value.BASE_DIR + Value.IMAGE_SOURCE_DIR);
        if (Files.exists(targetDir) && Files.isDirectory(targetDir)) {
            JFileChooser fileChooser = new JFileChooser(targetDir.toFile());
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image only", "jpg", "png",
                    "gif");
            fileChooser.setFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String imgSrc = Value.IMAGE_DIR + "/" + selectedFile.getName();
                String imgAlt = getImaAlt(selectedFile.getName());
                editor.insert("\n" + Value.HTML_IMAGE(imgSrc, imgAlt) + "\n",
                        editor.getCaretPosition());
            }
        } else {
            Error error = new Error();
            error.initErrorFrame(targetDir.toString() + " does not exists.");
        }
    }

    private void addImgExcerpt()
    {
        Path targetDir = Paths.get(Value.BASE_DIR + Value.IMAGE_SOURCE_DIR);
        if (Files.exists(targetDir) && Files.isDirectory(targetDir)) {
            JFileChooser fileChooser = new JFileChooser(targetDir.toFile());
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image only", "jpg", "png",
                    "gif");
            fileChooser.setFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                excerptImg = Value.IMAGE_DIR + "/" + selectedFile.getName();
                excerptImgAlt = getImaAlt(selectedFile.getName());
                displayBlog();
            }
        } else {
            Error error = new Error();
            error.initErrorFrame(targetDir.toString() + " does not exists.");
        }
    }

    private void updateChanges()
    {
        if (validateAndGetInput()) {
            setAbbrInput();
            displayBlog();
        }
    }

    private void write()
    {

    }
    // ----------- END Methods used by listeners ----------- //

    // ---------- START Private Classes and Action Listeners ---------- //

    private class Paragraph
    {
        public String openTag;
        public String closeTag;
        public List<Run> runs;

        public Paragraph()
        {
            openTag = "";
            closeTag = "";
            runs = new ArrayList<Run>();
        }
    }

    private class ParagraphStyle
    {
        public String style;

        public void setDefaultStyle()
        {
            this.style = Value.PARAGRAPH_STYLE_PLAIN;
        }
    }

    private class Run
    {
        public String openTag;
        public String closeTag;
        public String text;

        public Run()
        {
            this.openTag = "";
            this.closeTag = "";
            this.text = "";
        }
    }

    private class RunStyle
    {
        public boolean isHyperLink;
        public String url;
        public VerticalAlign script;

        public void setDefaultStyle()
        {
            this.isHyperLink = false;
            this.url = "";
            this.script = VerticalAlign.BASELINE;
        }
    }

    private class MyTextArea extends JTextArea
    {
        private static final long serialVersionUID = 1584484767928290057L;

        public MyTextArea(int rows, int coloumns)
        {
            super(rows, coloumns);
        }

        @Override
        public void paintComponent(Graphics g)
        {
            // Draw a red vertical ruler at line length
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.RED);
            FontMetrics fm = g2.getFontMetrics();
            int width = fm.charWidth('a');
            g2.drawLine(width * Value.LINE_LENGTH, 0, width * Value.LINE_LENGTH, getHeight());
            g2.dispose();
        }
    }

    private class ResizeEventListener implements ComponentListener
    {
        @Override
        public void componentResized(ComponentEvent e)
        {
            initAbbrPanel();
        }

        @Override
        public void componentMoved(ComponentEvent e)
        {
        }

        @Override
        public void componentShown(ComponentEvent e)
        {
        }

        @Override
        public void componentHidden(ComponentEvent e)
        {
        }
    }

    private class AddCategoryButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            addCategory();
        }
    }

    private class AddAbbrButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            addAbbr();
        }
    }

    private class DeleteAbbrButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            deleteAbbr(e.getActionCommand());
        }
    }

    private class UploadImgButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            uploadImg();
        }
    }

    private class AddImgBlogButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            addImgBlog();
        }
    }

    private class AddImgExcerptButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            addImgExcerpt();
        }
    }

    private class UpdateChangesButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            updateChanges();
        }
    }

    private class WriteButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            write();
        }
    }

    private class EditorListener implements DocumentListener
    {
        @Override
        public void removeUpdate(DocumentEvent e)
        {
            updateLineNumber();
        }

        @Override
        public void insertUpdate(DocumentEvent e)
        {
            updateLineNumber();
        }

        @Override
        public void changedUpdate(DocumentEvent e)
        {
            updateLineNumber();
        }
    }

    private class FileNameListener implements DocumentListener
    {
        @Override
        public void removeUpdate(DocumentEvent e)
        {
            updateFileName();
        }

        @Override
        public void insertUpdate(DocumentEvent e)
        {
            updateFileName();
        }

        @Override
        public void changedUpdate(DocumentEvent e)
        {
            updateFileName();
        }
    }
    // ----------- END Private Classes and Action Listeners ----------- //
}

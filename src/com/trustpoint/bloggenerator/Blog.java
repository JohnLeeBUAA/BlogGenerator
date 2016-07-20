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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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

    // User inputs
    private String oldFileName;
    private String fileName;
    private String title;
    private String date;
    private Author author;
    private String categories;
    private Abbr abbr;

    // Parse elements
    private Header header;
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
        this.categories = "";
        this.abbr = new Abbr();

        this.header = new Header();
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

    public void initFromHTMLFile(File file)
    {
        // TODO: implement this
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
        JButton addImgExcerptButton = new JButton("Add/Change Excerpt Image");
        addImgExcerptButton.addActionListener(new AddImgExcerptButtonListener());
        buttonPanel1.add(addImgExcerptButton);
        JButton addImgBlogButton = new JButton("Add Image To Blog");
        addImgBlogButton.addActionListener(new AddImgBlogButtonListener());
        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(new SaveButtonListener());
        JButton writeButton = new JButton("Write to File");
        writeButton.addActionListener(new WriteButtonListener());

        buttonPanel1.add(uploadImgButton);
        buttonPanel1.add(addImgExcerptButton);
        buttonPanel1.add(addImgBlogButton);
        buttonPanel.add(buttonPanel1);
        buttonPanel2.add(saveButton);
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

        for (Map.Entry<String, String> abbrRecord : abbr.list.entrySet()) {
            JTextField abbrRecordShort = new JTextField(abbrRecord.getKey());
            abbrRecordShort.setPreferredSize(abbrRecordShortDimension);
            abbrRecordShort.setEditable(false);

            JTextField abbrRecordFull = new JTextField(abbrRecord.getValue());
            abbrRecordFull.setPreferredSize(abbrRecordFullDimension);
            abbrRecordFull.setEditable(false);

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
        // Add header
        header.headerLines.add(Value.HEADER_START);
        header.headerLines.add(Value.HEADER_LAYOUT);
        header.headerLines.add(Value.HEADER_TITLE);
        header.headerLines.add(Value.HEADER_DATE);
        header.headerLines.add(Value.HEADER_AUTHOR);
        header.headerLines.add(Value.HEADER_CATEGORIES);
        header.headerLines.add(Value.HEADER_EXCERPT);
        header.headerLines.add(Value.HEADER_END);

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
                newRun.text = currentRun.toString();
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
        header.headerLines.set(Value.HEADER_TITLE_LINECT, Value.HEADER_TITLE + title);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        date = dateFormat.format(currentDate);
        dateInput.setText(date);
        header.headerLines.set(Value.HEADER_DATE_LINECT,
                Value.HEADER_DATE + date + Value.HEADER_TIME);

        getCategoriesInput();

        getAbbrInput();
        initAbbrPanel();
        setAbbrInput();

        setHeaderExcerpt();
    }

    private void setHeaderExcerpt()
    {

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

                    if (word.contains("IT") || word.contains("IoT") || word.contains("UW")
                            || word.contains("CEO") || word.contains("M2M")
                            || word.contains("WOW")) {
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
    }

    /**
     * Set abbreviations in blog according this.abbr
     */
    private void setAbbrInput()
    {

    }

    private void getCategoriesInput()
    {
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

        String result = "";
        for (Map.Entry<String, Integer> catRecord : CategoryList.categoryCount.entrySet()) {
            if (catRecord.getValue() > 0) {
                result += catRecord.getKey() + " ";
            }
        }
        result = result.trim();

        categoriesInput.setText(result);
        header.headerLines.set(Value.HEADER_CATEGORIES_LINECT, Value.HEADER_CATEGORIES + result);
    }

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
     * Cannot break line inside a tag
     */
    private void displayBlog()
    {
        editor.setText("");
        for (int i = 0; i < header.headerLines.size(); i++) {
            editor.append(header.headerLines.get(i) + "\n");
        }

        String line = "";
        for (int i = 0; i < paragraphs.size(); i++) {
            editor.append("\n");
            line = "";
            line += paragraphs.get(i).openTag;
            for (int j = 0; j < paragraphs.get(i).runs.size(); j++) {
                if ((line + paragraphs.get(i).runs.get(j).openTag).length() > Value.LINE_LENGTH) {
                    editor.append(StringUtils.stripEnd(line, " ") + "\n");
                    line = paragraphs.get(i).runs.get(j).openTag;
                } else {
                    line += paragraphs.get(i).runs.get(j).openTag;
                }

                String runText = paragraphs.get(i).runs.get(j).text;

                if (!StringUtils.isBlank(runText)) {
                    String[] wordList = StringUtils.split(runText);

                    if (runText.charAt(0) == ' ') {
                        wordList[0] = " " + wordList[0];
                    }
                    for (int k = 1; k < wordList.length; k++) {
                        wordList[k] = " " + wordList[k];
                    }
                    for (int k = 0; k < wordList.length; k++) {
                        if ((line + wordList[k]).length() > Value.LINE_LENGTH) {
                            editor.append(StringUtils.stripEnd(line, " ") + "\n");
                            line = StringUtils.trim(wordList[k]);
                        } else {
                            line += wordList[k];
                        }
                    }
                    if (runText.charAt(runText.length() - 1) == ' ') {
                        line += " ";
                    }
                }

                if ((line + paragraphs.get(i).runs.get(j).closeTag).length() > Value.LINE_LENGTH) {
                    editor.append(StringUtils.stripEnd(line, " ") + "\n");
                    line = paragraphs.get(i).runs.get(j).closeTag;
                } else {
                    line += paragraphs.get(i).runs.get(j).closeTag;
                }
            }
            if ((line + paragraphs.get(i).closeTag).length() > Value.LINE_LENGTH) {
                editor.append(StringUtils.stripEnd(line, " ") + "\n");
                line = paragraphs.get(i).closeTag;
            } else {
                line += paragraphs.get(i).closeTag;
            }
            editor.append(StringUtils.stripEnd(line, " ") + "\n");
        }
    }

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
        if (input.equals("")) {
            Error error = new Error();
            error.initErrorFrame("Please choose a category to add.");
        } else {
            categories = categoriesInput.getText();
            if (categories.equals("")) {
                categories = input;
            } else if (categories.contains(input)) {
                Error error = new Error();
                error.initErrorFrame("Category: \"" + input + "\" has already been added.");
            } else {
                categories += " " + input;
            }
            categoriesInput.setText(categories);
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
        if (inputShortForm.equals("")) {
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

    }

    private void addImgBlog()
    {

    }

    private void addImgExcerpt()
    {

    }

    private void save()
    {

    }

    private void write()
    {

    }

    // ---------- START Private Classes and Action Listeners ---------- //
    private class Header
    {
        public List<String> headerLines;

        public Header()
        {
            this.headerLines = new ArrayList<String>();
        }
    }

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

    private class SaveButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            save();
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

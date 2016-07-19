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
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
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
 * Handle a single blog.
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
    private String operation;
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
        operation = "";

        header = new Header();
        paragraphs = new ArrayList<Paragraph>();

        oldFileName = "";
        fileName = "";
        title = "";
        date = "";
        author = new Author();
        categories = "";
        abbr = new Abbr();
    }

    public void initFromDocxFile(File file)
    {
        operation = Value.OPERATION_GENERATE;
        initBlogFrame();
        this.file = file;
        parseDOCXFile();
        initInput();
        trimSpace();
        replaceHTMLChars();
        displayBlog();
    }

    public void initFromHTMLFile(File file)
    {
        // TODO: implement this
    }

    private void initBlogFrame()
    {
        blogFrame = new JFrame(Value.TITLE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        blogFrame.setSize(screenSize.width, screenSize.height);
        blogFrame.setLocationRelativeTo(null);
        blogFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        blogFrame.setLayout(new GridLayout(1, 2));

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
        titleInput.setPreferredSize(inputDimension);
        authorInput = new JComboBox<String>(AuthorList.nameList.toArray((new String[0])));
        authorInput.setPreferredSize(inputDimensionHalf);
        dateInput = new JTextField();
        dateInput.setPreferredSize(inputDimensionHalf);
        categoriesInput = new JTextField();
        categoriesInput.setPreferredSize(inputDimensionHalf);
        categoryList = new JComboBox<String>(CategoryList.categoryList.toArray((new String[0])));
        categoryList.setPreferredSize(inputDimensionHalf);
        JButton addCategoryButton = new JButton("Add Category");
        addCategoryButton.addActionListener(new addCategoryButtonListener());
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
        addAbbrButton.addActionListener(new addAbbrButtonListener());
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

    private void initAbbrPanel()
    {
        abbrPanel.removeAll();
        abbrPanel.setLayout(inputFlowLayout);

        for (Map.Entry<String, String> abbrRecord : abbr.getList().entrySet()) {
            JTextField abbrRecordShort = new JTextField(abbrRecord.getKey());
            abbrRecordShort.setPreferredSize(abbrRecordShortDimension);
            abbrRecordShort.setEditable(false);

            JTextField abbrRecordFull = new JTextField(abbrRecord.getValue());
            abbrRecordFull.setPreferredSize(abbrRecordFullDimension);
            abbrRecordFull.setEditable(false);

            JButton deleteAbbrButton = new JButton("Delete Abbr");
            deleteAbbrButton.setPreferredSize(buttonDimension);
            deleteAbbrButton.addActionListener(new deleteAbbrButtonListener());
            deleteAbbrButton.setActionCommand(abbrRecord.getKey());

            abbrPanel.add(abbrRecordShort);
            abbrPanel.add(abbrRecordFull);
            abbrPanel.add(deleteAbbrButton);
        }

        blogFrame.revalidate();
        blogFrame.repaint();
    }

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

    private void updateParagraphStyle()
    {
        lastParagraphStyle.style = currentParagraphStyle.style;
    }

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
            newRun.text = currentRun.toString();
            updateRunStyle();
        }
        setLastRunTags(tempRuns.get(tempRuns.size() - 1));

        return tempRuns;
    }

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

    private void updateRunStyle()
    {
        lastRunStyle.isHyperLink = currentRunStyle.isHyperLink;
        lastRunStyle.url = currentRunStyle.url;
        lastRunStyle.script = currentRunStyle.script;
    }

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

    private void initInput()
    {

    }

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

    private class addCategoryButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            addCategory();
        }
    }

    private class addAbbrButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            addAbbr();
        }
    }

    private class deleteAbbrButtonListener implements ActionListener
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
}

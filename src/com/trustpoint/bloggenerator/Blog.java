package com.trustpoint.bloggenerator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes.Name;

import javax.lang.model.type.PrimitiveType;
import javax.naming.InitialContext;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
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
 * Handle a single blog.
 *
 * @author zli
 *
 */
public class Blog extends JFrame
{
    private static final long serialVersionUID = 131029660448982933L;

    private String operation;

    private List<String> lines;
    private int lineCt;
    private int headerEOL;

    private String oldFileName;
    private String fileName;
    private String title;
    private String date;
    private Author author;
    private String categories;
    private Abbr abbr;

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

    // Parse elements
    private XWPFDocument document;
    private XWPFParagraph currentParagraph;
    private XWPFRun currentRun;
    private String currentParagraphText;
    private ParagraphStyle lastParagraphStyle;
    private ParagraphStyle currentParagraphStyle;
    private RunStyle lastRunStyle;
    private RunStyle currentRunStyle;

    public Blog()
    {
        operation = "";

        lines = new ArrayList<String>();
        lineCt = 0;
        headerEOL = 0;

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
        parseDOCXFile(file);
        initInput();
        displayBlog();
    }

    public void initFromHTMLFile(File file)
    {
        operation = Value.OPERATION_EDIT;
        initBlogFrame();
        parseHTMLFile(file);
        getInputFromBlog();
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

    private void parseDOCXFile(File file)
    {
        // Add header
        lines.add(Value.HEADER_START + "\n");
        lines.add(Value.HEADER_LAYOUT + "\n");
        lines.add(Value.HEADER_TITLE + "\n");
        lines.add(Value.HEADER_DATE + "\n");
        lines.add(Value.HEADER_AUTHOR + "\n");
        lines.add(Value.HEADER_CATEGORIES + "\n");
        lines.add(Value.HEADER_EXCERPT + "\n");
        lines.add(Value.HEADER_END + "\n");

        // Parse each paragraph
        try {
            // FileInputStream fis = new FileInputStream("/Users/zli/Downloads/google.docx");
            FileInputStream fis = new FileInputStream(
                    "/Users/zli/Downloads/TrustPoint in the Community.docx");
            document = new XWPFDocument(fis);
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            lastParagraphStyle = new ParagraphStyle();
            lastParagraphStyle.style = Value.PARAGRAPH_STYLE_PLAIN;
            currentParagraphStyle = new ParagraphStyle();
            for (int i = 0; i < paragraphs.size(); i++) {
                currentParagraph = paragraphs.get(i);
                String paragraphText = currentParagraph.getParagraphText();
                if (!StringUtils.isBlank(paragraphText)) {
                    currentParagraphText = "";
                    currentParagraphStyle.style = "";
                    getParagraphTextAndStyle();
                    setParagraphTags();
                    switch (currentParagraphStyle.style) {
                    case Value.PARAGRAPH_STYLE_BULLET:
                    case Value.PARAGRAPH_STYLE_DECIMAL:
                        lines.add(Value.LINE_PREFIX + Value.HTML_LIST_OPEN + currentParagraphText
                                + Value.HTML_LIST_CLOSE + "\n");
                        break;
                    case Value.PARAGRAPH_STYLE_HEADER:
                        lines.add("\n" + Value.HTML_HEADER_OPEN + currentParagraphText
                                + Value.HTML_HEADER_CLOSE + "\n");
                        break;
                    case Value.PARAGRAPH_STYLE_PLAIN:
                        lines.add("\n" + Value.HTML_PARAGRAPH_OPEN + currentParagraphText
                                + Value.HTML_PATAGRAPH_CLOSE + "\n");
                        break;
                    }
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
                lines.add(Value.HTML_BULLET_LIST_CLOSE + "\n");
            } else if (lastParagraphStyle.style.equals(Value.PARAGRAPH_STYLE_DECIMAL)) {
                lines.add(Value.HTML_NUMBER_LIST_CLOSE + "\n");
            }
            if (currentParagraphStyle.style.equals(Value.PARAGRAPH_STYLE_BULLET)) {
                lines.add("\n" + Value.HTML_BULLET_LIST_OPEN + "\n");
            } else if (currentParagraphStyle.style.equals(Value.PARAGRAPH_STYLE_DECIMAL)) {
                lines.add("\n" + Value.HTML_NUMBER_LIST_OPEN + "\n");
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
            lines.add(Value.HTML_BULLET_LIST_CLOSE + "\n");
        } else if (lastParagraphStyle.style.equals(Value.PARAGRAPH_STYLE_DECIMAL)) {
            lines.add(Value.HTML_NUMBER_LIST_CLOSE + "\n");
        }
    }

    private void getParagraphTextAndStyle()
    {
        List<XWPFRun> runs = currentParagraph.getRuns();

        // Set paragraph style
        String numfmt = currentParagraph.getNumFmt();
        if (numfmt != null) {
            currentParagraphStyle.style = numfmt;
        }
        else if (runs.get(0).isBold()) {
            currentParagraphStyle.style = Value.PARAGRAPH_STYLE_HEADER;
        }
        else {
            currentParagraphStyle.style = Value.PARAGRAPH_STYLE_PLAIN;
        }

        // Set paragraph text
        lastRunStyle = new RunStyle();
        lastRunStyle.setDefaultStyle();
        currentRunStyle = new RunStyle();
        for (int i = 0; i < runs.size(); i++) {
            currentRun = runs.get(i);
            getRunStyle();
            setRunTags();
            currentParagraphText += currentRun.toString();
            updateRunStyle();
        }
        setLastRunTags();
    }

    private void getRunStyle()
    {
        if (currentRun instanceof XWPFHyperlinkRun) {
            currentRunStyle.isHyperLink = true;
            currentRunStyle.link = ((XWPFHyperlinkRun) currentRun).getHyperlink(document).getURL();
        }
        else {
            currentRunStyle.isHyperLink = false;
            currentRunStyle.link = "";
        }

        currentRunStyle.script = currentRun.getSubscript();
    }

    private void setRunTags()
    {

    }

    private void updateRunStyle()
    {
        lastRunStyle.isHyperLink = currentRunStyle.isHyperLink;
        lastRunStyle.link = currentRunStyle.link;
        lastRunStyle.script = currentRunStyle.script;
    }

    private void setLastRunTags()
    {
        if (!lastRunStyle.script.equals(VerticalAlign.BASELINE)) {
            if (lastRunStyle.script.equals(VerticalAlign.SUBSCRIPT)) {
                currentParagraphText +=
            }
        }
    }

    private void parseHTMLFile(File file)
    {

    }

    private void initInput()
    {

    }

    private void getInputFromBlog()
    {

    }

    private void displayBlog()
    {
        editor.setText("");
        for (int i = 0; i < lines.size(); i++) {
            // Break lines longer than 100 chars
            int startLineIndex = 0;
            int endLineIndex = lines.get(i).length();
//            while (endLineIndex - startLineIndex > Value.LINE_LENGTH) {
//                int spaceIndex = lines.get(i).lastIndexOf(' ', startLineIndex + Value.LINE_LENGTH);
//                if (spaceIndex == -1) {
//                    spaceIndex = lines.get(i).indexOf(' ', startLineIndex + Value.LINE_LENGTH + 1);
//                }
//                if (spaceIndex == -1) {
//                    break;
//                } else {
//                    editor.append(lines.get(i).substring(startLineIndex, spaceIndex) + "\n");
//                    startLineIndex = spaceIndex + 1;
//                }
//            }
            editor.append(lines.get(i).substring(startLineIndex, endLineIndex));
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

    private class ParagraphStyle
    {
        public String style;
    }

    private class RunStyle
    {
        public boolean isHyperLink;
        public String link;
        public VerticalAlign script;

        public void setDefaultStyle()
        {
            this.isHyperLink = false;
            this.link = "";
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

    public static void main(String[] args)
    {
        AbbrList.init();
        AuthorList.init();
        CategoryList.init();
        LowercaseWordList.init();
        Blog blog = new Blog();
        blog.initFromDocxFile(null);
        // blog.initBlogFrame();
        // blog.initAbbrPanel();
    }

}

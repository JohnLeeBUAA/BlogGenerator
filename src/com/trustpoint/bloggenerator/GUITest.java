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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
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

/**
 * Handle a single blog.
 *
 * @author zli
 *
 */
public class GUITest extends JFrame
{
    private static final long serialVersionUID = 131029660448982933L;

    private Author author;
    private Date date;
    private String title;
    private List<String> categories;
    private List<String> lines;
    private int lineCt;
    private int headerEOL;
    private String fileName;
    private String oldFileName;
    private String operation;
    private Abbr abbr;

    // GUI elements
    private JTextArea editor;
    private JTextArea lineNumber;
    private JPanel abbrPanel;
    private Dimension screenSize;
    private List<JTextField> abbrRecordShortList;
    private List<JTextField> abbrRecordFullList;

    public void initFromDocxFile(File file)
    {
        initBlogFrame();
    }

    public void initFromHTMLFile(File file)
    {
        // TODO: Implement this
        initBlogFrame();
    }

    private void initBlogFrame()
    {
        JFrame blogFrame = new JFrame(Value.TITLE);
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
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
        JPanel inputPanel = new JPanel(new GridLayout(4, 1));

        FlowLayout inputFlowLayout = new FlowLayout(FlowLayout.LEFT);
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
        JTextField fileNameInput = new JTextField();
        Dimension inputDimension = new Dimension(
                (screenSize.width) / 2 - Value.FLOWLAYOUT_GAP * 3 - labelDimension.width,
                fileNameInput.getPreferredSize().height);
        Dimension inputDimensionHalf = new Dimension(
                ((screenSize.width) / 2 - Value.FLOWLAYOUT_GAP * 5 - labelDimension.width * 2) / 2,
                fileNameInput.getPreferredSize().height);
        fileNameInput.setPreferredSize(inputDimension);
        JTextField titleInput = new JTextField();
        titleInput.setPreferredSize(inputDimension);
        JComboBox<String> authorInput = new JComboBox<String>(
                AuthorList.nameList.toArray((new String[0])));
        authorInput.setPreferredSize(inputDimensionHalf);
        JTextField dateInput = new JTextField();
        dateInput.setPreferredSize(inputDimensionHalf);
        JTextField categoriesInput = new JTextField();
        categoriesInput.setPreferredSize(inputDimensionHalf);
        JComboBox<String> categoryList = new JComboBox<String>(
                AuthorList.nameList.toArray((new String[0])));
        categoryList.setPreferredSize(inputDimensionHalf);
        JButton addCategoryButton = new JButton("Add Category");
        addCategoryButton.addActionListener(new addCategoryButtonListener());
        Dimension addCategoryButtonDimension = addCategoryButton.getMinimumSize();
        Dimension categoryListDimension = new Dimension(
                (screenSize.width) / 2 - Value.FLOWLAYOUT_GAP * 5 - labelDimension.width
                        - inputDimensionHalf.width - addCategoryButtonDimension.width,
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

        operationPanel.add(inputPanel, BorderLayout.PAGE_START);
        // -----------END inputPanel----------- //

        // The abbreviation panel on the center right side of the frame
        abbrPanel = new JPanel();
        operationPanel.add(abbrPanel, BorderLayout.CENTER);

        // The button panel on the bottom right side of the frame
        // ----------START bottonPanel---------- //
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
        //buttonPanel.setPreferredSize(new Dimension((screenSize.width) / 2, buttonPanel.getPreferredSize().height));

        FlowLayout buttonFlowLayout = new FlowLayout(FlowLayout.CENTER);

        JPanel buttonPanel1 = new JPanel(buttonFlowLayout);
        //buttonPanel1.setPreferredSize(new Dimension((screenSize.width) / 2, buttonPanel1.getPreferredSize().height));
        JPanel buttonPanel2 = new JPanel(buttonFlowLayout);
        //buttonPanel2.setPreferredSize(new Dimension((screenSize.width) / 2, buttonPanel2.getPreferredSize().height));

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

        FlowLayout inputFlowLayout = new FlowLayout(FlowLayout.LEFT);
        inputFlowLayout.setHgap(Value.FLOWLAYOUT_GAP);

        abbrPanel.setLayout(inputFlowLayout);
        abbrPanel.setPreferredSize(
                new Dimension((screenSize.width) / 2, abbrPanel.getPreferredSize().height));

        // Labels, inputs and buttons
        JLabel abbrLabel = new JLabel("Abbreviation", JLabel.LEFT);
        Dimension dimensionSmall = new Dimension(
                ((screenSize.width) / 2 - Value.FLOWLAYOUT_GAP * 4) / 2,
                abbrLabel.getPreferredSize().height);
        Dimension dimensionLarge = new Dimension(
                ((screenSize.width) / 2 - Value.FLOWLAYOUT_GAP * 4) / 4,
                abbrLabel.getPreferredSize().height);
        abbrLabel.setPreferredSize(dimensionSmall);
        JLabel shortLabel = new JLabel("Short Form", JLabel.LEFT);
        shortLabel.setPreferredSize(dimensionSmall);
        JLabel fullLabel = new JLabel("Full Form", JLabel.LEFT);
        fullLabel.setPreferredSize(dimensionSmall);
        JTextField addAbbrInput = new JTextField();
        addAbbrInput.setPreferredSize(dimensionSmall);
        JButton addAbbrButton = new JButton("Add Abbr");
        addAbbrButton.setPreferredSize(dimensionSmall);

        abbrPanel.add(abbrLabel);
        abbrPanel.add(addAbbrInput);
        abbrPanel.add(addAbbrButton);
        abbrPanel.add(shortLabel);
        abbrPanel.add(fullLabel);

        for (Map.Entry<String, String> abbrRecord : abbr.getList().entrySet()) {
            JPanel abbrRecordPanel = new JPanel(inputFlowLayout);

            JTextField abbrRecordShort = new JTextField(abbrRecord.getKey());
            abbrRecordShort.setPreferredSize(dimensionSmall);
            abbrRecordShortList.add(abbrRecordShort);

            JTextField abbrRecordFull = new JTextField(abbrRecord.getValue());
            abbrRecordFull.setPreferredSize(dimensionLarge);
            abbrRecordFullList.add(abbrRecordFull);

            JButton deleteAbbrButton = new JButton("Delete Abbr");
            deleteAbbrButton.setPreferredSize(dimensionSmall);

            abbrRecordPanel.add(abbrRecordShort);
            abbrRecordPanel.add(abbrRecordFull);
            abbrRecordPanel.add(deleteAbbrButton);

            abbrPanel.add(abbrRecordPanel);
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
        AuthorList.init();
        GUITest blog = new GUITest();
        blog.initBlogFrame();
        blog.abbr = new Abbr();
        blog.initAbbrPanel();
    }

}

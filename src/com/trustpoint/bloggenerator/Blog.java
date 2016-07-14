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
 * Handle on single blog.
 *
 * @author zli
 *
 */
public class Blog extends JFrame
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

    // GUI elements
    private JFrame blogFrame;
    private JTextArea editor;
    private JTextArea lineNumber;
    private Font font;

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
        font = new Font("monospaced", Font.PLAIN, lineNumber.getFont().getSize());

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
        JComboBox<String> authorInput = new JComboBox<String>(AuthorList.nameList.toArray((new String[0])));
        authorInput.setPreferredSize(inputDimensionHalf);
        JTextField dateInput = new JTextField();
        dateInput.setPreferredSize(inputDimensionHalf);
        JTextField categoriesInput = new JTextField();
        categoriesInput.setPreferredSize(inputDimensionHalf);
        JTextField categoriesList = new JTextField();
        categoriesList.setPreferredSize(inputDimensionHalf);
        JButton addCategoryButton = new JButton("Add Cat.");
        //addCategoryButton.setPreferredSize(labelDimension);

        inputPanel1.add(fileNameLabel);
        inputPanel1.add(fileNameInput);
        inputPanel.add(inputPanel1);
        inputPanel2.add(titleLabel);
        inputPanel2.add(titleInput);
        inputPanel.add(inputPanel2);
        inputPanel3.add(authorLabel);
        inputPanel3.add(authorInput);
        inputPanel3.add(dateLabel);
        inputPanel3.add(dateInput);
        inputPanel.add(inputPanel3);
        inputPanel4.add(categoriesLabel);
        inputPanel4.add(categoriesInput);
        //inputPanel4.add(categoriesList);
        inputPanel4.add(addCategoryButton);
        inputPanel.add(inputPanel4);

        operationPanel.add(inputPanel, BorderLayout.PAGE_START);
        // -----------END inputPanel----------- //

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

    private void updateLineNumber()
    {
        int rows = editor.getLineCount();
        lineNumber.setText("");
        for (int i = 1; i <= rows; i++)
        {
            lineNumber.append(Integer.toString(i));
            if (i != rows)
            {
                lineNumber.append("\n");
            }
        }
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
        //AuthorList.init();
        //Blog blog = new Blog();
        //blog.initBlogFrame();
        try {
            URL url = new URL("https://www.google.ca/search?q=CEO");
            HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
            httpcon.addRequestProperty("User-Agent", "Chrome/51.0.2704");
            //httpcon.addRequestProperty("User-Agent", "Mozilla/4.76");
            //Thread.sleep(10000);
            BufferedReader br =
                    new BufferedReader(
                        new InputStreamReader(httpcon.getInputStream()));

                   String input;

                   while ((input = br.readLine()) != null){
                      System.out.println(input);
                   }
                   br.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}

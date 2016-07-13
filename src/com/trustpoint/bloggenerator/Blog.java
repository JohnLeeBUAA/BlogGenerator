package com.trustpoint.bloggenerator;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Date;
import java.util.List;

import javax.lang.model.type.PrimitiveType;
import javax.naming.InitialContext;
import javax.swing.BoxLayout;
import javax.swing.JButton;
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
        blogFrame.setLayout(new BorderLayout());

        JPanel editorPanel = new JPanel(new FlowLayout());

        lineNumber = new JTextArea(Value.EDITOR_INITIAL_LINES, 2);
        lineNumber.setEditable(false);
        editorPanel.add(lineNumber);

        editor = new JTextArea(Value.EDITOR_INITIAL_LINES, 60);
        editor.getDocument().addDocumentListener(new EditorListener());
        editorPanel.add(editor);

        updateLineNumber();

        JScrollPane editorScroller = new JScrollPane(editorPanel);
        editorScroller.setPreferredSize(new Dimension((screenSize.width / 2), screenSize.height));
        blogFrame.add(editorScroller, BorderLayout.LINE_START);


        JPanel infoPanel = new JPanel(new GridLayout(4, 1));

        FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT);
        flowLayout.setHgap(20);
        JPanel fileNamePanel = new JPanel(flowLayout);
        JLabel fileNameLabel = new JLabel("File Name:", JLabel.RIGHT);
        Dimension dimension = fileNameLabel.getPreferredSize();
        fileNameLabel.setPreferredSize(new Dimension(dimension.width * 2, dimension.height));
        JTextField fileNameText = new JTextField();
        //fileNameText.setColumns(50);
        fileNamePanel.add(fileNameLabel);
        fileNamePanel.add(fileNameText);

        infoPanel.add(fileNamePanel);

        JPanel fileNamePanel2 = new JPanel(flowLayout);
        JLabel fileNameLabel2 = new JLabel("A looooonger j label:", JLabel.RIGHT);
        Dimension dimension2 = fileNameLabel2.getPreferredSize();
        fileNameLabel2.setPreferredSize(new Dimension(dimension2.width * 2, dimension2.height));
        JTextField fileNameText2 = new JTextField();
        //fileNameText.setColumns(50);
        fileNamePanel2.add(fileNameLabel2);
        fileNamePanel2.add(fileNameText2);

        infoPanel.add(fileNamePanel2);

        blogFrame.add(infoPanel, BorderLayout.CENTER);


//        FlowLayout buttonFlowLayout = new FlowLayout(FlowLayout.CENTER);
//        buttonFlowLayout.setVgap(20);
//        JPanel buttonPanel = new JPanel(buttonFlowLayout);
//        JButton saveButton = new JButton("Save Changes");
//        saveButton.addActionListener(new saveButtonListener());
//        buttonPanel.add(saveButton);
//        JButton writeButton = new JButton("Write to File");
//        writeButton.addActionListener(new writeButtonListener());
//        buttonPanel.add(writeButton);
//        blogFrame.add(buttonPanel, BorderLayout.PAGE_END);

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

    private void save()
    {

    }

    private void write()
    {

    }

    private class saveButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            save();
        }
    }

    private class writeButtonListener implements ActionListener
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
        Blog blog = new Blog();
        blog.initBlogFrame();
    }

}

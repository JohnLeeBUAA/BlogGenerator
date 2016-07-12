package test;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GUITest extends JFrame
{
    private static final long serialVersionUID = 8739983700750888337L;

    private JFrame mainFrame;
    private JFrame blogFrame;

    public GUITest()
    {
        initMainFrame("first frame");
    }

    private void initMainFrame(String title)
    {
        mainFrame = new JFrame(title);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame.setSize(screenSize.width, screenSize.height);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void displayUploadGUI()
    {
        GridLayout gridLayout = new GridLayout(3, 1);
        mainFrame.setLayout(gridLayout);

        JLabel lable = new JLabel(
                "Choose the operation to perform. Then choose the file to upload.");
        lable.setHorizontalAlignment(JLabel.CENTER);
        lable.setVerticalAlignment(JLabel.BOTTOM);
        lable.setSize(300, 300);
        mainFrame.add(lable);

        JPanel panel = new JPanel(new FlowLayout());

        JButton uploadDocx = new JButton("Generate Blog From Docx File");
        uploadDocx.addActionListener(new DocxClickListener());
        panel.add(uploadDocx);

        JButton uploadHtml = new JButton("Edit Existing HTML Blog");
        uploadHtml.addActionListener(new HtmlClickListener());
        panel.add(uploadHtml);

        mainFrame.add(panel);
        mainFrame.setVisible(true);
    }

    public void displayBlogGUI() {
        mainFrame.dispose();
        blogFrame = new JFrame("rest");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        blogFrame.setSize(screenSize.width, screenSize.height);
        blogFrame.setLayout(new FlowLayout());
        JLabel lable = new JLabel(
                "Choose the operation to perform. Then choose the file to upload.");
        blogFrame.add(lable);
        blogFrame.add(lable);
        blogFrame.add(lable);
        blogFrame.setVisible(true);
    }

    private class DocxClickListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Docx file only", "docx");
            fileChooser.setFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION)
            {
                File selectedFile = fileChooser.getSelectedFile();
                mainFrame.setTitle(selectedFile.getPath());
                mainFrame.getContentPane().removeAll();
            }
        }
    }

    private class HtmlClickListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("HTML file only", "html");
            fileChooser.setFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION)
            {
                File selectedFile = fileChooser.getSelectedFile();
                mainFrame.setTitle(selectedFile.getPath());
            }
        }
    }

    public static void main(String[] args)
    {
        GUITest gui = new GUITest();
        gui.displayUploadGUI();
    }
}
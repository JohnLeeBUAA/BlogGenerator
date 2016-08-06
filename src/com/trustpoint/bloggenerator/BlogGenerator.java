package com.trustpoint.bloggenerator;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * The entrance frame of the application. Do some setup work and allow user to choose operation and
 * upload file.
 *
 * @author zli
 *
 */
public class BlogGenerator extends JFrame {
  private static final long serialVersionUID = 4191224068294036050L;

  private JFrame uploadFrame;

  /**
   * Check if it is in the correct directory.
   */
  public void setUp() {
    // Validate path and get base directory
    String currentDir = System.getProperty("user.dir");
    if (!(currentDir.substring(currentDir.length() - Value.RELATIVE_DIR.length()))
        .equals(Value.RELATIVE_DIR)) {
      Error error = new Error();
      error
          .initErrorFrame("Please put the BlogGenerator folder directly under the website folder.");
      return;
    }
    Value.BASE_DIR = currentDir.substring(0, currentDir.length() - Value.SELF_DIR.length());

    // Build GUI
    initUploadFrame();
  }

  /**
   * Construct GUI for uploading DOCX file.
   */
  private void initUploadFrame() {
    uploadFrame = new JFrame(Value.TITLE);
    uploadFrame.setSize(600, 200);
    uploadFrame.setLocationRelativeTo(null);
    uploadFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    GridLayout gridLayout = new GridLayout(3, 1);
    uploadFrame.setLayout(gridLayout);

    JLabel lable = new JLabel("Choose the operation to perform. Then choose the file to upload.");
    lable.setHorizontalAlignment(JLabel.CENTER);
    lable.setVerticalAlignment(JLabel.BOTTOM);
    lable.setSize(300, 300);
    uploadFrame.add(lable);

    JPanel panel = new JPanel(new FlowLayout());

    JButton uploadDocx = new JButton("Generate Blog From Docx File");
    uploadDocx.addActionListener(new DocxClickListener());
    panel.add(uploadDocx);

    uploadFrame.add(panel);

    uploadFrame.setVisible(true);
  }

  // ---------- START Action Listeners ---------- //
  private class DocxClickListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      JFileChooser fileChooser = new JFileChooser();
      FileNameExtensionFilter filter = new FileNameExtensionFilter("Docx file only", "docx");
      fileChooser.setFileFilter(filter);
      fileChooser.setAcceptAllFileFilterUsed(false);
      int returnValue = fileChooser.showOpenDialog(null);
      if (returnValue == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        Blog blog = new Blog();
        blog.initFromDocxFile(selectedFile);
      }
    }
  }
  // ----------- END Action Listeners ----------- //

  /**
   * Application entrance.
   *
   * @param args
   */
  public static void main(String[] args) {
    BlogGenerator blogGenerator = new BlogGenerator();
    blogGenerator.setUp();
  }
}

package com.trustpoint.bloggenerator;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;

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

    private JFrame blogFrame;

    public void initFromDocxFile(File file)
    {
        initBlogFrame();
    }

    public void initFromHTMLFile(File file)
    {
        initBlogFrame();
    }

    public void initBlogFrame()
    {
        blogFrame = new JFrame(Value.TITLE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        blogFrame.setSize(screenSize.width, screenSize.height);
        blogFrame.setLocationRelativeTo(null);
        blogFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        constructBlogFrame();
    }

    public void constructBlogFrame()
    {
        blogFrame.setVisible(true);
    }

}

package test;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFHyperlink;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class FileExtractor {
    public static void main(String[] args) {
        /*
         * try { FileInputStream fis = new FileInputStream(
         * "/Users/zli/Documents/java programs/readword/test.docx");
         * XWPFDocument doc = new XWPFDocument(fis); List<XWPFParagraph>
         * paragraphs = doc.getParagraphs(); System.out.println(
         * "Total number of paragraph: "+paragraphs.size()); int index = 0; for
         * (XWPFParagraph para : paragraphs) { System.out.println(
         * "##########Paragraph Number " + (index++) + "##########");
         * System.out.println(para.getText()); } fis.close(); } catch (Exception
         * e) { e.printStackTrace(); }
         */

        StringBuffer text = null;
        try {
            FileInputStream fis = new FileInputStream("/Users/zli/Downloads/google.docx");
            XWPFDocument document = new XWPFDocument(fis);
            text = new StringBuffer();

            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String ptext = paragraph.getParagraphText();
                System.out.println(ptext);
                if (ptext.equals(""))
                {
                    System.out.println("Empty");
                }
            }
//                String numfmt = paragraph.getNumFmt();
//                if (numfmt != null)
//                {
//                    System.out.println(numfmt);
//                }
//
//                int pos = 0;
//                for (XWPFRun run : paragraph.getRuns()) {
//                    VerticalAlign subscript = run.getSubscript();
//                    String smalltext = run.toString();
//                    System.out.println(smalltext);
//                    String lastchar = smalltext.substring(smalltext.length() - 1, smalltext.length());
//                    if (lastchar.equals(" "))
//                    {
//                        lastchar = "space";
//                    }
//
//                    System.out.println("lastchar: " + lastchar + "\n");

//                    switch (subscript) {
//                        case BASELINE:
//                            System.out.println("smalltext, plain = " + smalltext);
//                            break;
//                        case SUBSCRIPT:
//                            System.out.println("smalltext, subscript = " + smalltext);
//                            break;
//                        case SUPERSCRIPT:
//                            System.out.println("smalltext, superscript = " + smalltext);
//                            break;
//                    }
//                    System.out.println("Current run IsBold : " + run.isBold());
//                    System.out.println("Current run IsItalic : " + run.isItalic());
//                    for (char c : run.text().toCharArray()) {
//
//                        System.out.print(c);
//                        pos++;
//                    }
//                    System.out.println();
//                }
//            }

            /*
            // First up, all our paragraph based text
            Iterator<XWPFParagraph> i = document.getParagraphsIterator();
            while (i.hasNext()) {
                XWPFParagraph paragraph = i.next();
                // Do the paragraph text
                for (XWPFRun run : paragraph.getRuns()) {
                    if (run instanceof XWPFHyperlinkRun) {
                        text.append("HyperLink: ");
                        text.append(run.toString());
                        // bean.setName(run.toString());
                        XWPFHyperlink link = ((XWPFHyperlinkRun) run).getHyperlink(document);
                        if (link != null) {
                            text.append(" <" + link.getURL() + ">");
                        }
                        text.append("\n");
                    }
                }
            }
            */
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(text);

    }
}

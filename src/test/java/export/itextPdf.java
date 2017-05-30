/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

/**
 *
 * @author Quincy
 */
public class itextPdf {

    @Test
    public void start_test() throws DocumentException, FileNotFoundException {

        try {
            // step 1
            Document document = new Document();
            // step 2
            PdfWriter.getInstance(document, new FileOutputStream("test-itext.pdf"));
            // step 3
            document.open();
            
            String FONT = "fonts/FreeSans.ttf";
            
            BaseFont bf1 = BaseFont.createFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED);
            Font french = new Font(bf1, 12);
            BaseFont bf2 = BaseFont.createFont(FONT, BaseFont.CP1250, BaseFont.EMBEDDED);
            Font czech = new Font(bf2, 12);
            BaseFont bf3 = BaseFont.createFont(FONT, "Cp1251", BaseFont.EMBEDDED);
            Font russian = new Font(bf3, 12);
            document.add(new Paragraph("Vous \u00eates d'o\u00f9?", french));
            document.add(new Paragraph("\u00c0 tout \u00e0 l'heure. \u00c0 bient\u00f4t.", french));
            document.add(new Paragraph("Je me pr\u00e9sente.", french));
            document.add(new Paragraph("C'est un \u00e9tudiant.", french));
            document.add(new Paragraph("\u00c7a va?", french));
            document.add(new Paragraph("Il est ing\u00e9nieur. Elle est m\u00e9decin.", french));
            document.add(new Paragraph("C'est une fen\u00eatre.", french));
            document.add(new Paragraph("R\u00e9p\u00e9tez, s'il vous pla\u00eet.", french));
            document.add(new Paragraph("Odkud jste?", czech));
            document.add(new Paragraph("Uvid\u00edme se za chvilku. M\u011bj se.", czech));
            document.add(new Paragraph("Dovolte, abych se p\u0159edstavil.", czech));
            document.add(new Paragraph("To je studentka.", czech));
            document.add(new Paragraph("V\u0161echno v po\u0159\u00e1dku?", czech));
            document.add(new Paragraph("On je in\u017een\u00fdr. Ona je l\u00e9ka\u0159.", czech));
            document.add(new Paragraph("Toto je okno.", czech));
            document.add(new Paragraph("Zopakujte to pros\u00edm.", czech));
            document.add(new Paragraph("\u041e\u0442\u043a\u0443\u0434\u0430 \u0442\u044b?", russian));
            document.add(new Paragraph("\u0423\u0432\u0438\u0434\u0438\u043c\u0441\u044f \u0432 \u043d\u0435\u043c\u043d\u043e\u0433\u043e. \u0423\u0432\u0438\u0434\u0438\u043c\u0441\u044f.", russian));
            document.add(new Paragraph("\u041f\u043e\u0437\u0432\u043e\u043b\u044c\u0442\u0435 \u043c\u043d\u0435 \u043f\u0440\u0435\u0434\u0441\u0442\u0430\u0432\u0438\u0442\u044c\u0441\u044f.", russian));
            document.add(new Paragraph("\u042d\u0442\u043e \u0441\u0442\u0443\u0434\u0435\u043d\u0442.", russian));
            document.add(new Paragraph("\u0425\u043e\u0440\u043e\u0448\u043e?", russian));
            document.add(new Paragraph("\u041e\u043d \u0438\u043d\u0436\u0435\u043d\u0435\u0440. \u041e\u043d\u0430 \u0434\u043e\u043a\u0442\u043e\u0440.", russian));
            document.add(new Paragraph("\u042d\u0442\u043e \u043e\u043a\u043d\u043e.", russian));
            document.add(new Paragraph("\u041f\u043e\u0432\u0442\u043e\u0440\u0438\u0442\u0435, \u043f\u043e\u0436\u0430\u043b\u0443\u0439\u0441\u0442\u0430.", russian));
            
            Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 30,Font.BOLD);
            document.add(new Paragraph("Title of the document", catFont));
            // step 4
            document.add(new Paragraph("Hello World!\n test \n                  test"));
            
            
            Chunk chunk = new Chunk("Go to Contact information");
            chunk.setLocalGoto("contact");
            document.add(new Paragraph(chunk));
            document.newPage();
            
            document.add(new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut consectetur, nisi non commodo interdum, nunc massa sodales velit, vel elementum ipsum odio id ligula. Aliquam sed sapien eget nibh varius varius id ac quam. Aliquam id arcu enim. Curabitur sollicitudin placerat dui, nec venenatis ipsum consectetur ut. Fusce sem nibh, semper et nibh sed, varius mattis ipsum. Duis tristique mauris velit. Nam eleifend sapien tempus, sollicitudin risus sit amet, dignissim nisl. Aliquam tincidunt rutrum auctor. Integer sollicitudin lectus faucibus ipsum facilisis imperdiet. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Aliquam erat volutpat. Etiam eu commodo massa, ac venenatis est. Sed venenatis mattis vehicula. Duis eleifend lacus nec purus convallis, non mollis justo commodo. Aenean et arcu vitae metus pharetra venenatis ac non tellus. Vestibulum ultrices turpis urna, vitae condimentum sem pharetra in.\n"
                    + "\n"
                    + "Maecenas aliquet auctor ipsum eget congue. Sed at ullamcorper ante. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Suspendisse scelerisque at erat at condimentum. Vivamus sed aliquet risus, non tempor odio. Etiam pulvinar, mauris ut placerat consequat, ipsum massa imperdiet turpis, eget consequat justo metus sit amet ligula. Donec vel lacinia erat. Donec eget enim sed justo euismod vehicula ut in metus. Suspendisse augue dolor, tempor eu mollis vitae, pellentesque sit amet nulla. Morbi ac sapien arcu. Vivamus in elit dictum orci dapibus blandit eget vitae est. Morbi sed ultricies neque. Ut at fringilla urna. Quisque dictum bibendum nisi, non pellentesque velit cursus vel.\n"
                    + "\n"
                    + "Etiam convallis tincidunt arcu sit amet pellentesque. Suspendisse vitae gravida tellus. Fusce consectetur turpis condimentum, molestie libero pellentesque, aliquet dolor. Duis ullamcorper purus sem, id ornare lacus interdum non. Aenean quis arcu elementum, ultrices tellus sed, dapibus nisl. Aliquam ut iaculis mi, vitae porttitor tellus. Vestibulum lacinia velit sed venenatis volutpat. Nullam magna ipsum, feugiat vestibulum nunc tincidunt, egestas ultrices libero. Quisque fringilla risus ut lectus posuere vulputate. Fusce diam mi, efficitur vitae nulla eget, facilisis luctus nisi. Sed scelerisque hendrerit porttitor. Aenean mollis, urna in bibendum bibendum, lectus orci dictum sapien, ut pretium dui lacus vel turpis. Nunc hendrerit est sit amet turpis venenatis suscipit. Phasellus fermentum aliquam tellus nec sodales.\n"
                    + "\n"
                    + "Etiam eget scelerisque erat, nec commodo arcu. Nullam tempus vehicula auctor. Integer feugiat ornare viverra. In nulla arcu, faucibus ut risus malesuada, elementum egestas eros. Maecenas varius augue faucibus, vulputate turpis at, vestibulum nisi. Etiam placerat vehicula erat, ac viverra nulla interdum id. Sed nibh sem, bibendum ac sodales vitae, iaculis vel magna. Phasellus fringilla quis lectus ac egestas. Praesent iaculis, orci non ultrices mattis, urna ipsum suscipit tortor, nec viverra mi tortor id libero. Quisque vehicula malesuada elit, vitae interdum nunc. In laoreet viverra scelerisque. Nam venenatis blandit tortor, vel aliquam mi rutrum eget. In ut nibh ut nibh lobortis ullamcorper. Morbi neque elit, dictum vel justo nec, pulvinar fermentum purus. Sed consequat dictum hendrerit. Quisque pulvinar mi volutpat est aliquet facilisis.\n"
                    + "\n"
                    + "Phasellus ultrices euismod ligula, ac sagittis diam semper eu. Nulla aliquam justo vel leo congue condimentum. Fusce molestie tellus felis, vel fringilla mi molestie ac. Praesent venenatis sapien eget lectus gravida, vitae condimentum ligula sagittis. Mauris varius placerat sagittis. Phasellus eget tortor quis felis pellentesque malesuada pharetra a lectus. Sed rutrum mi id aliquam lacinia. Donec posuere auctor vulputate. Duis a ante id nisl cursus accumsan. "));
            
            
            
            Chunk chunk1 = new Chunk("Contact information");
            chunk1.setLocalDestination("contact");
            Chapter chapter = new Chapter(new Paragraph(chunk1), 1);
            chapter.setNumberDepth(0);
            document.add(chapter);
            
            PdfPTable table = new PdfPTable(2);
            table.addCell(new Paragraph("test 1", catFont));
            table.addCell("2");
            document.add(table);
            
            
            // step 5
            document.close();
        } catch (IOException ex) {
            Logger.getLogger(itextPdf.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}

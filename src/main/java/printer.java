/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alfredospos;

/**
 *
 * @author User1
 */

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PageRanges;
import java.util.ArrayList;
 
 
public class printer implements Printable {
 
    private PrintService[] printService;
    private String text;
    ArrayList<String> orders;
 
    public printer() {
        this.printService = PrinterJob.lookupPrintServices();
    }
 
//    public static void main(String[] args) {
//        printer lt = new printer();
//        lt.printString("SAIRA LOVES BABY BROTHER");
//         
//    }
 
    public void printString(ArrayList<String> input, int totalItem) {
 
       // this.text = input;
         orders = input;
      //  System.out.println("++++++++++++++++++++" + this.text);
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        aset.add(MediaSizeName.INVOICE);
        aset.add(new PageRanges(1, totalItem));
        aset.add(new Copies(1));
         
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(this);
 
//        try {
//            printJob.setPrintService(printService[3]);
//            //index of installed printers on you system
//            //not sure if default-printer is always '0'
//            printJob.print(aset);
//        } catch (PrinterException err) {
//            System.err.println(err);
//        }
    }
 
    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        Graphics2D g2 = (Graphics2D) g;
        g2.translate(pf.getImageableX(), pf.getImageableY());
      //  g.drawString(this.text, 20, 20); //String.valueOf()
      for(int i=0;i<this.orders.size();i++){
        g.drawString(this.orders.get(i), 10, 10);
        g.drawString("",20, 20);
      }
        return PAGE_EXISTS;
//    if (pageIndex > 0){
//   return Printable.NO_SUCH_PAGE;
//   }
//
//   Graphics2D g2 = (Graphics2D) g;
//   g2.translate(pf.getImageableX(), pf.getImageableY());
//   componenet_name.paint(g2);
//   return Printable.PAGE_EXISTS;
  
    }
}

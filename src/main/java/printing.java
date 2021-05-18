/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package alfredospos;

/**
 *
 * @author User1
 */
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.awt.FontMetrics;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PageRanges;

/**
 *
 * @author mic
 */

public class printing implements Printable {

    /**
     * Creates new form bill_form
     */
      ArrayList<String> orders;
      float subT, tot, tax;
 
   
  
    public PageFormat getPageFormat(PrinterJob pj)
{
    
    PageFormat pf = pj.defaultPage();
    Paper paper = pf.getPaper();    

    double middleHeight =350.0;  
    double headerHeight = 2.0;                  
    double footerHeight = 2.0;                  
    double width = convert_CM_To_PPI(8);      //printer know only point per inch.default value is 72ppi
    double height = convert_CM_To_PPI(headerHeight+middleHeight+footerHeight); 
    paper.setSize(width, height);
    paper.setImageableArea(0,10,width,height - convert_CM_To_PPI(1));   //define boarder size    after that print area width is about 180 points
            
    pf.setOrientation(PageFormat.PORTRAIT);           //select orientation portrait or landscape but for this time portrait
    pf.setPaper(paper);    

    return pf;
}
    
    protected static double convert_CM_To_PPI(double cm) {            
	        return toPPI(cm * 0.393600787);            
}
 
protected static double toPPI(double inch) {            
	        return inch * 72d;            
}

   
     public void printString(ArrayList<String> input, float subtotal, float taxT, float total) {
 
       // this.text = input;
       //  orders = input;
      //  System.out.println("++++++++++++++++++++" + this.text);
      subT = subtotal;
      tot = total;
      tax = taxT;
      orders = new ArrayList<String>();
       for(int i = 0; i< input.size();i++){
            orders.add(input.get(i));
            
        }
      
      
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        aset.add(MediaSizeName.INVOICE);
       // aset.add(new PageRanges(1, totalItem));
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
    
  public int print(Graphics graphics, PageFormat pageFormat,int pageIndex) 
  throws PrinterException 
  {    
      
                
        
      int result = NO_SUCH_PAGE;    
        if (pageIndex == 0) {                    
        
            Graphics2D g2d = (Graphics2D) graphics;                    

            double width = pageFormat.getImageableWidth();                    
           
            g2d.translate((int) pageFormat.getImageableX(),(int) pageFormat.getImageableY()); 

            ////////// code by alqama//////////////

            FontMetrics metrics=g2d.getFontMetrics(new Font("Arial",Font.BOLD,7));
        //    int idLength=metrics.stringWidth("000000");
            //int idLength=metrics.stringWidth("00");
            int idLength=metrics.stringWidth("000");
            int amtLength=metrics.stringWidth("000000");
            int qtyLength=metrics.stringWidth("00000");
            int priceLength=metrics.stringWidth("000000");
            int prodLength=(int)width - idLength - amtLength - qtyLength - priceLength-17;

        //    int idPosition=0;
        //    int productPosition=idPosition + idLength + 2;
        //    int pricePosition=productPosition + prodLength +10;
        //    int qtyPosition=pricePosition + priceLength + 2;
        //    int amtPosition=qtyPosition + qtyLength + 2;
            
            int productPosition = 0;
            int discountPosition= prodLength+5;
            int pricePosition = discountPosition +idLength+10;
            int qtyPosition=pricePosition + priceLength + 4;
            int amtPosition=qtyPosition + qtyLength;
            
            
              
        try{
            /*Draw Header*/
            int y=20;
            int yShift = 10;
            int headerRectHeight=15;
            int headerRectHeighta=40;
            
        
                
             g2d.setFont(new Font("Monospaced",Font.PLAIN,9));
            g2d.drawString("-------------------------------------",12,y);y+=yShift;
            g2d.drawString("      Restaurant Bill Receipt        ",12,y);y+=yShift;
            g2d.drawString("-------------------------------------",12,y);y+=headerRectHeight;
      
            g2d.drawString("-------------------------------------",10,y);y+=yShift;
            g2d.drawString(" Food Name                 T.Price   ",10,y);y+=yShift;
            g2d.drawString("-------------------------------------",10,y);y+=headerRectHeight;
//            g2d.drawString(" "+pn1a+"                  "+pp1a+"  ",10,y);y+=yShift;
//            g2d.drawString(" "+pn2a+"                  "+pp2a+"  ",10,y);y+=yShift;
//            g2d.drawString(" "+pn3a+"                  "+pp3a+"  ",10,y);y+=yShift;
//            g2d.drawString(" "+pn4a+"                  "+pp4a+"  ",10,y);y+=yShift;

             for(int i = 0; i< orders.size();i++){
            
            g2d.drawString(orders.get(i),10,y);y+=yShift;
//            System.out.println(orders.get(i));
//            ArrayList<String> items = new ArrayList<String>();
//Map<String, Integer> items = new HashMap<String, Integer>();
//if (items.containsKey(orders.get(i))){
//    items.put(orders.get(i),items.get(orders.get(i))+1);}
//else{
//    items.put(orders.get(i), 1);
//}
//            System.out.println(items);
//            Map<String, Integer> items = new HashMap<String, Integer>();
        }
    
            g2d.drawString("-------------------------------------",10,y);y+=yShift;
            g2d.drawString(" Sub-Total: "+ "$" +String.valueOf(subT)+"               ",10,y);y+=yShift;
             g2d.drawString(" Tax:      "+"$"+String.valueOf(tax)+"               ",10,y);y+=yShift;
              g2d.drawString(" Total:    "+"$"+String.valueOf(tot)+"               ",10,y);y+=yShift;
            g2d.drawString("-------------------------------------",10,y);y+=yShift;
            g2d.drawString("          Free Home Delivery         ",10,y);y+=yShift;
            g2d.drawString("             03111111111             ",10,y);y+=yShift;
            g2d.drawString("*************************************",10,y);y+=yShift;
            g2d.drawString("    THANKS TO VISIT OUR RESTUARANT   ",10,y);y+=yShift;
            g2d.drawString("*************************************",10,y);y+=yShift;
                   
           
             
           
            
//            g2d.setFont(new Font("Monospaced",Font.BOLD,10));
//            g2d.drawString("Customer Shopping Invoice", 30,y);y+=yShift; 
          

    }
    catch(Exception r){
    r.printStackTrace();
    }

              result = PAGE_EXISTS;    
          }    
          return result;    
      }
  
}

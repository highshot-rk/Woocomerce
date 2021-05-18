
import com.icoderman.woocommerce.ApiVersionType;
import com.icoderman.woocommerce.EndpointBaseType;
import com.icoderman.woocommerce.WooCommerce;
import com.icoderman.woocommerce.WooCommerceAPI;
import com.icoderman.woocommerce.oauth.OAuthConfig;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author businessmac
 */
public class NewJFrame3 extends javax.swing.JFrame {
    
    //connection string for woocommerce API
   OAuthConfig config = new OAuthConfig("https://digitsoftex.com", "ck_a581c04673f15c2b23506c4dc5bde28b7e9abfab", "cs_85220da57bfce74596be66d3bc918b43b2744388");
   WooCommerce wooCommerce = new WooCommerceAPI(config,  ApiVersionType.V3);
   ImageIcon icon = new ImageIcon("//Users/businessmac/Desktop/alert-icon-red.png");        
                
    double total = 0;
    double temp_total;
    double tax = 0;
    double total_sum = 0;
    double price = 0;
    double taxPercent = 0.06;
    
    ////////////////////////////////////////////////// Vadim code//////////////////////////////
    
    // if you press back button, ypu have to go previous category.
    Stack<Integer> hierachy;
    
    // varialbel to store all category infos.
    List m_All_categories = null;
    
    static NewJFrame3 mainFrame;
   /////////////////////////////////////////////////////////////////////////////////////////////
    
                
    ArrayList<Double> prices = new ArrayList<Double>();  
    
    
     DefaultListModel model;
     
     
      //function to round
    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    //function to format numbers
    DecimalFormat df = new DecimalFormat("0.00");
    
////////////////////////////////////////////////// Vadim code//////////////////////////////
    
    // get sub categories from main category
    public static List<Map> getSubCategoriesFromMainCategory(List all_categories, Map main_category){
        List<Map> results = new ArrayList<>();
        for(int i = 0 ; i < all_categories.size(); i++){
            Map category = (Map) all_categories.get(i);
            if((int)(category.get("parent")) == (int)(main_category.get("id")))
                results.add(category);
        }
        return results;
    }
    
    public void checkBackButton(){
        jPanel1.revalidate();
        jPanel1.repaint();
        if(hierachy.size() < 1)
            btn_back.setEnabled(false);
        else
            btn_back.setEnabled(true);
    }
    // show categories
    // m_Main_categories: categories to show
    // m_All_categories: all categories
    // right_panel: boolean to show categories on right panel or not.
    public void showCategories(List<Map> m_Main_categories, List m_All_categories, Map<String, String> param_products, boolean right_panel ){
        // get main category images and show main categories in left panel..
        if(right_panel){
            jPanel1.removeAll();
            jPanel1.setLayout(new GridLayout((int)(m_Main_categories.size() / 5) + 1, 5,20,20));
        }else{
            jPanel4.removeAll();
            jPanel4.setLayout(new GridLayout(m_Main_categories.size(), 1));
        }
        for(int i = 0; i < m_Main_categories.size(); i++){
            Map cur_main_category = m_Main_categories.get(i);
            Object image_info = (Object) cur_main_category.get("image");
            String str_image_info = image_info.toString();
            String[] array_image_info = str_image_info.split(",");
            String img_link = array_image_info[5].replaceFirst("src=", "");
            System.out.println(img_link);
            JLabel lbl_image = null; 
            Image image = null;
            try {
                URL url = new URL(img_link);
                image = ImageIO.read(url);
                Image dImage = image.getScaledInstance(150, 150,Image.SCALE_SMOOTH);//Stores images generated from URLs into Image object dimg
                lbl_image = new JLabel(new ImageIcon(dImage));
                // get subcategory from main category
                // if size is 0, show products else show sub categories.
                lbl_image.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent ev) {
                        if(!right_panel) // if main category is clicked, remove history of hierachy.
                            hierachy.removeAllElements();
                        if (ev.getClickCount() == 1 && !ev.isConsumed()) { // check double clicked.
                            jPanel1.revalidate();
                            jPanel1.repaint();
                            ev.consume();
                            List<Map> subCategories = getSubCategoriesFromMainCategory(m_All_categories, cur_main_category);
                            if(subCategories.size() == 0){ // this category has products no subcategories.
                                hierachy.push((int) cur_main_category.get("id"));
                                mainFrame.setTitle("Loading.......");
                                showProductsOfCategory(param_products, (int) cur_main_category.get("id"));
                            }else{
                                System.out.println("----------subcategories--------------");
                                hierachy.push((int) cur_main_category.get("id"));
                                mainFrame.setTitle("Loading.......");
                                showCategories(subCategories, m_All_categories, param_products, true);
                            }
                        }
                    }
                });
                if(!right_panel)
                    jPanel4.add(lbl_image);     
                else
                    jPanel1.add(lbl_image);                
            }catch(IOException e){
                System.out.println("load image error" + img_link);
            }   
        }
        checkBackButton();
        this.setTitle("Loading completed");
        System.out.println("loading end");
    }
    
    // show products according to categories.
    // param_products: woocormerce params.
    public void showProductsOfCategory(Map<String, String> param_products, int cur_category_id){
        
        // there is no subcategories.
            param_products.put("category", Integer.toString(cur_category_id));
            List products = wooCommerce.getAll(EndpointBaseType.PRODUCTS.getValue(), param_products);
            jPanel1.removeAll();
            jPanel1.setLayout(new GridLayout((int)(products.size() / 5) + 1, 5,20,20));
            for(int i = 0; i < products.size(); i++ ){
                Map product = (Map) products.get(i);
                ArrayList images = (ArrayList)product.get("images");
                String[] image_info = (images.toString()).split(",");
                String image_link = image_info[5].replaceFirst("src=", "");
                System.out.println(image_link);
                Image product_image_temp = null;
                try {
                    URL url = new URL(image_link);
                    product_image_temp = ImageIO.read(url);
                    Image product_image = product_image_temp.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    JLabel lbl_product = new JLabel(new ImageIcon(product_image));
                    jPanel1.add(lbl_product);
                    lbl_product.addMouseListener(new MouseAdapter(){
                        @Override
                        public void mouseClicked(MouseEvent evpro) {
                            if (evpro.getClickCount() == 1 && !evpro.isConsumed()) {
                                evpro.consume();
                                 jPanel1.revalidate();
                                 jPanel1.repaint();
                                String name = (String) product.get("name");
                                String capturedprice = (String) product.get("price"); 
                                double price = Double.parseDouble(capturedprice);
                                jList1.setModel(model);
                                model.addElement(name + "----" + "$" + price + "\n");
                                total = total + price;
                                SubtotalTextBox.setText(df.format(total));
                                tax = round(total * taxPercent, 2);
                                TaxTextBox.setText( df.format(tax));
                                total_sum = round(total + tax, 2);
                                TotalTextBox.setText( df.format(total_sum));
                                prices.add(price);
                            }
                        }
                    });
                }catch(Exception e){
                    System.out.println("product display error.");
                 }
            }
            checkBackButton();
            mainFrame.setTitle("Loading completed");
            System.out.println("loading end");
    }
///////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Creates new form NewJFrame3
     */
    public NewJFrame3() {
        initComponents();
        model = new DefaultListModel();
        

////////////////////////////////////////////////// Vadim code//////////////////////////////

hierachy =  new Stack<Integer>();

System.out.println("------------ Vadim code ----------------------");

Map<String, String> param_category = new HashMap<>(); //creates parameters Map object
String max_categories_count = "100"; // 1 to 100
param_category.put("per_page", max_categories_count);//Sets numbers of result to retrieve
param_category.put("offset", "0");

Map<String, String> param_products = new HashMap<>(); //creates parameters Map object
param_products.put("per_page", "100");//Sets numbers of result to retrieve
param_products.put("offset", "0");

// fetch all categories
m_All_categories = wooCommerce.getAll(EndpointBaseType.PRODUCTS_CATEGORIES.getValue(), param_category);
// fetch main categories
List<Map> m_Main_categories = new ArrayList<Map>();
for(int i = 0 ; i < m_All_categories.size(); i++){
    Map category = (Map) m_All_categories.get(i);
    if((int)(category.get("parent")) == 0)
        m_Main_categories.add(category);
}
showCategories(m_Main_categories, m_All_categories, param_products, false);
System.out.println("-------------------------------------------");
///////////////////////////////////////////////////////////////////////////////////////        
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator1 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        jList1 = new javax.swing.JList<>();
        labelSubtotal = new java.awt.Label();
        SubtotalTextBox = new javax.swing.JTextArea();
        labelTotal = new java.awt.Label();
        TaxTextBox = new javax.swing.JTextArea();
        labelTax = new java.awt.Label();
        TotalTextBox = new javax.swing.JTextArea();
        deleteOrderButton = new javax.swing.JButton();
        btn_back = new javax.swing.JButton();
        OrderButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        ContainerPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        labelSubtotal.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        labelSubtotal.setText("SUB-TOTAL");

        SubtotalTextBox.setColumns(20);
        SubtotalTextBox.setRows(5);
        SubtotalTextBox.setText("0");

        labelTotal.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        labelTotal.setText("TOTAL");

        TaxTextBox.setColumns(20);
        TaxTextBox.setRows(5);
        TaxTextBox.setText("0");

        labelTax.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        labelTax.setText("TAX");

        TotalTextBox.setColumns(20);
        TotalTextBox.setRows(5);
        TotalTextBox.setText("0");

        deleteOrderButton.setText("Delete Order");
        deleteOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteOrderButtonActionPerformed(evt);
            }
        });

        btn_back.setText("Back");
        btn_back.setEnabled(false);
        btn_back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_backActionPerformed(evt);
            }
        });

        OrderButton.setText("Order");
        OrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OrderButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(labelTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelSubtotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(labelTax, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(SubtotalTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jList1, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(TaxTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 3, Short.MAX_VALUE)))
                        .addGap(6, 6, 6))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(deleteOrderButton)
                                .addGap(18, 18, 18)
                                .addComponent(OrderButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_back))
                            .addComponent(TotalTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jList1, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(deleteOrderButton, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(OrderButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_back, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(24, 24, 24)
                .addComponent(labelSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SubtotalTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addComponent(labelTax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TaxTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TotalTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        SubtotalTextBox.getAccessibleContext().setAccessibleParent(SubtotalTextBox);

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 152, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 748, Short.MAX_VALUE)
        );

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 662, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 750, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout ContainerPanelLayout = new javax.swing.GroupLayout(ContainerPanel);
        ContainerPanel.setLayout(ContainerPanelLayout);
        ContainerPanelLayout.setHorizontalGroup(
            ContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1366, Short.MAX_VALUE)
            .addGroup(ContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ContainerPanelLayout.createSequentialGroup()
                    .addContainerGap(181, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(523, Short.MAX_VALUE)))
            .addGroup(ContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ContainerPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(1186, Short.MAX_VALUE)))
        );
        ContainerPanelLayout.setVerticalGroup(
            ContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 791, Short.MAX_VALUE)
            .addGroup(ContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ContainerPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(35, Short.MAX_VALUE)))
            .addGroup(ContainerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ContainerPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(30, Short.MAX_VALUE)))
        );

        jScrollPane1.setViewportView(ContainerPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(918, 918, 918)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 877, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(395, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 756, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jScrollPane1.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void deleteOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteOrderButtonActionPerformed
       
                if(jList1.isSelectionEmpty()==true){
                     JOptionPane.showMessageDialog(null,"Select an Order to delete!","Warning!",JOptionPane.PLAIN_MESSAGE,icon);
//  JOptionPane.showMessageDialog(this,"Select an Order to delete!");
}
        
        int index = jList1.getSelectedIndex();
        double getPrice = prices.get(index);
        DefaultListModel model = (DefaultListModel) jList1.getModel();
       
        if (index > -1) {
            JOptionPane.showMessageDialog(this,"Are you sure you want to delete this order?","Warning!",JOptionPane.PLAIN_MESSAGE,icon);
            model.remove(index);
            prices.remove(index);
        }
 

        //  Subtotal = Subtotal - getPrice;
        total = total - getPrice;
        // update the value in subtotal text box
        SubtotalTextBox.setText(df.format(total));

        tax = tax - getPrice * taxPercent;
        // update the value in tax
        TaxTextBox.setText(df.format(tax));

        total_sum = total + tax;
        //update the value in total sum
        TotalTextBox.setText(df.format(total_sum));
        
           
        
        
        
        
    }//GEN-LAST:event_deleteOrderButtonActionPerformed

    private void OrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OrderButtonActionPerformed
       int totalItem = 0;
        ArrayList<String> orderArray=new ArrayList<String>();
        StringBuilder allOrders = new StringBuilder("");
        for(int i = 0; i <jList1.getModel().getSize(); i++) {
              //  orders = orders + jList2.getModel().getElementAt(i);
             //  allOrders.append("\n").append(jList2.getModel().getElementAt(i));
               orderArray.add(jList1.getModel().getElementAt(i));
               
                 //orders = orders + "\n";
               totalItem = totalItem + 1;
                
            }
      //  orders = allOrders.toString();
     // printer.printString(orderArray, totalItem);
      
      //
      float subTotal = Float.parseFloat(SubtotalTextBox.getText());
       float tax = Float.parseFloat(TaxTextBox.getText());
        float total = Float.parseFloat(TotalTextBox.getText());
       PrinterJob pj = PrinterJob.getPrinterJob();
       printing letprint = new printing();
        pj.setPrintable(letprint,letprint.getPageFormat(pj));
        try {
            letprint.printString(orderArray, subTotal, tax, total);
             pj.print();
          
        }
         catch (PrinterException ex) {
                 ex.printStackTrace();
        }
     
        //System.out.println(jList2);
//        countFrequencies(items);
    }//GEN-LAST:event_OrderButtonActionPerformed

    private void btn_backActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_backActionPerformed
        // TODO add your handling code here:
        System.out.println(hierachy);
        int category_id = hierachy.pop();
        checkBackButton();
        int parent_sel_category_id = -1;
        List<Map> m_categories = new ArrayList<Map>();
        for(int i = 0; i< m_All_categories.size(); i++){
            Map category = (Map) m_All_categories.get(i);
            if((int)category.get("id") == category_id){
                parent_sel_category_id = (int) category.get("parent");
                break;
            }
        }
        if(parent_sel_category_id == 0){ // parent is main category
            jPanel1.removeAll();
            hierachy.removeAllElements();
            mainFrame.setTitle("Loading completed");
            return;
        }
        
        for(int i = 0; i< m_All_categories.size(); i++){
            Map category = (Map) m_All_categories.get(i);
            if((int)category.get("parent") == parent_sel_category_id){
                m_categories.add(category);
            }
        }
        Map<String, String> param_products = new HashMap<>(); //creates parameters Map object
        param_products.put("per_page", "100");//Sets numbers of result to retrieve
        param_products.put("offset", "0");
        mainFrame.setTitle("Loading......");
        showCategories(m_categories, m_All_categories, param_products, true);
        System.out.println("-------------hierachy list-----------");
        System.out.println(hierachy);
    }//GEN-LAST:event_btn_backActionPerformed

   
    public static void main(String args[]) {
   
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                mainFrame = new NewJFrame3();
                mainFrame.setVisible(true);
//                new JFrame().setVisible(true);
              
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ContainerPanel;
    private javax.swing.JButton OrderButton;
    private javax.swing.JTextArea SubtotalTextBox;
    private javax.swing.JTextArea TaxTextBox;
    private javax.swing.JTextArea TotalTextBox;
    private javax.swing.JButton btn_back;
    private javax.swing.JButton deleteOrderButton;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private java.awt.Label labelSubtotal;
    private java.awt.Label labelTax;
    private java.awt.Label labelTotal;
    // End of variables declaration//GEN-END:variables
}

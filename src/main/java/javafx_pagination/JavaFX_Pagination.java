package javafx_pagination;

import java.awt.BorderLayout;
import java.awt.Button;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @web http://java-buddy.blogspot.com/
 */
public class JavaFX_Pagination extends Application {

    private Pagination pagination;
    final static int numOfPage = 5;
    
     public int itemsPerPage() {
    return 8;
  }

    public VBox createPage(int pageIndex) throws IOException {
        VBox pageBox = new VBox(5);
        int page = pageIndex * itemsPerPage();
        
   
for (int i = page; i < page + itemsPerPage(); i++) {
   FileInputStream input;
   java.awt.Image image = null;
        try {
//            input = new FileInputStream("//Users/businessmac/Desktop/BBlogo3.png");
//              Image image = new Image(input);
//        ImageView imageView = new ImageView(image);  
        
         URL url = new URL("http://digitsoftex.com/wp-content/uploads/2021/02/War-Beast-Computer-Gaming-Case-Enclosure1.jpg"); //ImgLink gets all urls for image
            image = ImageIO.read(url);
            java.awt.Image dimg = image.getScaledInstance(150, 150,java.awt.Image.SCALE_SMOOTH);
        
        VBox element = new VBox();
        JLabel[] labels = new JLabel[page + itemsPerPage()];
        labels[i]=new JLabel(new ImageIcon(dimg)); 
        Label pageLabel = new Label("" + labels[i].getIcon());
//        pageBox.getChildren().add(pageLabel);
         element.getChildren().addAll(pageLabel);
      pageBox.getChildren().add(element);
        
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JavaFX_Pagination.class.getName()).log(Level.SEVERE, null, ex);
        }}
      return pageBox;
    }

    @Override
    public void start(Stage primaryStage) {

        pagination = new Pagination(numOfPage);
        pagination.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageIndex) {
                try {
                    return createPage(pageIndex);
                } catch (IOException ex) {
                    Logger.getLogger(JavaFX_Pagination.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });

        AnchorPane anchor = new AnchorPane();
        AnchorPane.setTopAnchor(pagination, 10.0);
        AnchorPane.setRightAnchor(pagination, 10.0);
        AnchorPane.setBottomAnchor(pagination, 10.0);
        AnchorPane.setLeftAnchor(pagination, 10.0);
        anchor.getChildren().add(pagination);

        Scene scene = new Scene(anchor, 400, 300);

        primaryStage.setTitle("java-buddy.blogspot.com");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
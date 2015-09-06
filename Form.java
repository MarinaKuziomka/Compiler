import javax.swing.*;
import java.awt.*;

public class Form extends JFrame {
    public Form(int sizeWidth,int sizeHeight){
        super("Lab_3");
        Dimension sz = Toolkit.getDefaultToolkit().getScreenSize();
        int locationX = (sz.width - sizeWidth) / 2;
        int locationY = (sz.height - sizeHeight) / 2;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        setBounds(locationX, locationY, sizeWidth, sizeHeight);
        setVisible(true);
    }
}

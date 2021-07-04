package windows_app;

import ImageCompressionLib.containers.type.MyBufferedImage;
import utils.BuffImConvertor;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DifferenceForm extends JFrame {
    JPanel jPanel=new JPanel(new BorderLayout());
    JLabel jLabel=new JLabel();
    MyBufferedImage image1,image2;

    public DifferenceForm(MyBufferedImage image1, MyBufferedImage image2) throws HeadlessException {
        this.image1 = image1;
        this.image2 = image2;
        setContentPane(jPanel);
        setVisible(true);
        setSize(450,450);
        jPanel.add(jLabel);
        calculate();
    }
    private void calculate(){
        if(image2==null||image1==null)
            return;
        MyBufferedImage tmp=image1.minus(image2);
        BufferedImage bufferedImage= BuffImConvertor.getInstance().convert(tmp);
        ImageIcon icon=new ImageIcon(bufferedImage);
        ImageIcon res=new ImageIcon(icon.getImage().getScaledInstance(400,400,Image.SCALE_DEFAULT));
        jLabel.setIcon(res);
    }
}

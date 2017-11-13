import ImageCompression.Objects.ApplicationOPC;
import ImageCompression.Objects.Flag;
import ImageCompression.Objects.MyImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainForm extends JFrame{

    private JPanel mainPanel;
    private JButton button1;

    public MainForm() throws HeadlessException {
        setVisible(true);
        setContentPane(mainPanel);
        setSize(800,800);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }
    public static void main(String[] arg){
        new MainForm();
    }
}

package WindowsApp;

import ImageCompressionLib.Containers.Type.Flag;

import javax.swing.*;
import java.awt.*;

public class MainPage extends JFrame {
    private JProgressBar progressBar1;
    private JPanel mainPanel;
    private JButton selectFileButton;
    private JButton convertButton;
    private JSpinner spinner1;
    private JPanel panelFlag;

    private FlagForm flagForm;


    public MainPage() throws HeadlessException {
        setContentPane(mainPanel);
        setVisible(true);
        setSize(700,500);
        init();
    }
    private void init(){
        flagForm=new FlagForm(new Flag(),panelFlag);
    }


    public static void main(String[] a){
        new MainPage();
    }
}

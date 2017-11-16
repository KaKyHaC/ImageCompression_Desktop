import ImageCompression.Containers.Matrix;
import ImageCompression.Objects.ApplicationOPC;
import ImageCompression.Objects.Flag;
import ImageCompression.Objects.MyImage;
import ImageCompression.Objects.Parameters;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainForm extends JFrame{

    private JPanel mainPanel;
    private JButton bSelect;
    private JLabel image;
    private JButton bExe;
    private JLabel lInfo;

    private Parameters parameters = Parameters.getInstanse();
    private ApplicationOPC applicationOPC = ApplicationOPC.getInstance();

    public MainForm() throws HeadlessException {
        setVisible(true);
        setContentPane(mainPanel);
        setSize(800,800);
        setResizable(false);
        setLocation(new Point(200,100));

        Init();
        setListeners();
    }
    private void Init(){
        File dir=new File(parameters.PathAppDir);
        dir.mkdir();
    }
    private void setListeners(){
        bSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser=new JFileChooser();
                jFileChooser.setCurrentDirectory(new File(parameters.PathAppDir));
                int res=jFileChooser.showDialog(null,"Choose Image");
                if(res==JFileChooser.APPROVE_OPTION){
                    File file=jFileChooser.getSelectedFile();
                    onFileSelected(file);
                }
            }
        });
    }
    private void onFileSelected(File file){
        String name=file.getName();
        lInfo.setText("Name: "+name+" ,Size: "+file.length()/1024+"kB ");

//        if(name.contains(".bmp")||name.contains(".BMP")||name.contains(".jpg"))
            setLableImage(file);
        try {
            processImage(file);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    private void setLableImage(File imagef){
        try {
            BufferedImage myPicture = ImageIO.read(imagef);
            ImageIcon imageIcon=new ImageIcon(myPicture);
            lInfo.setText(lInfo.getText()+"\n "+imageIcon.getIconWidth()+"x"+imageIcon.getIconHeight());
            Image image1=imageIcon.getImage().getScaledInstance(600,600,Image.SCALE_DEFAULT);
            image.setIcon(new ImageIcon(image1));

        } catch (IOException e1) {
            e1.printStackTrace();
            System.out.println(e1.getMessage());
        }
    }
    private void processImage(File file) throws IOException {
        BufferedImage  bufferedImage=ImageIO.read(file);

    }
    public static void main(String[] arg){
        new MainForm();
    }

}

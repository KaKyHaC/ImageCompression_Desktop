package ImageCompression.Steganography;

import ImageCompression.Steganography.Containers.IContainer;
import ImageCompression.Steganography.Containers.OpcContainer;
import ImageCompression.Steganography.Utils.ImageProcessorUtils;
import ImageCompression.Steganography.Utils.MessageParser;
import ImageCompression.Utils.Objects.Parameters;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class StegonagraphyUI extends JFrame {
    private JLabel originalImage;
    private JLabel targetImage;
    private JButton bSelect;
    private JTextField textField1;
    private JRadioButton rMultiTWO;
    private JSpinner spinner1;
    private JSpinner spinner2;
    private JPanel mainForm;
    private JLabel lInfo;
    private JProgressBar progressBar1;
    private JButton bStart;
    private JLabel resMessage;
    //
    private Parameters parameters = Parameters.getInstanse();
    private MessageParser messageParser=new MessageParser();
    private File file,resFile;
    private Boolean isDirect=true;

    StegonagraphyUI() {
        this.show();
        this.setContentPane(mainForm);
        setSize(800,600);
        setResizable(false);

        init();
        setListeners();
    }
    void init(){
        spinner1.setValue(8);
        spinner2.setValue(8);
        textField1.setText("Message");
        rMultiTWO.setSelected(true);
    }
    void setListeners(){
        bSelect.addActionListener(e -> {
            JFileChooser jFileChooser=new JFileChooser();
            jFileChooser.setCurrentDirectory(new File(parameters.PathAppDir));
            int res=jFileChooser.showDialog(null,"Choose Image");
            if(res==JFileChooser.APPROVE_OPTION){
                file=jFileChooser.getSelectedFile();
                onFileSelected(file);
            }
        });
        bStart.addActionListener(e->{
            JFileChooser jFileChooser=new JFileChooser();
            jFileChooser.setCurrentDirectory(new File(parameters.PathAppDir));
            int res=jFileChooser.showDialog(null,"Choose Image");
            if(res==JFileChooser.APPROVE_OPTION){
                resFile=jFileChooser.getSelectedFile();

                onResFileSelected(resFile);
            }
        });
    }

    private void onResFileSelected(File tarFile){
        try {
            tarFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        ImageProcessorUtils.Triple<IContainer<OpcContainer<Short>>> opcs=directStego(file);
//        reverceStego(opcs,w,h,tarFile);
    }
    private void reverceStego(File from,File to){
        StegoModule sm=new StegoModule();
        int uW=(int)spinner1.getValue();
        int uH=(int)spinner2.getValue();
        boolean isMulti=rMultiTWO.isSelected();
        boolean[] res= sm.reverse(from,to,uW,uH,isMulti);
        String sMessage=messageParser.toString(res);
        resMessage.setText(sMessage);
        setLableImage(to,targetImage);
    }
    private void directStego(File from,File to){
        StegoModule sm=new StegoModule();
        int uW=(int)spinner1.getValue();
        int uH=(int)spinner2.getValue();
        String message=textField1.getText();
        boolean[] bM=messageParser.fromString(message);
        sm.direct(from,to,uW,uH,bM);
    }
    private void onFileSelected(File file){
        String name=file.getName();
        lInfo.setText("Name: "+name+" ,Size: "+file.length()/1024+"kB ");

        if(name.contains(".bmp")||name.contains(".BMP")||name.contains(".jpg")) {
            setLableImage(file,originalImage);
            isDirect=true;
            bStart.setText("Convert to BAR");
        }else{
            isDirect=false;
            bStart.setText("Convert from BAR");
        }
//        }else if(name.contains(myType)){
////            bExe.setText("Convert to BMP");
//        }
    }
    private void setLableImage(File imagef,JLabel lable){
        try {
            BufferedImage myPicture = ImageIO.read(imagef);
            ImageIcon imageIcon=new ImageIcon(myPicture);
            lInfo.setText(lInfo.getText()+"\n "+imageIcon.getIconWidth()+"x"+imageIcon.getIconHeight());
            Dimension size=originalImage.getSize();
            Image image1=imageIcon.getImage().getScaledInstance(size.width,size.height,Image.SCALE_DEFAULT);
            lable.setIcon(new ImageIcon(image1));
//            targetImage.setIcon(new ImageIcon(image1));

        } catch (IOException e1) {
            e1.printStackTrace();
            System.out.println(e1.getMessage());
        }
    }


    public static void main(String[] arg){
        StegonagraphyUI form=new StegonagraphyUI();

    }
}

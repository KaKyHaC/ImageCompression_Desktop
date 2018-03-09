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
    private File file,resFile;
    private int w,h;

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
        ImageProcessorUtils.Triple<IContainer<OpcContainer<Short>>> opcs=directStego(file);
        reverceStego(opcs,w,h,tarFile);
    }
    private void reverceStego(ImageProcessorUtils.Triple<IContainer<OpcContainer<Short>>> units,int width,int height,File target){
        StegoModule sm=new StegoModule();
        int uW=(int)spinner1.getValue();
        int uH=(int)spinner2.getValue();
        boolean isMulti=rMultiTWO.isSelected();
        boolean[] res;
        res= sm.reverse(units,width,height,uW,uH,isMulti,target.getAbsolutePath());
        byte[] byteMess=new MessageParser().parseMessage(res);
        String sMessage=byteMess.toString();
        resMessage.setText(sMessage);
        setLableImage(target,targetImage);
    }
    private ImageProcessorUtils.Triple<IContainer<OpcContainer<Short>>> directStego(File file){
        StegoModule sm=new StegoModule();
        int uW=(int)spinner1.getValue();
        int uH=(int)spinner2.getValue();
        String message=textField1.getText();
        byte[] bM=message.getBytes();
        boolean[] boolMess=new MessageParser().parseMessage(bM);
        ImageProcessorUtils.Triple<IContainer<OpcContainer<Short>>> res;
        res= sm.direct(file.getAbsolutePath(), uW, uH, boolMess);
        return res;
    }
    private void onFileSelected(File file){
        String name=file.getName();
        lInfo.setText("Name: "+name+" ,Size: "+file.length()/1024+"kB ");

        if(name.contains(".bmp")||name.contains(".BMP")||name.contains(".jpg")) {
            setLableImage(file,originalImage);
//            bExe.setText("Convert to BAR");
        }
//        }else if(name.contains(myType)){
////            bExe.setText("Convert to BMP");
//        }
    }
    private void setLableImage(File imagef,JLabel lable){
        try {
            BufferedImage myPicture = ImageIO.read(imagef);
            ImageIcon imageIcon=new ImageIcon(myPicture);
            w=myPicture.getWidth();
            h=myPicture.getHeight();
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

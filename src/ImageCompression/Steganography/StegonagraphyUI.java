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
    private JRadioButton rbText;
    private JRadioButton rbFile;
    private JButton bMessageFile;
    private JPanel panelMessageFile;
    private JPanel panelMessageText;
    private JLabel lMessageFile;
    private JRadioButton rbNoMessage;
    private JPanel panelForReverse;
    private JPanel panelImage1;
    private JPanel panelImage2;
    //
    private Parameters parameters = Parameters.getInstanse();
    private MessageParser messageParser=new MessageParser();
    private ButtonGroup buttonGroup=new ButtonGroup();
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
        progressBar1.setMaximum(100);
        buttonGroup.add(rbFile);
        buttonGroup.add(rbText);
        buttonGroup.add(rbNoMessage);
        panelImage1.setMaximumSize(panelImage1.getSize());
        panelImage2.setMaximumSize(panelImage2.getSize());

        showPanel();
        panelForReverse.setVisible(false);
        bStart.setEnabled(false);
    }
    void setListeners(){
        bSelect.addActionListener(e -> {
            JFileChooser jFileChooser=new JFileChooser();
            jFileChooser.setCurrentDirectory(new File(parameters.PathAppDir));
            int res=jFileChooser.showDialog(null,"Choose Image");
            if(res==JFileChooser.APPROVE_OPTION){
                file=jFileChooser.getSelectedFile();
                bStart.setEnabled(true);
                onFileSelected(file);
            }
        });
        bStart.addActionListener(e->{
            JFileChooser jFileChooser=new JFileChooser();
            jFileChooser.setCurrentDirectory(new File(parameters.PathAppDir));
            int res=jFileChooser.showDialog(null,"Save");
            if(res==JFileChooser.APPROVE_OPTION){
                resFile=jFileChooser.getSelectedFile();
                onResFileSelected(resFile);
            }
        });
        rbText.addActionListener(e->showPanel());
        rbFile.addActionListener(e->showPanel());
        rbNoMessage.addActionListener(e->showPanel());
    }

    private void showPanel(){
        panelMessageText.setVisible(rbText.isSelected());
        panelMessageFile.setVisible(rbFile.isSelected());
    }
    private void onResFileSelected(File tarFile){
        bStart.setEnabled(false);
        bSelect.setEnabled(false);
        progressBar1.setValue(0);
        new Thread(()-> {
            try {
                tarFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            progressBar1.setValue(10);
            if (isDirect) {
                directStego(file, resFile);
            } else {
                reverceStego(file, resFile);
            }
            bStart.setEnabled(true);
            bSelect.setEnabled(true);
            progressBar1.setValue(100);
        }).start();
//        ImageProcessorUtils.Triple<IContainer<OpcContainer<Short>>> opcs=directStego(file);
//        reverceStego(opcs,w,h,tarFile);
    }
    private void reverceStego(File from,File to){
        StegoModule sm=new StegoModule();
        int uW=(int)spinner1.getValue();
        int uH=(int)spinner2.getValue();
        boolean isMulti=rMultiTWO.isSelected();
//        BufferedImage image=sm.getImageFromStego(file,uW,uH,isMulti);
//        setLableImage(image,targetImage);
//        try {
//            ImageIO.write(image,"BMP",to);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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
            bStart.setText("Convert to BMP");
        }
        panelForReverse.setVisible(!isDirect);
//        }else if(name.contains(myType)){
////            bExe.setText("Convert to BMP");
//        }
    }
    private void setLableImage(BufferedImage image,JLabel label){
//        Dimension panelSize=panelImage1.getSize();

        ImageIcon imageIcon=new ImageIcon(image);
        lInfo.setText(lInfo.getText()+"\n "+imageIcon.getIconWidth()+"x"+imageIcon.getIconHeight());
        Dimension size=label.getSize();
        label.setMaximumSize(size);
        Image image1=imageIcon.getImage().getScaledInstance(size.width,size.height,Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(image1));

        panelImage1.setSize(size);
        panelImage2.setSize(size);
//        panelImage1.
//        panelImage2.setSize(panelSize);
//        Dimension panelSize1=panelImage1.getSize();
//        Dimension panelSize2=panelImage2.getSize();

    }
    private void setLableImage(File imagef,JLabel label){
        try {
            BufferedImage myPicture = ImageIO.read(imagef);
            setLableImage(myPicture,label);
        } catch (IOException e1) {
            e1.printStackTrace();
            System.out.println(e1.getMessage());
        }
    }


    public static void main(String[] arg){
        StegonagraphyUI form=new StegonagraphyUI();

    }
}

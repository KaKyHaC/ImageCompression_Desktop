package ImageCompression.Steganography;

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
    private JLabel lInfoLable;
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
    private JPanel panelForDirect;
    private JTextPane lInfo;
    private JButton bResFile;
    //
    private Parameters parameters = Parameters.getInstanse();
    private MessageParser messageParser=new MessageParser();
    private ButtonGroup buttonGroup=new ButtonGroup();
    private File file,resFile;
    private Boolean isDirect=true;
    private Dimension imageSize=null;

    StegonagraphyUI() {
        this.show();
        this.setContentPane(mainForm);
        setSize(800,600);
        setResizable(false);

        init();
        setListeners();
    }
    private void init(){
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
        panelForDirect.setVisible(false);
        bStart.setEnabled(false);
    }

    private void setListeners(){
        bSelect.addActionListener(e -> {
            JFileChooser jFileChooser=new JFileChooser();
            jFileChooser.setCurrentDirectory(new File(parameters.PathAppDir));
            int res=jFileChooser.showDialog(null,"Choose Image");
            if(res==JFileChooser.APPROVE_OPTION){
                file=jFileChooser.getSelectedFile();
                resFile=null;
                bStart.setEnabled(true);
                onFileSelected(file);
            }
        });
        bStart.addActionListener(e->{
            if(resFile==null)
                selectResFile();

            onResFileSelected(resFile);
        });
        bResFile.addActionListener(e->{
            selectResFile();
        });
        rbText.addActionListener(e->showPanel());
        rbFile.addActionListener(e->showPanel());
        rbNoMessage.addActionListener(e->showPanel());
    }
    private void showPanel(){
        panelMessageText.setVisible(rbText.isSelected());
        panelMessageFile.setVisible(rbFile.isSelected());
    }
    private void selectResFile(){
        JFileChooser jFileChooser=new JFileChooser();
        jFileChooser.setCurrentDirectory(new File(parameters.PathAppDir));
        int res=jFileChooser.showDialog(null,"Save to");
        if(res==JFileChooser.APPROVE_OPTION){
            resFile=jFileChooser.getSelectedFile();
        }
    }
    private void onResFileSelected(File tarFile){
        bStart.setEnabled(false);
        bSelect.setEnabled(false);
        bResFile.setEnabled(false);
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
            bResFile.setEnabled(true);
            progressBar1.setValue(100);
        }).start();
//        ImageProcessorUtils.Triple<IContainer<OpcContainer<Short>>> opcs=directStego(file);
//        reverceStego(opcs,w,h,tarFile);
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
        panelForDirect.setVisible(isDirect);
//        }else if(name.contains(myType)){
////            bExe.setText("Convert to BMP");
//        }
    }


    private void reverceStego(File from,File to){
        StegoModule sm=new StegoModule();
        int uW=(int)spinner1.getValue();
        int uH=(int)spinner2.getValue();
        boolean isMulti=rMultiTWO.isSelected();
        boolean[] res= sm.reverse(from,to,uW,uH,isMulti);
        setMessage(res);
        setLableImage(to,targetImage);
    }
    private void directStego(File from,File to){
        StegoModule sm=new StegoModule();
        int uW=(int)spinner1.getValue();
        int uH=(int)spinner2.getValue();
        boolean[] bM=getMessage();
        sm.direct(from,to,uW,uH,bM);
    }

    private boolean[] getMessage(){
        boolean[] bM=null;
        if(rbText.isSelected()) {
            String message = textField1.getText();
            bM = messageParser.fromString(message);
        }
        return bM;
    }
    private void setMessage(boolean[] mesasge){
        if(rbText.isSelected()) {
            String sMessage = messageParser.toString(mesasge);
            resMessage.setText(sMessage);
        }
    }


    private void setLableImage(BufferedImage image,JLabel label){
        if(imageSize==null)
            imageSize=label.getSize();
//        Dimension panelSize=panelImage1.getSize();

        ImageIcon imageIcon=new ImageIcon(image);
        lInfo.setText(lInfo.getText()+"\n "+imageIcon.getIconWidth()+"x"+imageIcon.getIconHeight());
//        Dimension size=label.getSize();
//        label.setMaximumSize(size);
        Image image1=imageIcon.getImage().getScaledInstance(imageSize.width,imageSize.height,Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(image1));

        label.setSize(imageSize);
//        panelImage2.setSize(imageSize);
//        panelImage1.setSize(imageSize);

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

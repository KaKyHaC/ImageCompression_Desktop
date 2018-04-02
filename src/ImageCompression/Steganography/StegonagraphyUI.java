package ImageCompression.Steganography;

import ImageCompression.Steganography.Utils.MessageParser;
import ImageCompression.Utils.Objects.Parameters;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

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
    private JButton bFileRes;
    //
    private Parameters parameters = Parameters.getInstanse();
    private MessageParser messageParser=new MessageParser();
    private ButtonGroup buttonGroup=new ButtonGroup();
    private File file,fileRes,fileMessage;
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
        progressBar1.setString("0");

        showPanel();
        panelForReverse.setVisible(false);
        panelForDirect.setVisible(false);
        bStart.setEnabled(false);
    }

    private void setListeners(){
        bSelect.addActionListener(e -> {
            File ans=openFileDialog(file,"Choose original file","Choose File");
            if(ans!=null){
                file=ans;
                fileRes=null;
                fileMessage=null;
                bStart.setEnabled(true);
                onFileSelected(file);
            }
        });
        bStart.addActionListener(e->{
            if(fileRes==null)
                fileRes=openFileDialog(fileRes,"Choose destination file","Save To");

            onfileResSelected(fileRes);
        });
        bFileRes.addActionListener(e->{
            fileRes=openFileDialog(fileRes,"Choose destination file","Save To");
        });
        bMessageFile.addActionListener(e->{
            fileMessage=openFileDialog(fileMessage,"Choose message file","Select Message");
            if(fileMessage!=null){
                lMessageFile.setText(fileMessage.toString());
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
    private File openFileDialog(File defFile,String title,String approveText){
        JFileChooser jFileChooser=new JFileChooser();
        jFileChooser.setCurrentDirectory(new File(parameters.PathAppDir));
        jFileChooser.setDialogTitle(title);
        if(defFile!=null)jFileChooser.setSelectedFile(defFile);
        int res=jFileChooser.showDialog(null,approveText);
        File result=null;
        if(res==JFileChooser.APPROVE_OPTION){
            result=jFileChooser.getSelectedFile();
        }
        return result;
    }
    private void onfileResSelected(File tarFile){
        bStart.setEnabled(false);
        bSelect.setEnabled(false);
        bFileRes.setEnabled(false);
        progressBar1.setValue(0);
        restartTimer();
        new Thread(()-> {
            try {
                tarFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            progressBar1.setValue(10);
            if (isDirect) {
                directStego(file, fileRes);
            } else {
                reverceStego(file, fileRes);
            }
            bStart.setEnabled(true);
            bSelect.setEnabled(true);
            bFileRes.setEnabled(true);
            progressBar1.setValue(100);
            finishTimer();
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
        boolean isMulti=rMultiTWO.isSelected();
        sm.direct(from,to,uW,uH,bM,isMulti);
    }

    private boolean[] getMessage(){
        boolean[] bM=null;
        if(rbText.isSelected()) {
            String message = textField1.getText();
            bM = messageParser.fromString(message);
        }else if(rbFile.isSelected()){
            if(fileMessage==null)
                bMessageFile.doClick();
            bM=messageParser.fromFile(fileMessage);
        }
        return bM;
    }
    private void setMessage(boolean[] mesasge){
        if(rbText.isSelected()) {
            String sMessage = messageParser.toString(mesasge);
            resMessage.setText(sMessage);
        }else if(rbFile.isSelected()){
            if(fileMessage==null)
                bMessageFile.doClick();
            messageParser.toFile(mesasge,fileMessage);
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

    boolean isTimer=false;
    Long timeStart ;
    private void restartTimer(){
        isTimer=true;
        timeStart=Calendar.getInstance().getTimeInMillis();
        new Thread(()->{
            while (isTimer){
                Long timeNow = Calendar.getInstance().getTimeInMillis();
                progressBar1.setString(String.valueOf(timeNow-timeStart)+" ms");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    private void finishTimer(){
        isTimer=false;
        Long timeNow = Calendar.getInstance().getTimeInMillis();
        progressBar1.setString(String.valueOf(timeNow-timeStart)+" ms");
    }

    public static void main(String[] arg){
        StegonagraphyUI form=new StegonagraphyUI();

    }
}

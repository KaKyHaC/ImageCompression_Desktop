package windows_app;

import ImageCompressionLib.containers.ByteVectorContainer;
import ImageCompressionLib.containers.EncryptParameters;
import ImageCompressionLib.containers.Parameters;
import ImageCompressionLib.containers.type.ByteVector;
import ImageCompressionLib.containers.type.Flag;
import ImageCompressionLib.containers.type.MyBufferedImage;
import ImageCompressionLib.containers.type.Size;
import ImageCompressionLib.convertor.ConvertorDefault;
import ImageCompressionLib.utils.functions.ImageStandardDeviation;
import ImageCompressionLib.utils.functions.opc.IStegoMessageUtil;
import utils.BuffImConvertor;
import utils.StegoPassword;
import kotlin.Pair;
import kotlin.jvm.functions.Function0;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class MainPage extends JFrame {
    private JProgressBar progressBar1;
    private JPanel mainPanel;
    private JButton selectFileButton;
    private JButton convertButton;
    private JSpinner spinnerUnitWidth;
    private JPanel panelFlag;
    private JSpinner spinnerUnitHeight;
    private JSpinner spinnerBaseWidth;
    private JSpinner spinnerBaseHeight;
    private JLabel labelImage1;
    private JLabel labelImage2;
    private JLabel labelInfo;
    private JPasswordField passwordField1;
    private JEditorPane editorPaneMessage;
    private JSpinner spinnerPosition;
    private JRadioButton steganographyRadioButton;
    private JPanel panelButton;
    private JButton differenceButton;
    private JLabel labelDeviation;
    private JButton reverceButton;
    private JLabel labelInfoBefore;
    private JLabel labelInfoAfter;
    private JButton maxCompressionButton;
    private JButton steganographyButton;
    private JTextField stegoPass;

    private FlagForm flagForm;
    private ConvertorDefault convertorDefault;
//    private File toSave,toRead;
    private Size imageIconSize=new Size(300,200);
    private MyBufferedImage imageOriginal,imageDestination;
    private ByteVectorContainer byteVectorContainer;
    private File fileImage,fileBVC,fileImageRes;

    //TODO StegoPass
    public MainPage() throws HeadlessException {
        setContentPane(mainPanel);
        setVisible(true);
        setSize(1050,650);
        init();
        setListeners();
        setDefaultValues();
    }
    private void setDefaultValues(){
        spinnerUnitHeight.setValue(8);
        spinnerUnitWidth.setValue(8);
        spinnerBaseWidth.setValue(1);
        spinnerBaseHeight.setValue(1);
    }
    private void init(){
        flagForm=new FlagForm(Flag.createDefaultFlag(),panelFlag);
        ConvertorDefault.IDao iDao=new ConvertorDefault.IDao() {
            @Override
            public void onResultByteVectorContainer(@NotNull ByteVectorContainer vector) {
                byteVectorContainer=vector;
                try {
                    vector.writeToStream(new FileOutputStream(fileBVC));
                } catch (Exception e) {
                    e.printStackTrace();
                    labelInfo.setText(e.toString());
                }
            }

            @Override
            public void onResultImage(@NotNull MyBufferedImage image, @NotNull Parameters parameters) {
                imageDestination=image;
                BufferedImage bufferedImage= utils.BuffImConvertor.getInstance().convert(image);
                setLabelImage(labelImage2,bufferedImage);
                try {
                    ImageIO.write(bufferedImage,"BMP",fileImageRes);
                } catch (IOException e) {
                    e.printStackTrace();
                    labelInfo.setText(e.toString());
                }
            }

            @NotNull
            @Override
            public Pair<MyBufferedImage, Parameters> getImage() {
                Flag flag = flagForm.getFlag();
                Size unit = new Size((int) spinnerUnitWidth.getValue(), (int) spinnerUnitHeight.getValue());
                Size base = new Size((int) spinnerBaseWidth.getValue(), (int) spinnerBaseHeight.getValue());
                Size imageSize = new Size(imageOriginal.getWidth(), imageOriginal.getHeight());
                Parameters parameters = new Parameters(flag, imageSize, unit, base);
                return new Pair<>(imageOriginal, parameters);
            }

            @NotNull
            @Override
            public ByteVectorContainer getByteVectorContainer() {
                byteVectorContainer.getMainData().refreshIterator();
                return byteVectorContainer;
            }
        };
        ConvertorDefault.IGuard iGuard=new ConvertorDefault.IGuard(){
            @Override
            public void onMessageRead(@NotNull ByteVector vector) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        editorPaneMessage.setText(new String(vector.toByteArray()));
                    }
                }).start();
            }
            @Nullable
            @Override
            public EncryptParameters getEncryptProperty() {
                EncryptParameters encryptParameters = new EncryptParameters();
                encryptParameters.setPassword(String.valueOf(passwordField1.getPassword()));

                if(!steganographyRadioButton.isSelected())
                    return encryptParameters;

                String message = editorPaneMessage.getText();
                byte[] tmp = message.getBytes();
                ByteVector vector = new ByteVector(tmp.length + 1);
                for (byte b : tmp)
                    vector.append(b);
                encryptParameters.setMessage(vector);

                int pos = (int) spinnerPosition.getValue();
                encryptParameters.setSteganography(new EncryptParameters.StegaParameters(pos, new Function0<IStegoMessageUtil>() {
                    @Override
                    public IStegoMessageUtil invoke() {
                        return new StegoPassword(stegoPass.getText());
                    }
                }));

                return encryptParameters;
            }
        };
        convertorDefault=new ConvertorDefault(iDao,iGuard);
    }
    private void setListeners(){
        selectFileButton.addActionListener(e -> onSelectButton());
        convertButton.addActionListener(e -> onStartButton(FileType.BMP));
        convertorDefault.setProgressListener((integer, s) -> {
            SwingUtilities.invokeLater(()->{
                progressBar1.setValue(integer);
                progressBar1.setString(s);
                labelInfo.setText(s);
            });
            return null;
        });
        differenceButton.addActionListener(e->{new DifferenceForm(imageOriginal,imageDestination);});
        reverceButton.addActionListener(e->onStartButton(FileType.BAR));
        maxCompressionButton.addActionListener(e->{setMaxCompressionButton();});
        steganographyButton.addActionListener(e->{setSteganographyButton();});
    }

    private void onSelectButton(){
        JFileChooser jFileChooser=new JFileChooser(windows_app.Parameters.getInstanse().PathAppDir);
        jFileChooser.showOpenDialog(this);
        File file=jFileChooser.getSelectedFile();
        onFileSelected(file);
    }
    enum FileType{BMP,BAR}
//    FileType curFileType;
    private void onFileSelected(File file){
        System.out.println(file);
        setFiles(file);
    }
    private void onStartButton(FileType curFileType){
        if(curFileType==FileType.BMP&&imageOriginal==null) {
            onSelectButton();
            return;
        }
        if(curFileType==FileType.BAR&&byteVectorContainer==null) {
            onSelectButton();
            return;
        }

        panelButton.setVisible(false);
        new Thread(()->{
            try {
                if (curFileType == FileType.BMP)
                    convertorDefault.FromBmpToBar(ConvertorDefault.Computing.MultiThreads);
                else
                    convertorDefault.FromBarToBmp(ConvertorDefault.Computing.MultiThreads);
                afterConvert();
            }catch (Exception e){
                e.printStackTrace();
                labelInfo.setText(e.toString());
            }finally {
                panelButton.setVisible(true);
            }
        }).start();
    }
    private void afterConvert(){
        if(imageOriginal!=null&&imageDestination!=null)
            labelDeviation.setText(Double.toString(ImageStandardDeviation.getDeviation(imageOriginal,imageDestination)));

        labelInfoAfter.setText("File:"+fileBVC.getName()+", Size:"+fileBVC.length()/1024+" kb");
    }
    private void setLabelImage(JLabel label, BufferedImage image) {
        ImageIcon imageIcon = new ImageIcon(image);
        labelInfo.setText(imageIcon.getIconWidth() + "x" + imageIcon.getIconHeight());
        Image image1 = imageIcon.getImage().getScaledInstance(imageIconSize.getWidth(),imageIconSize.getHeight(), Image.SCALE_DEFAULT);
        label.setIcon(new ImageIcon(image1));
    }
    private void setMaxCompressionButton(){
        Flag flag=new Flag();
        flag.setTrue(Flag.Parameter.DC);
        flag.setTrue(Flag.Parameter.DCT);
        flag.setTrue(Flag.Parameter.Enlargement);
        flag.setTrue(Flag.Parameter.Quantization);
        flag.setTrue(Flag.Parameter.CompressionUtils);
        flag.setTrue(Flag.Parameter.OneFile);
        flag.setTrue(Flag.Parameter.LongCode);
        flag.setTrue(Flag.Parameter.ColorConversions);
        flagForm.setFlag(flag);
        spinnerUnitWidth.setValue(8);
        spinnerUnitHeight.setValue(8);
        spinnerBaseHeight.setValue(1);
        spinnerBaseWidth.setValue(1);
        steganographyRadioButton.setSelected(false);
    }
    private void setSteganographyButton(){
        Flag flag=new Flag();
        flag.setTrue(Flag.Parameter.DC);
        flag.setTrue(Flag.Parameter.CompressionUtils);
        flag.setTrue(Flag.Parameter.OneFile);
        flag.setTrue(Flag.Parameter.GlobalBase);
        flag.setTrue(Flag.Parameter.ByteBase);
        flagForm.setFlag(flag);
        spinnerUnitWidth.setValue(1);
        spinnerUnitHeight.setValue(8);
        spinnerBaseHeight.setValue(1);
        spinnerBaseWidth.setValue(8);
        spinnerPosition.setValue(1);
        steganographyRadioButton.setSelected(true);
    }

    private void resetFields(){
        fileBVC=fileImageRes=fileImage=null;
        imageOriginal=null;
        byteVectorContainer=null;
        labelInfo.setText("");
        labelInfoAfter.setText("");
        labelInfoBefore.setText("");
        labelDeviation.setText("");
        labelImage1.setIcon(null);
        labelImage2.setIcon(null);
    }
    private void setFiles(File selected){
        System.out.println(selected);
        resetFields();
        FileType curFileType = (selected.getAbsolutePath().contains("bar")) ? FileType.BAR : FileType.BMP;
        String filePath=selected.getAbsolutePath();
        String newFile=filePath.substring(0,filePath.length()-4);//+(curFileType==FileType.BMP?".bar":"Res.bmp");
        if(curFileType==FileType.BMP){
            fileImage=selected;
            fileBVC= new File(newFile+".bar");
            labelInfoBefore.setText("File:"+fileImage.getName()+", Size:"+fileImage.length()/1024+" kb");
            try{
                BufferedImage imageReaded=ImageIO.read(fileImage);
                setLabelImage(labelImage1,imageReaded);
                imageOriginal=BuffImConvertor.getInstance().convert(imageReaded);
                fileBVC.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                labelInfo.setText(e.toString());
            }
        }else{
            fileBVC=selected;
            try{
                byteVectorContainer=ByteVectorContainer.readFromStream(new FileInputStream(fileBVC));
            }catch (Exception e){
                labelInfo.setText(e.toString());
                e.printStackTrace();
            }
        }
        fileImageRes=new File(newFile+"Res.bmp");
        try{
            fileImageRes.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            labelInfo.setText(e.toString());
        }
    }

    public static void main(String[] a){
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch(Exception ignored){}
        new MainPage();
    }
}


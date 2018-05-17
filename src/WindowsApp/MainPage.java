package WindowsApp;

import ImageCompressionLib.Containers.ByteVectorContainer;
import ImageCompressionLib.Containers.EncryptParameters;
import ImageCompressionLib.Containers.Parameters;
import ImageCompressionLib.Containers.Type.ByteVector;
import ImageCompressionLib.Containers.Type.Flag;
import ImageCompressionLib.Containers.Type.MyBufferedImage;
import ImageCompressionLib.Containers.Type.Size;
import ImageCompressionLib.Convertor.ConvertorDefault;
import ImageCompressionLib.ProcessingModules.ModuleFile;
import ImageCompressionLib.Utils.Functions.Opc.IStegoMessageUtil;
import Utils.BuffImConvertor;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    private FlagForm flagForm;
    private ConvertorDefault convertorDefault;
    private File toSave,toRead;
    private BufferedImage imageReaded;


    public MainPage() throws HeadlessException {
        setContentPane(mainPanel);
        setVisible(true);
        setSize(850,500);
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
//                ModuleFile file=new ModuleFile(toSave);
//                file.write(vector,flagForm.getFlag());
                try {
                    vector.writeToStream(new FileOutputStream(toSave));
                } catch (Exception e) {
                    e.printStackTrace();
                    labelInfo.setText(e.toString());
                }
            }

            @Override
            public void onResultImage(@NotNull MyBufferedImage image, @NotNull Parameters parameters) {
                BufferedImage bufferedImage=Utils.BuffImConvertor.getInstance().convert(image);
                setLabelImage(labelImage2,bufferedImage);
                try {
                    ImageIO.write(bufferedImage,"PNG",toSave);
                } catch (IOException e) {
                    e.printStackTrace();
                    labelInfo.setText(e.toString());
                }
            }

            @NotNull
            @Override
            public Pair<MyBufferedImage, Parameters> getImage() {
                MyBufferedImage myBufferedImage = BuffImConvertor.getInstance().convert(imageReaded);
                Flag flag = flagForm.getFlag();
                Size unit = new Size((int) spinnerUnitWidth.getValue(), (int) spinnerUnitHeight.getValue());
                Size base = new Size((int) spinnerBaseWidth.getValue(), (int) spinnerBaseHeight.getValue());
                Size imageSize = new Size(imageReaded.getWidth(), imageReaded.getHeight());
                Parameters parameters = new Parameters(flag, imageSize, unit, base);
                return new Pair<>(myBufferedImage, parameters);
            }

            @NotNull
            @Override
            public ByteVectorContainer getByteVectorContainer() {
//                ModuleFile moduleFile=new ModuleFile(toRead);
//                return moduleFile.read();
                try{
                    return ByteVectorContainer.readFromStream(new FileInputStream(toRead));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    labelInfo.setText(e.toString());
                }
                return null;
            }
        };
        ConvertorDefault.IGuard iGuard=new ConvertorDefault.IGuard(){
            @Override
            public void onMessageRead(@NotNull ByteVector vector) {
                editorPaneMessage.setText(new String(vector.toByteArray()));
            }
            @Nullable
            @Override
            public EncryptParameters getEncryptProperty() {
                EncryptParameters encryptParameters = new EncryptParameters();
                encryptParameters.setPassword(passwordField1.getPassword().toString());

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
                        return new IStegoMessageUtil() {
                            @Override
                            public boolean isUseNextBlock() {
                                return true;
                            }
                        };
                    }
                }));

                return encryptParameters;
            }
        };
        convertorDefault=new ConvertorDefault(iDao,iGuard);
    }
    private void setListeners(){
        selectFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSelectButton();
            }
        });
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onStartButton();
            }
        });
        convertorDefault.setProgressListener(new Function2<Integer, String, Unit>() {
            @Override
            public Unit invoke(Integer integer, String s) {
                SwingUtilities.invokeLater(()->{
                    progressBar1.setValue(integer);
                    progressBar1.setString(s);
                });
                return null;
            }
        });
        convertorDefault.setOnImageReadyListener(new Function1<MyBufferedImage, Unit>() {
            @Override
            public Unit invoke(MyBufferedImage myBufferedImage) {
                BufferedImage im=BuffImConvertor.getInstance().convert(myBufferedImage);
                setLabelImage(labelImage2,im);
                return null;
            }
        });
    }

    private void onSelectButton(){
        JFileChooser jFileChooser=new JFileChooser(ImageCompressionLib.Parameters.getInstanse().PathAppDir);
        jFileChooser.showOpenDialog(this);
        File file=jFileChooser.getSelectedFile();
        onFileSelected(file);
    }
    enum FileType{BMP,BAR}
    FileType curFileType;
    private void onFileSelected(File file){
        System.out.println(file);
        toRead=file;
        curFileType =(file.getAbsolutePath().contains("bar"))?FileType.BAR:FileType.BMP;
        String filePath=toRead.getAbsolutePath();
        String newFile=filePath.substring(0,filePath.length()-4)+(curFileType==FileType.BMP?".bar":"Res.bmp");
        toSave=new File(newFile);
        try {
            toSave.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(curFileType==FileType.BMP){
            try {
                imageReaded=ImageIO.read(toRead);
                setLabelImage(labelImage1,imageReaded);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void onStartButton(){
        if(toRead==null)
            onSelectButton();

        if(curFileType==FileType.BMP)
            convertorDefault.FromBmpToBar(ConvertorDefault.Computing.MultiThreads);
        else
            convertorDefault.FromBarToBmp(ConvertorDefault.Computing.MultiThreads);
    }

    private void setLabelImage(JLabel label, BufferedImage image) {
        ImageIcon imageIcon = new ImageIcon(image);
        labelInfo.setText(imageIcon.getIconWidth() + "x" + imageIcon.getIconHeight());
        Image image1 = imageIcon.getImage().getScaledInstance(label.getWidth(),label.getHeight(), Image.SCALE_DEFAULT);
        label.setIcon(new ImageIcon(image1));
    }
    public static void main(String[] a){
        new MainPage();
    }
}

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
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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


    public MainPage() throws HeadlessException {
        setContentPane(mainPanel);
        setVisible(true);
        setSize(750,500);
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
                ModuleFile file=new ModuleFile(toSave);
                file.write(vector,flagForm.getFlag());
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
                try {
                    BufferedImage image=ImageIO.read(toRead);
                    MyBufferedImage myBufferedImage= BuffImConvertor.getInstance().convert(image);
                    Flag flag=flagForm.getFlag();
                    Size unit=new Size((int)spinnerUnitWidth.getValue(),(int)spinnerUnitHeight.getValue());
                    Size base=new Size((int)spinnerBaseWidth.getValue(),(int)spinnerBaseHeight.getValue());
                    Size imageSize=new Size(image.getWidth(),image.getHeight());
                    Parameters parameters=new Parameters(flag,imageSize,unit,base);
                    return new Pair<>(myBufferedImage,parameters);
                } catch (IOException e) {
                    labelInfo.setText(e.toString());
                }
                return null;
            }

            @NotNull
            @Override
            public ByteVectorContainer getByteVectorContainer() {
                ModuleFile moduleFile=new ModuleFile(toRead);
                return moduleFile.read();
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

    }


    private void setLabelImage(JLabel label, BufferedImage image) {
        ImageIcon imageIcon = new ImageIcon(image);
        labelInfo.setText(labelInfo.getText() + "\n " + imageIcon.getIconWidth() + "x" + imageIcon.getIconHeight());
        Image image1 = imageIcon.getImage().getScaledInstance(600, 600, Image.SCALE_DEFAULT);
        label.setIcon(new ImageIcon(image1));

    }
    public static void main(String[] a){
        new MainPage();
    }
}

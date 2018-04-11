import ImageCompression.Constants.Cosine;
import ImageCompression.Containers.Flag;
import ImageCompression.Containers.Size;
import ImageCompression.Containers.TripleDataOpcMatrix;
import ImageCompression.Containers.TripleShortMatrix;
import ImageCompression.Convertor.ConvertorDefault;
import ImageCompression.Convertor.Implementations.AbsDaoDesktop;
import ImageCompression.Parameters;
import ImageCompression.ProcessingModules.ModuleOPC.AbsModuleOPC;
import ImageCompression.ProcessingModules.ModuleOPC.StegoEncrWithOPC;
import kotlin.Unit;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainForm extends JFrame{

    private JPanel mainPanel;
    private JButton bSelect;
    private JLabel image;
    private JButton bExe;
    private JLabel lInfo;
    private JButton bFlag;
    private JProgressBar progressBar1;
    private JSlider slider1;
    private JLabel lSliderVal;
    private JRadioButton isMultiThread;
    private JPasswordField passwordField1;
    private JSpinner spinnerW;
    private JSpinner spinnerH;
    private JTextField messageField;

    private Parameters parameters = Parameters.getInstanse();
    private Flag flag=new Flag("0");
    private File file;
    private ConvertorDefault convertor;
    private String myType=".bar";

    public MainForm() throws HeadlessException {
        setVisible(true);
        setContentPane(mainPanel);
        setSize(700,600);
        setMinimumSize(new Dimension(800,400));
//        setResizable(false);
//        setLocation(new Point(200,100));

        Init();
        setListeners();
        slider1.setValue(2);
    }
    private void Init(){
        File dir=new File(parameters.PathAppDir);
        dir.mkdir();

        flag.setChecked(Flag.Parameter.OneFile,true);
        flag.setChecked(Flag.Parameter.DC,true);

        BufferedImage myPicture = new BufferedImage(1,1,BufferedImage.TYPE_4BYTE_ABGR);
        ImageIcon imageIcon=new ImageIcon(myPicture);
        Image image1=imageIcon.getImage().getScaledInstance(600,600,Image.SCALE_DEFAULT);
        image.setIcon(new ImageIcon(image1));

        spinnerH.setValue(1);
        spinnerW.setValue(1);

        new Thread(()->{
            Cosine.getCos(0,0);
        }).start();

        ConvertorDefault.IDao dao=new AbsDaoDesktop() {
            @NotNull
            @Override
            public File getFileOriginal() {
                return file;
            }

            @NotNull
            @Override
            public File getFileTarget() {
                return file;
            }

            @NotNull
            @Override
            public Size getSameBase() {
                return new Size((int)spinnerW.getValue(),(int)spinnerH.getValue());
            }

            @NotNull
            @Override
            public Flag getFlag() {
                return flag;
            }
        };
        ConvertorDefault.IFactory factory=new ConvertorDefault.IFactory() {
            @NotNull
            @Override
            public AbsModuleOPC getModuleOPC(@NotNull TripleShortMatrix tripleShortMatrix, @NotNull Flag flag) {
                int w=(int)spinnerW.getValue();
                int h=(int)spinnerH.getValue();
                String pass=String.valueOf(passwordField1.getPassword());
                String mess=messageField.getText();
                return new StegoEncrWithOPC(tripleShortMatrix,flag,w,h,mess,pass);
            }

            @NotNull
            @Override
            public AbsModuleOPC getModuleOPC(@NotNull TripleDataOpcMatrix tripleDataOpcMatrix, @NotNull Flag flag) {
                int w=(int)spinnerW.getValue();
                int h=(int)spinnerH.getValue();
                String pass=String.valueOf(passwordField1.getPassword());
                StegoEncrWithOPC mod=new StegoEncrWithOPC(tripleDataOpcMatrix,flag,w,h,pass);
                mod.setOnMessageReadedListener((String s)-> {
                    messageField.setText(s);
                    return Unit.INSTANCE;
                });
                return mod;
            }
        };
        convertor=new ConvertorDefault(dao,factory);
    }
    private void setListeners(){
        bSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser=new JFileChooser();
                jFileChooser.setCurrentDirectory(new File(parameters.PathAppDir));
                int res=jFileChooser.showDialog(null,"Choose Image");
                if(res==JFileChooser.APPROVE_OPTION){
                    file=jFileChooser.getSelectedFile();
                    onFileSelected(file);
                }
            }
        });
        bFlag.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FlagForm flagForm=new FlagForm(flag);
                flagForm.setOnChengeListener(e1 -> {
                    slider1.setValue(1);
                    flag=flagForm.getFlag();
                });
            }
        });
        bExe.addActionListener(e -> {
            String name=file.getName();
            if(name.contains(".bmp")||name.contains(".BMP")||name.contains(".jpg")) {
                try {
                    processImage(file);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }else if(name.contains(myType)){
                try {
                    processBar(file);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        convertor.setProgressListener((integer, s) -> {
            SwingUtilities.invokeLater(() -> {
                progressBar1.setValue(integer);
                progressBar1.setString(s);
            });
            return null;
        });
        convertor.setOnImageReadyListener(bufferedImage ->{
            new Thread(()->{
                SwingUtilities.invokeLater(() -> {
                    ImageIcon imageIcon = new ImageIcon(bufferedImage);
                    lInfo.setText("convertorDesktop:" + " \n " + imageIcon.getIconWidth() + "x" + imageIcon.getIconHeight());
                    Image image1 = imageIcon.getImage().getScaledInstance(600, 600, Image.SCALE_DEFAULT);
                    image.setIcon(new ImageIcon(image1));
                });
            }).start();
            return null;
        } );
        slider1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int v=slider1.getValue();
                sliderListener(v);
            }
        });
        passwordField1.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
//                convertorDesktop.setPassword(passwordField1.getText());
            }
        });
        spinnerW.addChangeListener(e -> {
            int val=(int)spinnerW.getValue();
            if(val<1)
                spinnerW.setValue(1);
//            convertorDesktop.setGlobalBaseW(val);
        });
        spinnerH.addChangeListener(e -> {
            int val=(int)spinnerH.getValue();
            if(val<1)
                spinnerH.setValue(1);
//            convertorDesktop.setGlobalBaseH(val);
        });
    }
    private void onFileSelected(File file){
        String name=file.getName();
        lInfo.setText("Name: "+name+" ,Size: "+file.length()/1024+"kB ");

        if(name.contains(".bmp")||name.contains(".BMP")||name.contains(".jpg")) {
            setLableImage(file);
            bExe.setText("Convert to BAR");
        }else if(name.contains(myType)){
            bExe.setText("Convert to BMP");
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
        ConvertorDefault.Computing computing=(isMultiThread.isSelected())? ConvertorDefault.Computing.MultiThreads: ConvertorDefault.Computing.OneThread;
//        ConvertorDesktop.Info info=new ConvertorDesktop.Info(flag,
//                String.valueOf(passwordField1.getPassword()),null,1,1 );
        new Thread(()-> convertor.FromBmpToBar(computing)).start();
    }
    private void processBar(File file)throws Exception{
        ConvertorDefault.Computing computing=(isMultiThread.isSelected())? ConvertorDefault.Computing.MultiThreads: ConvertorDefault.Computing.OneThread;
        String pass=String.valueOf(passwordField1.getPassword());
        new Thread(()-> convertor.FromBarToBmp(computing)).start();
    }

    private void sliderListener(int val){
        if(val==0){
            lSliderVal.setText("min compression");
            flag=new Flag((short)0);
            flag.setChecked(Flag.Parameter.LongCode,true);
            flag.setChecked(Flag.Parameter.OneFile,true);
        }else if(val==1) {
            lSliderVal.setText("Custom");
        } else if(val==2){
            lSliderVal.setText("max compression");
            flag=new Flag((short)0);
            flag.setChecked(Flag.Parameter.OneFile,true);
            flag.setChecked(Flag.Parameter.DC,true);
            flag.setQuantization(Flag.QuantizationState.First);
            flag.setChecked(Flag.Parameter.Enlargement,true);
//            flag.setAlignment(true);
            flag.setChecked(Flag.Parameter.CompressionUtils,true);
        }
    }

    private String getFileWithOutType(String path){
        return path.substring(0,path.length()-4);
    }

    public static void main(String[] arg){
        new MainForm();
    }

}

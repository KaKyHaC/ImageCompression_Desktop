import ImageCompression.Containers.Matrix;
import ImageCompression.Objects.*;

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
    private JButton bFlag;
    private JProgressBar progressBar1;

    private Parameters parameters = Parameters.getInstanse();
    private ApplicationOPC applicationOPC = ApplicationOPC.getInstance();
    private Flag flag=new Flag("0");
    private File file;
    private Convertor convertor=new Convertor();

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

        flag.setOneFile(true);
        flag.setLongCode(true);
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
            }else if(name.contains(".bar")){
                try {
                    processBar(file);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        convertor.addPublishListener(integer -> {
            progressBar1.setValue(integer);
            return null;
        });
    }
    private void onFileSelected(File file){
        String name=file.getName();
        lInfo.setText("Name: "+name+" ,Size: "+file.length()/1024+"kB ");

        if(name.contains(".bmp")||name.contains(".BMP")||name.contains(".jpg")) {
            setLableImage(file);
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
        progressBar1.setValue(0);
        File toSave=new File(parameters.PathAppDir+"/default");
        JFileChooser jFileChooser=new JFileChooser(parameters.PathAppDir);
        if(jFileChooser.showSaveDialog(null)==JFileChooser.APPROVE_OPTION){
            toSave=jFileChooser.getSelectedFile();
        }
        BufferedImage  bufferedImage=ImageIO.read(file);
        MyBufferedImage myBufferedImage=new MyBufferedImage(bufferedImage,flag);
        Matrix matrix=myBufferedImage.getYCbCrMatrix();

        applicationOPC.FromMatrixToFile(val -> {
            System.out.println(val);
            progressBar1.setValue(val);
        },matrix,getFileWithOutType(toSave.getAbsolutePath()));
        progressBar1.setValue(100);
    }
    private void processBar(File file)throws Exception{
        Matrix matrix=applicationOPC.FromFileToMatrix(val ->{
            System.out.println(val);
            progressBar1.setValue(val);
        },getFileWithOutType(file.getAbsolutePath() ));
        MyBufferedImage myBufferedImage=new MyBufferedImage(matrix);
        BufferedImage bufferedImage=myBufferedImage.getBufferedImage();
        image.setIcon(new ImageIcon(bufferedImage.getScaledInstance(600,600,Image.SCALE_DEFAULT)));
    }

    private String getFileWithOutType(String path){
        return path.substring(0,path.length()-4);
    }

    public static void main(String[] arg){
        new MainForm();
    }

}

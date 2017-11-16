import ImageCompression.Objects.Flag;
import javafx.scene.Parent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.Vector;

public class FlagForm extends JFrame{
    private Flag flag;
    private final JPanel panel = new JPanel(new GridLayout(10,1));
    private Vector<JCheckBox> jCheckBoxArray=new Vector<>();

    public FlagForm()throws HeadlessException {
        flag=new Flag("0");
        Factory();
    }
    public FlagForm(Flag flag)throws HeadlessException{
        this.flag=flag;
        Factory();
    }
    private void Factory()throws HeadlessException{
        getContentPane().add(panel, BorderLayout.CENTER);
        setVisible(true);
        setSize(800,800);
        setLocation(new Point(200,100));

        Init();
    }
    private void addView(String name,boolean val){
        JCheckBox jCheckBox=new JCheckBox(name,val);
        jCheckBoxArray.add(jCheckBox);
        panel.add(jCheckBox,BorderLayout.SOUTH);
    }

    public Flag getFlag() {
        int i=0;
        flag.setDC(jCheckBoxArray.get(i++).isSelected());
        flag.setEnlargement(jCheckBoxArray.get(i++).isSelected());
        flag.setLongCode(jCheckBoxArray.get(i++).isSelected());
        flag.setOneFile(jCheckBoxArray.get(i++).isSelected());
        flag.setGlobalBase(jCheckBoxArray.get(i++).isSelected());
        flag.setAlignment(jCheckBoxArray.get(i++).isSelected());
        flag.setPassword(jCheckBoxArray.get(i++).isSelected());
        flag.setSteganography(jCheckBoxArray.get(i++).isSelected());
        return flag;
    }

    private void Init(){
        addView("isDC",flag.isDC());
        addView("isEnlargement",flag.isEnlargement());
        addView("isLongCode",flag.isLongCode());
        addView("isOneFile",flag.isOneFile());
        addView("isGlobalBase",flag.isGlobalBase());
        addView("isAlignment",flag.isAlignment());
        addView("isPassword",flag.isPassword());
        addView("isSteganography",flag.isSteganography());

        panel.revalidate();
        panel.repaint();
        pack();
    }

    public void setOnChengeListener(ActionListener actionListener){
        for(JCheckBox cb : jCheckBoxArray){
            cb.addActionListener(actionListener);
        }
    }
    public static void main(String[] arg){
        new FlagForm();
    }
}

package WindowsApp;

import ImageCompressionLib.Containers.Type.Flag;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Vector;

public class FlagForm{
    private Flag flag;
    private final JPanel panel = new JPanel(new GridLayout(5,2));
    private Vector<JCheckBox> jCheckBoxArray=new Vector<>();

    public FlagForm(Flag flag,JPanel jPanel)throws HeadlessException{
        this.flag=flag;
        Factory(jPanel);
    }
    private void Factory(JPanel jPanel)throws HeadlessException{
        Init();

        jPanel.setLayout(new GridBagLayout());
        jPanel.add(panel);
//        jPanel.add(panel, BorderLayout.CENTER);
        jPanel.setVisible(true);
//        setLocation(new Point(200,100));
    }
    private void addView(String name,boolean val){
        JCheckBox jCheckBox=new JCheckBox(name,val);
        jCheckBoxArray.add(jCheckBox);
        panel.add(jCheckBox,BorderLayout.SOUTH);
    }

    public Flag getFlag() {
        int i=0;
        flag.setChecked(Flag.Parameter.DC,jCheckBoxArray.get(i++).isSelected());
        flag.setChecked(Flag.Parameter.Enlargement,jCheckBoxArray.get(i++).isSelected());
        flag.setChecked(Flag.Parameter.LongCode,jCheckBoxArray.get(i++).isSelected());
        flag.setChecked(Flag.Parameter.OneFile,jCheckBoxArray.get(i++).isSelected());
        flag.setChecked(Flag.Parameter.GlobalBase,jCheckBoxArray.get(i++).isSelected());
        flag.setChecked(Flag.Parameter.Alignment,jCheckBoxArray.get(i++).isSelected());
        flag.setChecked(Flag.Parameter.Password,jCheckBoxArray.get(i++).isSelected());
//        flag.setChecked(Flag.Parameter.Steganography,jCheckBoxArray.get(i++).isSelected());
        flag.setChecked(Flag.Parameter.CompressionUtils,jCheckBoxArray.get(i++).isSelected());

        Flag.QuantizationState qs=jCheckBoxArray.get(i++).isSelected()? Flag.QuantizationState.First: Flag.QuantizationState.Without;
        flag.setQuantization(qs);

//        Flag.Encryption es=jCheckBoxArray.get(i++).isSelected()? Flag.Encryption.First: Flag.Encryption.Without;
//        flag.setEncryption(es);
        return flag;
    }

    private void Init(){
        addView("isDC",flag.isChecked(Flag.Parameter.DC));
        addView("isEnlargement",flag.isChecked(Flag.Parameter.Enlargement));
        addView("isLongCode",flag.isChecked(Flag.Parameter.LongCode));
        addView("isOneFile",flag.isChecked(Flag.Parameter.OneFile));
        addView("isGlobalBase",flag.isChecked(Flag.Parameter.GlobalBase));
        addView("isAlignment",flag.isChecked(Flag.Parameter.Alignment));
        addView("isPassword",flag.isChecked(Flag.Parameter.Password));
//        addView("isSteganography",flag.isChecked(Flag.Parameter.Steganography));
        addView("isCompressionUtils",flag.isChecked(Flag.Parameter.CompressionUtils));
        addView("isQuantization",flag.getQuantization()== Flag.QuantizationState.First);
//        addView("isEncryption",flag.getEncryption()== Flag.Encryption.First);

        panel.revalidate();
        panel.repaint();
//        pack();
    }

    public void setOnChengeListener(ActionListener actionListener){
        for(JCheckBox cb : jCheckBoxArray){
            cb.addActionListener(actionListener);
        }
    }
//    public static void main(String[] arg){
//        new WindowsApp.FlagForm();
//    }
}

import ImageCompression.Containers.Flag;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Vector;

public class FlagForm extends JFrame{
    private Flag flag;
    private final JPanel panel = new JPanel(new GridLayout(10,1));
    private Vector<JCheckBox> jCheckBoxArray=new Vector<>();

    public FlagForm()throws HeadlessException {
        flag=new Flag();
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
        flag.setChecked(Flag.Parameter.DC,jCheckBoxArray.get(i++).isSelected());
        flag.setChecked(Flag.Parameter.Enlargement,jCheckBoxArray.get(i++).isSelected());
        flag.setChecked(Flag.Parameter.LongCode,jCheckBoxArray.get(i++).isSelected());
        flag.setChecked(Flag.Parameter.OneFile,jCheckBoxArray.get(i++).isSelected());
        flag.setChecked(Flag.Parameter.GlobalBase,jCheckBoxArray.get(i++).isSelected());
        flag.setChecked(Flag.Parameter.Alignment,jCheckBoxArray.get(i++).isSelected());
        flag.setChecked(Flag.Parameter.Password,jCheckBoxArray.get(i++).isSelected());
        flag.setChecked(Flag.Parameter.Steganography,jCheckBoxArray.get(i++).isSelected());
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
        addView("isSteganography",flag.isChecked(Flag.Parameter.Steganography));
        addView("isCompressionUtils",flag.isChecked(Flag.Parameter.CompressionUtils));
        addView("isQuantization",flag.getQuantization()== Flag.QuantizationState.First);
//        addView("isEncryption",flag.getEncryption()== Flag.Encryption.First);

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

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
        for(Flag.Parameter p : Flag.Parameter.values()){
            flag.setChecked(p,jCheckBoxArray.get(i++).isSelected());
        }

        return flag;
    }

    private void Init(){
        for(Flag.Parameter p : Flag.Parameter.values()){
            addView(p.name(),flag.isChecked(p));
        }

        panel.revalidate();
        panel.repaint();
    }

    public void setOnChengeListener(ActionListener actionListener){
        for(JCheckBox cb : jCheckBoxArray){
            cb.addActionListener(actionListener);
        }
    }
    public void setFlag(Flag flag){
        int i=0;
        for(Flag.Parameter p : Flag.Parameter.values()){
            jCheckBoxArray.get(i++).setSelected(flag.isChecked(p));
        }
    }
//    public static void main(String[] arg){
//        new WindowsApp.FlagForm();
//    }
}

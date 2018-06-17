package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Set;

public class GUIMain extends JDialog {
    private JPanel contentPane;
    private JButton DOSButton;
    private JButton HIJACKButton;
    private JButton LANDButton;
    private JButton BALLONButton;
    private JButton KILLButton;
    private JList list1;
    private JTextPane textPane1;
    private JTextPane textPane2;
    DefaultListModel<String> model;




    public GUIMain() {

        setContentPane(contentPane);
        setModal(true);
        //this.setDefaultCloseOperation(1);

        textPane1.setEditable(false);
        textPane2.setEditable(false);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);


        DOSButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

            }
        });
        HIJACKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

            }
        });
        LANDButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

            }
        });
        KILLButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

            }
        });
        BALLONButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

            }
        });

        


    }


    public void handle(Set<String> set){
        model.removeAllElements();
        for(String s : set){
            model.addElement(s);
        }
    }


    public static void main(String[] args) {
        GUIMain dialog = new GUIMain();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void createUIComponents() {
        model = new DefaultListModel<>();
        list1 = new JList(model);
    }
}

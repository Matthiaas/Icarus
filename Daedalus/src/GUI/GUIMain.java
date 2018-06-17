package GUI;

import networking.Procedures;
import networking.WIFIHandler;
import networking.WIFIMonitor;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Set;

public class GUIMain extends JDialog implements WIFIHandler {
    private JPanel contentPane;
    private JButton DOSButton;
    private JButton HIJACKButton;
    private JButton LANDButton;
    private JButton BALLONButton;
    private JButton KILLButton;
    private JList list1;
    private JTextPane textPane1;
    private JTextPane textPane2;


    private String selectedSSid;
    DefaultListModel<String> model;

    private boolean enabled = false;


    public GUIMain() {

        setContentPane(contentPane);
        setModal(true);
        textPane1.setEditable(false);
        textPane2.setEditable(false);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        WIFIMonitor.register(this);

        HIJACKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(!enabled)
                {
                    LANDButton.setEnabled(true);
                    KILLButton.setEnabled(true);
                    BALLONButton.setEnabled(true);
                }else{
                    LANDButton.setEnabled(false);
                    KILLButton.setEnabled(false);
                    BALLONButton.setEnabled(false);
                }

                enabled = !enabled;
            }
        });
        LANDButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Procedures.land(selectedSSid);
            }
        });
        KILLButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Procedures.kill(selectedSSid);
            }
        });
        BALLONButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Procedures.balloon(selectedSSid);
            }
        });


        list1.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                if (!arg0.getValueIsAdjusting()) {
                    String info = Procedures.info(list1.getSelectedValue().toString());
                    textPane2.setText(info);
                }
            }
        });


    }


    public void handle(Set<String> set){
        model.removeAllElements();
        for(String s : set){
            model.addElement(s);
        }
    }




    private void createUIComponents() {
        model = new DefaultListModel<>();
        list1 = new JList(model);
    }
}

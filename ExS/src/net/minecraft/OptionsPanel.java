package net.minecraft;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class OptionsPanel extends JDialog {

    private static final long serialVersionUID = 1L;
    public static boolean forceUpdate = false;
    private static Preferences options = Util.getOptions();
    public static boolean doNotDeleteMods = Boolean.parseBoolean(options.get("leavemods","false"));
    public static boolean doNotDeleteConfig = Boolean.parseBoolean(options.get("leaveconf","false"));
    public static int memory = Integer.parseInt(options.get("maxmem","512"));

    public OptionsPanel(Frame parent) {
        super(parent);
        setSize(800, 600);
        setModal(true);
        try {
            final TransparentCheckbox forceCheckBox = new TransparentCheckbox("Принудительно обновить клиент");
            final TransparentCheckbox doNotDeleteModsCheckBox = new TransparentCheckbox("Не удалять папку mods при обновлении");
            final TransparentCheckbox doNotDeleteConfigCheckBox = new TransparentCheckbox("Не удалять настройки модов при обновлении");
            final JButton repairBtn = new JButton("Восстановить клиент");

            forceCheckBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    OptionsPanel.forceUpdate = forceCheckBox.isSelected();
                }
            });
            forceCheckBox.setForeground(Color.BLACK);
            doNotDeleteModsCheckBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    OptionsPanel.doNotDeleteMods = doNotDeleteModsCheckBox.isSelected();
                    OptionsPanel.options.put("leavemods", Boolean.toString(OptionsPanel.doNotDeleteMods));
                }
            });
            doNotDeleteModsCheckBox.setForeground(Color.BLACK);
            doNotDeleteModsCheckBox.setSelected(doNotDeleteMods);
            doNotDeleteConfigCheckBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    OptionsPanel.doNotDeleteConfig = doNotDeleteConfigCheckBox.isSelected();
                    OptionsPanel.options.put("leaveconf", Boolean.toString(OptionsPanel.doNotDeleteConfig));
                }
            });
            doNotDeleteConfigCheckBox.setForeground(Color.BLACK);
            doNotDeleteConfigCheckBox.setSelected(doNotDeleteConfig);
            repairBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    try {
                        OptionsPanel.forceUpdate = true;
                        Util.deleteRecursive(Util.getWorkingDirectory());
                        Util.getOptions();
                        repairBtn.setEnabled(false);
                        repairBtn.setText("Все файлы клиента удалены!");
                        LoginForm.writeUsername();
                    } catch (Exception ex) {
                    };

                }
            });
            repairBtn.setOpaque(false);
            repairBtn.setToolTipText("Удалить все файлы клиента и скачать их заново");

            JPanel panel = new JPanel(new BorderLayout());
            JLabel label = new JLabel("Настройки", 0);
            label.setBorder(new EmptyBorder(0, 0, 16, 0));
            label.setFont(new Font("Default", 1, 16));
            panel.add(label, "North");
            String osbits = System.getProperty("os.arch").toLowerCase();
            int max = 1800;
            if (osbits == "x64") {
                max = 4000;
            }
            final JSlider maxmemSlider = new JSlider(300, max, Integer.parseInt(options.get("maxmem","512")));

            maxmemSlider.setPaintTicks(true);
            maxmemSlider.setPaintLabels(true);
            maxmemSlider.setMajorTickSpacing(400);
            maxmemSlider.setMinorTickSpacing(100);
            maxmemSlider.setLabelTable(maxmemSlider.createStandardLabels(400));
            final JLabel maxmemLabel = new JLabel("<html><center>Выделенная память: <i>" + options.get("maxmem","512") + " МБ</i><br />Внимание!<br />Не задавайте памяти больше, чем доступно программам!<br /><br /></center></html>", 0);
            maxmemSlider.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    JSlider source = (JSlider) e.getSource();
                    int cval = maxmemSlider.getValue();
                    if (cval < 2048) {
                        maxmemLabel.setText("<html><center>Выделенная память: <i>" + cval + " МБ</i><br />(требуется перезапуск лаунчера)<br />Внимание!<br />Не задавайте памяти больше, чем доступно программам!<br /></center></html>");
                    } else {
                        maxmemLabel.setText("<html><center>Выделенная память: <i><font color='red'>" + cval + " МБ</font></i><br />(требуется перезапуск лаунчера)<br />Внимание!<br />Не задавайте памяти больше, чем доступно программам!<br /></center></html>");
                    }
                    if (!source.getValueIsAdjusting()) {
                        OptionsPanel.options.put("maxmem", String.valueOf(cval));
                    }
                }
            });
            JPanel optionsPanel = new JPanel(new BorderLayout());
            JPanel labelPanel = new JPanel(new GridLayout(0, 1));

            optionsPanel.add(labelPanel, "East");

            labelPanel.add(forceCheckBox);
            labelPanel.add(doNotDeleteModsCheckBox);
            labelPanel.add(doNotDeleteConfigCheckBox);
            labelPanel.add(repairBtn);
            labelPanel.add(maxmemSlider);
            labelPanel.add(maxmemLabel);

            panel.add(optionsPanel, "Center");

            JPanel buttonsPanel = new JPanel(new BorderLayout());
            buttonsPanel.add(new JPanel(), "Center");
            JButton doneButton = new JButton("Готово");
            doneButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    //Util.saveOptions(OptionsPanel.options);
                    OptionsPanel.this.setVisible(false);
                }
            });
            buttonsPanel.add(doneButton, "East");
            buttonsPanel.setBorder(new EmptyBorder(16, 0, 0, 0));

            panel.add(buttonsPanel, "South");

            add(panel);
            panel.setBorder(new EmptyBorder(16, 24, 24, 24));
        } catch (Exception e) {
            //Util.deleteOptionsFile();
        }
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }
}
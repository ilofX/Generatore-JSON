import org.json.JSONObject;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class MainFrame extends JFrame {

    private JTextField inField, outField;
    private JTextArea logArea;
    private JScrollPane scrollPanel;
    private JLabel inLabel, outLabel, logLabel;
    private JFileChooser fileSelector;
    private JProgressBar progressBar;
    private JPanel panel;
    private JButton buttonStart;
    private Reader read;
    private Writer write;
    private Semaphore sam;
    private JSONObject object;
    private Executor exec;

    MainFrame() {
        super("JSON Generator");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("JSON Generator");
        this.exec = Executors.newSingleThreadExecutor();
        this.sam = new Semaphore(1);
        this.initComponents();
        this.addListener();
        this.initFrame();
    }

    public static void main(String[] args) {
        MainFrame mf = new MainFrame();
    }

    private void initComponents() {
        this.panel = new JPanel();

        this.inLabel = new JLabel("File di Input");
        this.outLabel = new JLabel("File di Output");
        this.logLabel = new JLabel("Log");

        this.inField = new JTextField();
        this.outField = new JTextField();

        this.logArea = new JTextArea();
        this.scrollPanel = new JScrollPane(this.logArea);
        this.progressBar = new JProgressBar();

        this.buttonStart = new JButton("Start");

        this.fileSelector = new JFileChooser();

        this.panel.setLayout(null);
        this.setResizable(false);

        this.panel.setSize(640, 420);
        this.panel.setMinimumSize(new Dimension(640, 420));
        this.setMinimumSize(new Dimension(640, 420));

        this.inLabel.setBounds(20, 10, 150, 20);
        this.inField.setBounds(170, 10, 430, 20);
        this.inField.setEditable(false);

        this.outLabel.setBounds(20, 40, 150, 20);
        this.outField.setBounds(170, 40, 430, 20);
        this.outField.setEditable(false);
        this.outField.setEditable(false);

        this.progressBar.setBounds(20, 80, 580, 20);

        this.logLabel.setBounds(20, 120, 80, 20);
        this.scrollPanel.setBounds(20, 150, 580, 150);

        this.buttonStart.setBounds(20, 320, 100, 40);
    }

    private void addListener() {
        this.inField.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getComponent().equals(inField))
                    fileSelector.setFileFilter(new FileNameExtensionFilter("Txt File", "txt"));
                fileSelector.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileSelector.showOpenDialog(panel);
                inField.setText(fileSelector.getSelectedFile().getPath());
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        this.outField.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getComponent().equals(outField))
                    fileSelector.setFileFilter(new FileNameExtensionFilter("JSON File", "json"));
                fileSelector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileSelector.showSaveDialog(panel);
                outField.setText(fileSelector.getSelectedFile().getPath() + "/ConvertedFile.json");
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        this.buttonStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inField.setFocusable(false);
                outField.setFocusable(false);
                progressBar.setIndeterminate(true);
                logArea.setText("");
                initParser();
                parse();
            }
        });
    }

    private void initFrame() {
        this.setContentPane(this.panel);
        this.panel.add(this.inLabel);
        this.panel.add(this.inField);
        this.panel.add(this.outLabel);
        this.panel.add(this.outField);
        this.panel.add(this.progressBar);
        this.panel.add(this.logLabel);
        this.panel.add(this.scrollPanel);
        this.panel.add(this.buttonStart);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    void samAquire() throws InterruptedException {
        this.sam.acquire();
    }

    void samRelease() {
        this.sam.release();
    }

    JSONObject getObject() {
        return this.object;
    }

    void setObject(JSONObject obj) {
        this.object = obj;
    }

    private void initParser() {
        if (this.inField.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Selezionare un file di input", "Avviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (this.outField.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Selezionare un file di output", "Avviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            this.read = new Reader(this.inField.getText(), this);
        } catch (FileNotFoundException ex) {
            this.logArea.setText(this.logArea.getText() + "\nInvalid input file!\n");
        }

        try {
            this.write = new Writer(this.outField.getText(), this);
        } catch (IOException ex) {
            this.logArea.setText(this.logArea.getText() + "\nInvalid output file!\n");
        }

        this.logArea.setText(this.logArea.getText() + "Parser Ready\n");
    }

    private void parse() {
        this.exec.execute(this.read);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.exec.execute(this.write);
    }

    void terminate() {
        this.progressBar.setIndeterminate(false);
    }

    void logEvent(String message) {
        this.logArea.setText(this.logArea.getText() + message + "\n");
    }

}
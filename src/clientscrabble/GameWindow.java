package clientscrabble;

import static clientscrabble.GridButton.NOT_ASSIGNED;
import dataObjects.TurnData;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import remote.IClientCallback;
import remote.IGameServer;

/**
 *
 * @author mgoudarzilist
 */
public class GameWindow extends javax.swing.JFrame {

    private ArrayList<String> listToJoin;
    int currentPIndex;
    private DefaultListModel<CheckListItem> model;
    int i = 0;
    int j = 0;
    public static char letterTemp = ' ';

    public static ArrayList<ArrayList<GridButton>> gridButtonList;
    public static java.awt.Color DEFAULT_BUTTON_COLOR;
    public static int boardSize;
    GameMainController controller;
    GridButton button = new GridButton();
    ArrayList<String> receivedWords = new ArrayList<String>();
    private static IClientCallback client;

    public int word1X1coordinate = 0;
    public int word1X2coordinate = 0;
    public int word1Y1coordinate = 0;
    public int word1Y2coordinate = 0;
    public int word2X2coordinate = 0;
    public int word2X1coordinate = 0;
    public int word2Y2coordinate = 0;
    public int word2Y1coordinate = 0;

    // private static IClientCallback client;
    // private static IGameServer gameServer;
    // TurnData turnData = new TurnData();
    public GameWindow(int n) {
        this.boardSize = n;
        gridButtonList = new ArrayList<>();
        initComponents();
        button.setLetter(' ');
        GridLayoutButtonCreator(boardSize);
        this.setVisible(true);
        player1TurnLabel.setEnabled(false);
        player2TurnLabel.setEnabled(false);
        player3TurnLabel.setEnabled(false);
        player4TurnLabel.setEnabled(false);
        mainMenuStartButton.setEnabled(false);
        jButtonAskGame.setEnabled(false);
        chatBoxSendButton.setEnabled(false);

        mainMenuPassButton.setEnabled(false);

        player1TurnLabel.setVisible(false);
        player2TurnLabel.setVisible(false);
        player3TurnLabel.setVisible(false);
        player4TurnLabel.setVisible(false);

        player1NameTextfield.setVisible(false);
        player2NameTextfield.setVisible(false);
        player3NameTextfield.setVisible(false);
        player4NameTextfield.setVisible(false);

        Player1scoreTextfield.setVisible(false);
        Player2scoreTextfield.setVisible(false);
        Player3scoreTextfield.setVisible(false);
        Player4scoreTextfield.setVisible(false);

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                // ((Client)client).Quit();
                controller.quit();
                System.out.println("Clicked exit button");
                controller.exit();
            }
        });

        controller = new GameMainController(this);
    }

    public GameWindow() {

        // playerTurnLabelHandler(player);
    }

    public GameWindow(boolean f) {
        // this.setVisible(false);
    }

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
        // (optional) ">
        /*
		 * If Nimbus (introduced in Java SE 6) is not available, stay with the default
		 * look and feel. For details see
		 * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        // </editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GameWindow(boardSize).setVisible(true);
            }
        });

    }
//    public void UpdateQueue(String[] queue) {
//        String queueText = String.join(System.lineSeparator(), queue);
//        System.out.println(queueText);
//        chatBox1.setText(queueText);
//    }

    public void UpdateQueue(String[] queue) throws RemoteException {
        String queueText = String.join(System.lineSeparator(), queue);
        System.out.println(queueText);
        // THere is a problem. WHen this is called the chatBox1 is null. Need to find ot
        // how to handle this.
        this.model = new DefaultListModel<>();
        JList list = new JList();
        currentPIndex = 0;
        for (int i = 0; i < queue.length; i++) {
            CheckListItem item = new CheckListItem(queue[i]);
            if (item.toString().equals(this.controller.getClientName())) {
                item.setSelected(true);
                currentPIndex = i;
            }
            model.addElement(item);
        }
        list.setModel(model);
        list.setCellRenderer(new CheckListRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                JList list = (JList) event.getSource();
                int index = list.locationToIndex(event.getPoint());// Get index of item clicked
                if (index != currentPIndex) {
                    CheckListItem item = (CheckListItem) list.getModel().getElementAt(index);
                    item.setSelected(!item.isSelected()); // Toggle selected state Repaint cell
                    list.repaint(list.getCellBounds(index, index));
                }

            }
        });
        jScrollPaneNamelist.setViewportView(list);
        // chatBox1.setText(queueText);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Controller = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        keyboardButtonA = new javax.swing.JButton();
        keyboardButtonE = new javax.swing.JButton();
        keyboardButtonB = new javax.swing.JButton();
        keyboardButtonC = new javax.swing.JButton();
        keyboardButtonD = new javax.swing.JButton();
        keyboardButtonF = new javax.swing.JButton();
        keyboardButtonG = new javax.swing.JButton();
        keyboardButtonH = new javax.swing.JButton();
        keyboardButtonI = new javax.swing.JButton();
        keyboardButtonJ = new javax.swing.JButton();
        keyboardButtonL = new javax.swing.JButton();
        keyboardButtonK = new javax.swing.JButton();
        keyboardButtonM = new javax.swing.JButton();
        keyboardButtonW = new javax.swing.JButton();
        keyboardButtonN = new javax.swing.JButton();
        keyboardButtonO = new javax.swing.JButton();
        keyboardButtonP = new javax.swing.JButton();
        keyboardButtonQ = new javax.swing.JButton();
        keyboardButtonS = new javax.swing.JButton();
        keyboardButtonX = new javax.swing.JButton();
        keyboardButtonT = new javax.swing.JButton();
        keyboardButtonU = new javax.swing.JButton();
        keyboardButtonV = new javax.swing.JButton();
        keyboardButtonR = new javax.swing.JButton();
        keyboardButtonY = new javax.swing.JButton();
        keyboardButtonZ = new javax.swing.JButton();
        keyboardButtonEndTurn = new javax.swing.JButton();
        keyboardButtonWord = new javax.swing.JButton();
        keyboardLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        mainMenuLoginButton = new javax.swing.JButton();
        mainMenuStartButton = new javax.swing.JButton();
        jButtonAskGame = new javax.swing.JButton();
        jScrollPaneNamelist = new javax.swing.JScrollPane();
        mainMenuPassButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        chatBox = new javax.swing.JTextArea();
        chatBoxSendButton = new javax.swing.JButton();
        chatBoxCleanButton = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        chatBoxLabel = new javax.swing.JLabel();
        mainMenuLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        player1TurnLabel = new javax.swing.JButton();
        player2TurnLabel = new javax.swing.JButton();
        player3TurnLabel = new javax.swing.JButton();
        player4TurnLabel = new javax.swing.JButton();
        player1NameTextfield = new javax.swing.JTextField();
        player2NameTextfield = new javax.swing.JTextField();
        player3NameTextfield = new javax.swing.JTextField();
        player4NameTextfield = new javax.swing.JTextField();
        Player1scoreTextfield = new javax.swing.JTextField();
        Player2scoreTextfield = new javax.swing.JTextField();
        Player3scoreTextfield = new javax.swing.JTextField();
        Player4scoreTextfield = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        GridContainerPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setMinimumSize(new java.awt.Dimension(1050, 960));
        setResizable(false);

        Controller.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), new javax.swing.border.MatteBorder(null)));
        Controller.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        Controller.setPreferredSize(new java.awt.Dimension(616, 151));

        jPanel3.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), new javax.swing.border.MatteBorder(null)));
        jPanel3.setFont(new java.awt.Font("Tahoma", 1, 8)); // NOI18N

        keyboardButtonA.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        keyboardButtonA.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        keyboardButtonA.setIconTextGap(2);
        keyboardButtonA.setLabel("A");
        keyboardButtonA.setName("A"); // NOI18N
        keyboardButtonA.setPreferredSize(new java.awt.Dimension(80, 62));
        keyboardButtonA.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyboardButtonAMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                keyboardButtonAMousePressed(evt);
            }
        });
        keyboardButtonA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonAActionPerformed(evt);
            }
        });

        keyboardButtonE.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        keyboardButtonE.setText("E");
        keyboardButtonE.setPreferredSize(new java.awt.Dimension(80, 62));
        keyboardButtonE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonEActionPerformed(evt);
            }
        });

        keyboardButtonB.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        keyboardButtonB.setText("B");
        keyboardButtonB.setName("B"); // NOI18N
        keyboardButtonB.setPreferredSize(new java.awt.Dimension(80, 62));
        keyboardButtonB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonBActionPerformed(evt);
            }
        });

        keyboardButtonC.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        keyboardButtonC.setText("C");
        keyboardButtonC.setPreferredSize(new java.awt.Dimension(80, 62));
        keyboardButtonC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonCActionPerformed(evt);
            }
        });

        keyboardButtonD.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        keyboardButtonD.setText("D");
        keyboardButtonD.setName("D"); // NOI18N
        keyboardButtonD.setPreferredSize(new java.awt.Dimension(80, 62));
        keyboardButtonD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonDActionPerformed(evt);
            }
        });

        keyboardButtonF.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        keyboardButtonF.setText("F");
        keyboardButtonF.setPreferredSize(new java.awt.Dimension(80, 62));
        keyboardButtonF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonFActionPerformed(evt);
            }
        });

        keyboardButtonG.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        keyboardButtonG.setText("G");
        keyboardButtonG.setPreferredSize(new java.awt.Dimension(80, 62));
        keyboardButtonG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonGActionPerformed(evt);
            }
        });

        keyboardButtonH.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        keyboardButtonH.setText("H");
        keyboardButtonH.setPreferredSize(new java.awt.Dimension(80, 62));
        keyboardButtonH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonHActionPerformed(evt);
            }
        });

        keyboardButtonI.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        keyboardButtonI.setText("I");
        keyboardButtonI.setPreferredSize(new java.awt.Dimension(80, 62));
        keyboardButtonI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonIActionPerformed(evt);
            }
        });

        keyboardButtonJ.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        keyboardButtonJ.setText("J");
        keyboardButtonJ.setPreferredSize(new java.awt.Dimension(80, 62));
        keyboardButtonJ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonJActionPerformed(evt);
            }
        });

        keyboardButtonL.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        keyboardButtonL.setText("L");
        keyboardButtonL.setPreferredSize(new java.awt.Dimension(80, 62));
        keyboardButtonL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonLActionPerformed(evt);
            }
        });

        keyboardButtonK.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        keyboardButtonK.setText("K");
        keyboardButtonK.setPreferredSize(new java.awt.Dimension(80, 62));
        keyboardButtonK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonKActionPerformed(evt);
            }
        });

        keyboardButtonM.setFont(new java.awt.Font("Times New Roman", 1, 10)); // NOI18N
        keyboardButtonM.setText("M");
        keyboardButtonM.setPreferredSize(new java.awt.Dimension(80, 62));
        keyboardButtonM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonMActionPerformed(evt);
            }
        });

        keyboardButtonW.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        keyboardButtonW.setText("W");
        keyboardButtonW.setPreferredSize(new java.awt.Dimension(80, 62));
        keyboardButtonW.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonWActionPerformed(evt);
            }
        });

        keyboardButtonN.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        keyboardButtonN.setText("N");
        keyboardButtonN.setPreferredSize(new java.awt.Dimension(80, 62));
        keyboardButtonN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonNActionPerformed(evt);
            }
        });

        keyboardButtonO.setFont(new java.awt.Font("Times New Roman", 1, 10)); // NOI18N
        keyboardButtonO.setText("O");
        keyboardButtonO.setPreferredSize(new java.awt.Dimension(80, 62));
        keyboardButtonO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonOActionPerformed(evt);
            }
        });

        keyboardButtonP.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        keyboardButtonP.setText("P");
        keyboardButtonP.setPreferredSize(new java.awt.Dimension(80, 62));
        keyboardButtonP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonPActionPerformed(evt);
            }
        });

        keyboardButtonQ.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        keyboardButtonQ.setText("Q");
        keyboardButtonQ.setPreferredSize(new java.awt.Dimension(80, 62));
        keyboardButtonQ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonQActionPerformed(evt);
            }
        });

        keyboardButtonS.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        keyboardButtonS.setText("S");
        keyboardButtonS.setPreferredSize(new java.awt.Dimension(80, 62));
        keyboardButtonS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonSActionPerformed(evt);
            }
        });

        keyboardButtonX.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        keyboardButtonX.setText("X");
        keyboardButtonX.setPreferredSize(new java.awt.Dimension(80, 62));
        keyboardButtonX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonXActionPerformed(evt);
            }
        });

        keyboardButtonT.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        keyboardButtonT.setText("T");
        keyboardButtonT.setPreferredSize(new java.awt.Dimension(80, 62));
        keyboardButtonT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonTActionPerformed(evt);
            }
        });

        keyboardButtonU.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        keyboardButtonU.setText("U");
        keyboardButtonU.setPreferredSize(new java.awt.Dimension(80, 62));
        keyboardButtonU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonUActionPerformed(evt);
            }
        });

        keyboardButtonV.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        keyboardButtonV.setText("V");
        keyboardButtonV.setPreferredSize(new java.awt.Dimension(80, 62));
        keyboardButtonV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonVActionPerformed(evt);
            }
        });

        keyboardButtonR.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        keyboardButtonR.setText("R");
        keyboardButtonR.setPreferredSize(new java.awt.Dimension(80, 62));
        keyboardButtonR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonRActionPerformed(evt);
            }
        });

        keyboardButtonY.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        keyboardButtonY.setText("Y");
        keyboardButtonY.setPreferredSize(new java.awt.Dimension(80, 62));
        keyboardButtonY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonYActionPerformed(evt);
            }
        });

        keyboardButtonZ.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        keyboardButtonZ.setText("Z");
        keyboardButtonZ.setPreferredSize(new java.awt.Dimension(80, 62));
        keyboardButtonZ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonZActionPerformed(evt);
            }
        });

        keyboardButtonEndTurn.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        keyboardButtonEndTurn.setText("End Turn");
        keyboardButtonEndTurn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonEndTurnActionPerformed(evt);
            }
        });

        keyboardButtonWord.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        keyboardButtonWord.setText("Word");
        keyboardButtonWord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyboardButtonWordActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(keyboardButtonY, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                    .addComponent(keyboardButtonS, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(keyboardButtonA, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(keyboardButtonG, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(keyboardButtonM, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(keyboardButtonT, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                    .addComponent(keyboardButtonN, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(keyboardButtonH, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(keyboardButtonB, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(keyboardButtonZ, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(keyboardButtonU, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                            .addComponent(keyboardButtonC, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(keyboardButtonI, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(keyboardButtonO, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(keyboardButtonV, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                            .addComponent(keyboardButtonP, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(keyboardButtonJ, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(keyboardButtonD, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addComponent(keyboardButtonEndTurn, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(keyboardButtonW, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                            .addComponent(keyboardButtonE, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(keyboardButtonK, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(keyboardButtonQ, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(keyboardButtonF, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                            .addComponent(keyboardButtonL, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(keyboardButtonR, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(keyboardButtonX, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(keyboardButtonWord, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(keyboardButtonE, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(keyboardButtonB, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(keyboardButtonC, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(keyboardButtonD, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(keyboardButtonF, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(keyboardButtonA, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(keyboardButtonH, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(keyboardButtonG, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(keyboardButtonI, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(keyboardButtonJ, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(keyboardButtonK, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(keyboardButtonL, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(keyboardButtonN, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(keyboardButtonM, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(keyboardButtonO, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(keyboardButtonP, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(keyboardButtonQ, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(keyboardButtonR, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(keyboardButtonX, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(keyboardButtonS, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(keyboardButtonT, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(keyboardButtonU, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(keyboardButtonV, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(keyboardButtonW, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(keyboardButtonY, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(keyboardButtonZ, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(keyboardButtonEndTurn, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(keyboardButtonWord, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        keyboardLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        keyboardLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        keyboardLabel.setText("KeyBoard");
        keyboardLabel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), new javax.swing.border.MatteBorder(null)));

        jPanel2.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), new javax.swing.border.MatteBorder(null)));
        jPanel2.setAutoscrolls(true);

        mainMenuLoginButton.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        mainMenuLoginButton.setText("Register & Login");
        mainMenuLoginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainMenuLoginButtonActionPerformed(evt);
            }
        });

        mainMenuStartButton.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        mainMenuStartButton.setText("Start Game");
        mainMenuStartButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainMenuStartButtonActionPerformed(evt);
            }
        });

        jButtonAskGame.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        jButtonAskGame.setText("Invite Players");
        jButtonAskGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAskGameActionPerformed(evt);
            }
        });

        mainMenuPassButton.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        mainMenuPassButton.setText("Pass");
        mainMenuPassButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainMenuPassButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPaneNamelist)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(mainMenuLoginButton, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mainMenuPassButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(mainMenuStartButton, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonAskGame, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)))
                        .addGap(26, 26, 26))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPaneNamelist, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mainMenuLoginButton, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mainMenuPassButton, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAskGame, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mainMenuStartButton, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), new javax.swing.border.MatteBorder(null)));
        jPanel4.setAutoscrolls(true);

        chatBox.setColumns(20);
        chatBox.setFont(new java.awt.Font("Times New Roman", 0, 13)); // NOI18N
        chatBox.setRows(5);
        jScrollPane1.setViewportView(chatBox);

        chatBoxSendButton.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        chatBoxSendButton.setText("Send Message");
        chatBoxSendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chatBoxSendButtonActionPerformed(evt);
            }
        });

        chatBoxCleanButton.setFont(new java.awt.Font("Times New Roman", 1, 11)); // NOI18N
        chatBoxCleanButton.setText("Clean !!!");
        chatBoxCleanButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chatBoxCleanButtonActionPerformed(evt);
            }
        });

        jTextField1.setFont(new java.awt.Font("Times New Roman", 0, 11)); // NOI18N
        jTextField1.setToolTipText("");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField1)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(chatBoxSendButton, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chatBoxCleanButton, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chatBoxSendButton, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chatBoxCleanButton, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        chatBoxLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        chatBoxLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        chatBoxLabel.setText("Chat Box ");
        chatBoxLabel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), new javax.swing.border.MatteBorder(null)));

        mainMenuLabel.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        mainMenuLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mainMenuLabel.setText("Main Menu");
        mainMenuLabel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), new javax.swing.border.MatteBorder(null)));

        javax.swing.GroupLayout ControllerLayout = new javax.swing.GroupLayout(Controller);
        Controller.setLayout(ControllerLayout);
        ControllerLayout.setHorizontalGroup(
            ControllerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ControllerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ControllerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(mainMenuLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ControllerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chatBoxLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ControllerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(keyboardLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        ControllerLayout.setVerticalGroup(
            ControllerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ControllerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ControllerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(keyboardLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chatBoxLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mainMenuLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ControllerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), new javax.swing.border.MatteBorder(null)));
        jPanel1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel3.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("SCORES");
        jLabel3.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), new javax.swing.border.MatteBorder(null)));

        player1TurnLabel.setBackground(new java.awt.Color(255, 0, 51));
        player1TurnLabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                player1TurnLabelActionPerformed(evt);
            }
        });

        player2TurnLabel.setBackground(new java.awt.Color(255, 0, 51));
        player2TurnLabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                player2TurnLabelActionPerformed(evt);
            }
        });

        player3TurnLabel.setBackground(new java.awt.Color(255, 0, 51));
        player3TurnLabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                player3TurnLabelActionPerformed(evt);
            }
        });

        player4TurnLabel.setBackground(new java.awt.Color(255, 0, 51));
        player4TurnLabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                player4TurnLabelActionPerformed(evt);
            }
        });

        player1NameTextfield.setEditable(false);
        player1NameTextfield.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        player1NameTextfield.setText("P1");
        player1NameTextfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                player1NameTextfieldActionPerformed(evt);
            }
        });

        player2NameTextfield.setEditable(false);
        player2NameTextfield.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        player2NameTextfield.setText("P2");
        player2NameTextfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                player2NameTextfieldActionPerformed(evt);
            }
        });

        player3NameTextfield.setEditable(false);
        player3NameTextfield.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        player3NameTextfield.setText("P3");
        player3NameTextfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                player3NameTextfieldActionPerformed(evt);
            }
        });

        player4NameTextfield.setEditable(false);
        player4NameTextfield.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        player4NameTextfield.setText("P4");
        player4NameTextfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                player4NameTextfieldActionPerformed(evt);
            }
        });

        Player1scoreTextfield.setEditable(false);
        Player1scoreTextfield.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        Player1scoreTextfield.setText("score1");

        Player2scoreTextfield.setEditable(false);
        Player2scoreTextfield.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        Player2scoreTextfield.setText("score2");

        Player3scoreTextfield.setEditable(false);
        Player3scoreTextfield.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        Player3scoreTextfield.setText("score3");

        Player4scoreTextfield.setEditable(false);
        Player4scoreTextfield.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        Player4scoreTextfield.setText("score4");

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Turn");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel4.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Player Username");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel5.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Score");
        jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel6.setFocusCycleRoot(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(player4TurnLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(player4NameTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14)
                        .addComponent(Player4scoreTextfield))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(player3TurnLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(player3NameTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14)
                        .addComponent(Player3scoreTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(player2TurnLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(player2NameTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14)
                        .addComponent(Player2scoreTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(player1TurnLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(player1NameTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(Player1scoreTextfield)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(49, 49, 49)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(player1TurnLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(player1NameTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Player1scoreTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(38, 38, 38)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(player2TurnLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(player2NameTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Player2scoreTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(player3TurnLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(player3NameTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Player3scoreTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(player4TurnLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(player4NameTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Player4scoreTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clientscrabble/Leaderboard.png"))); // NOI18N
        jLabel2.setLabelFor(jLabel2);
        jLabel2.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), new javax.swing.border.MatteBorder(null)));

        GridContainerPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), new javax.swing.border.MatteBorder(null)));
        GridContainerPanel.setLayout(new java.awt.GridLayout(20, 1, 1, 1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Controller, javax.swing.GroupLayout.DEFAULT_SIZE, 980, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(GridContainerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 733, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(GridContainerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(Controller, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAskGameActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonAskGameActionPerformed
        // TODO add your handling code here:
        int countTicked = 0;
        this.listToJoin = new ArrayList<>();
        for (int i = 0; i < model.size(); i++) {
            if (model.get(i).isSelected()) {
                countTicked++;
                listToJoin.add(model.get(i).toString());
//                temp+= model.get(i).toString();
//                temp+= ",";
//                System.out.println(model.get(i).toString());
            }
        }
        if (countTicked > 4) {
            JOptionPane.showMessageDialog(rootPane, "too much player");
            this.listToJoin = new ArrayList<>();
        } else if (countTicked < 2) {
            JOptionPane.showMessageDialog(rootPane, "too few player");
            this.listToJoin = new ArrayList<>();
        } else {
            controller.invite(listToJoin);
            this.EnableKeyboard();
            mainMenuPassButton.setEnabled(true);
        }

    }// GEN-LAST:event_jButtonAskGameActionPerformed

    private void mainMenuPassButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mainMenuPassButtonActionPerformed
        try {
            // TODO add your handling code here:
            TurnData turnData = new TurnData();
            turnData.column = button.getPositionColumn();
            turnData.row = button.getPositionRow();
            turnData.letter = button.getLetter();
            turnData.isTurn = false;
            DisableKeyboard();
            controller.endTurn(turnData);
            controller.endGame(this.controller.getClientName());
            mainMenuPassButton.setEnabled(false);
        } catch (RemoteException ex) {
            Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_mainMenuPassButtonActionPerformed

    private void player1TurnLabelActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_player1TurnLabelActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_player1TurnLabelActionPerformed

    private void player2TurnLabelActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_player2TurnLabelActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_player2TurnLabelActionPerformed

    private void player3TurnLabelActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_player3TurnLabelActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_player3TurnLabelActionPerformed

    private void player4TurnLabelActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_player4TurnLabelActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_player4TurnLabelActionPerformed

    private void keyboardButtonWordActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonWordActionPerformed
        // TODO add your handling code here:
        receivedWords = controller.wordIdentifier(button);
        // String temp1=receivedWords.get(0);
        controller.setValidMoves(button);
        // controller.setValidMoves(button);
        TurnData turnData = new TurnData();
        turnData.column = button.getPositionColumn();
        turnData.row = button.getPositionRow();
        turnData.letter = button.getLetter();
        turnData.isTurn = false;
        DisableKeyboard();

        controller.endTurn(turnData);
        int score = controller.requestVoting(receivedWords);
        Player1scoreTextfield.setText(Integer.toString(score));

        controller.ConsecutivePassHandler();
        mainMenuPassButton.setEnabled(false);

        for (String temp : receivedWords) {
            System.out.println(temp);

        }
    }// GEN-LAST:event_keyboardButtonWordActionPerformed

    private void keyboardButtonEndTurnActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonEndTurnActionPerformed
        // TODO add your handling code here:
        //GridButton buttonTemp=button;
        if (letterTemp == ' ' || button.getLetter() == ' ') {

            JOptionPane.showMessageDialog(this, "In the case you dont want to put any character, please select the pass button");

        } else {
            int i = controller.setValidMoves(button);
            if (i == 0) {
                JOptionPane.showMessageDialog(this, "No characters has been selected from the Keyboard or inserted to the Grid");
            }
            TurnData turnData = new TurnData();
            turnData.column = button.getPositionColumn();
            turnData.row = button.getPositionRow();
            turnData.letter = button.getLetter();
            turnData.isTurn = false;

            controller.endTurn(turnData);
            DisableKeyboard();
            controller.ConsecutivePassHandler();
            mainMenuPassButton.setEnabled(false);

            letterTemp = ' ';
            // button.setLetter(' ',true);
        }
    }// GEN-LAST:event_keyboardButtonEndTurnActionPerformed

    private void keyboardButtonZActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonZActionPerformed
        // TODO add your handling code here:
        String s = keyboardButtonZ.getText();
        System.out.println(s);

        letterTemp = s.charAt(0);
    }// GEN-LAST:event_keyboardButtonZActionPerformed

    private void keyboardButtonYActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonYActionPerformed
        // TODO add your handling code here:
        String s = keyboardButtonY.getText();
        System.out.println(s);

        letterTemp = s.charAt(0);
    }// GEN-LAST:event_keyboardButtonYActionPerformed

    private void keyboardButtonRActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonRActionPerformed
        // TODO add your handling code here:
        String s = keyboardButtonR.getText();
        System.out.println(s);

        letterTemp = s.charAt(0);
    }// GEN-LAST:event_keyboardButtonRActionPerformed

    private void keyboardButtonVActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonVActionPerformed
        // TODO add your handling code here:
        String s = keyboardButtonV.getText();
        System.out.println(s);

        letterTemp = s.charAt(0);
    }// GEN-LAST:event_keyboardButtonVActionPerformed

    private void keyboardButtonUActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonUActionPerformed
        // TODO add your handling code here:
        String s = keyboardButtonU.getText();
        System.out.println(s);

        letterTemp = s.charAt(0);
    }// GEN-LAST:event_keyboardButtonUActionPerformed

    private void keyboardButtonTActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonTActionPerformed
        // TODO add your handling code here:
        String s = keyboardButtonT.getText();
        System.out.println(s);

        letterTemp = s.charAt(0);
    }// GEN-LAST:event_keyboardButtonTActionPerformed

    private void keyboardButtonXActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonXActionPerformed
        // TODO add your handling code here:
        String s = keyboardButtonX.getText();
        System.out.println(s);

        letterTemp = s.charAt(0);
    }// GEN-LAST:event_keyboardButtonXActionPerformed

    private void keyboardButtonSActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonSActionPerformed
        // TODO add your handling code here:
        String s = keyboardButtonS.getText();
        System.out.println(s);

        letterTemp = s.charAt(0);
    }// GEN-LAST:event_keyboardButtonSActionPerformed

    private void keyboardButtonQActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonQActionPerformed
        // TODO add your handling code here:
        String s = keyboardButtonQ.getText();
        System.out.println(s);

        letterTemp = s.charAt(0);
    }// GEN-LAST:event_keyboardButtonQActionPerformed

    private void keyboardButtonPActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonPActionPerformed
        // TODO add your handling code here:
        String s = keyboardButtonP.getText();
        System.out.println(s);

        letterTemp = s.charAt(0);
    }// GEN-LAST:event_keyboardButtonPActionPerformed

    private void keyboardButtonOActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonOActionPerformed
        // TODO add your handling code here:
        String s = keyboardButtonO.getText();
        System.out.println(s);

        letterTemp = s.charAt(0);
    }// GEN-LAST:event_keyboardButtonOActionPerformed

    private void keyboardButtonNActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonNActionPerformed
        // TODO add your handling code here:
        String s = keyboardButtonN.getText();
        System.out.println(s);

        letterTemp = s.charAt(0);
    }// GEN-LAST:event_keyboardButtonNActionPerformed

    private void keyboardButtonWActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonWActionPerformed
        // TODO add your handling code here:
        String s = keyboardButtonW.getText();
        System.out.println(s);

        letterTemp = s.charAt(0);
    }// GEN-LAST:event_keyboardButtonWActionPerformed

    private void keyboardButtonMActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonMActionPerformed
        // TODO add your handling code here:
        String s = keyboardButtonM.getText();
        System.out.println(s);

        letterTemp = s.charAt(0);
    }// GEN-LAST:event_keyboardButtonMActionPerformed

    private void keyboardButtonKActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonKActionPerformed
        // TODO add your handling code here:
        String s = keyboardButtonK.getText();
        System.out.println(s);

        letterTemp = s.charAt(0);
    }// GEN-LAST:event_keyboardButtonKActionPerformed

    private void keyboardButtonLActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonLActionPerformed
        // TODO add your handling code here:
        String s = keyboardButtonL.getText();
        System.out.println(s);

        letterTemp = s.charAt(0);
    }// GEN-LAST:event_keyboardButtonLActionPerformed

    private void keyboardButtonJActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonJActionPerformed
        // TODO add your handling code here:
        String s = keyboardButtonJ.getText();
        System.out.println(s);

        letterTemp = s.charAt(0);
    }// GEN-LAST:event_keyboardButtonJActionPerformed

    private void keyboardButtonIActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonIActionPerformed
        // TODO add your handling code here:
        String s = keyboardButtonI.getText();
        System.out.println(s);

        letterTemp = s.charAt(0);
    }// GEN-LAST:event_keyboardButtonIActionPerformed

    private void keyboardButtonHActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonHActionPerformed
        // TODO add your handling code here:
        String s = keyboardButtonH.getText();
        System.out.println(s);

        letterTemp = s.charAt(0);
    }// GEN-LAST:event_keyboardButtonHActionPerformed

    private void keyboardButtonGActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonGActionPerformed
        // TODO add your handling code here:
        String s = keyboardButtonG.getText();
        System.out.println(s);

        letterTemp = s.charAt(0);
    }// GEN-LAST:event_keyboardButtonGActionPerformed

    private void keyboardButtonFActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonFActionPerformed
        // TODO add your handling code here:
        String s = keyboardButtonF.getText();
        System.out.println(s);

        letterTemp = s.charAt(0);
    }// GEN-LAST:event_keyboardButtonFActionPerformed

    private void keyboardButtonDActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonDActionPerformed
        // TODO add your handling code here:
        String s = keyboardButtonD.getText();
        System.out.println(s);

        letterTemp = s.charAt(0);
    }// GEN-LAST:event_keyboardButtonDActionPerformed

    private void keyboardButtonCActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonCActionPerformed
        // TODO add your handling code here:
        String s = keyboardButtonC.getText();
        System.out.println(s);

        letterTemp = s.charAt(0);
    }// GEN-LAST:event_keyboardButtonCActionPerformed

    private void keyboardButtonBActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonBActionPerformed
        // TODO add your handling code here:
        String s = keyboardButtonB.getText();
        System.out.println(s);

        letterTemp = s.charAt(0);
    }// GEN-LAST:event_keyboardButtonBActionPerformed

    private void keyboardButtonEActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonEActionPerformed
        // TODO add your handling code here:
        String s = keyboardButtonE.getText();
        System.out.println(s);

        letterTemp = s.charAt(0);
    }// GEN-LAST:event_keyboardButtonEActionPerformed

    private void keyboardButtonAActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_keyboardButtonAActionPerformed
        // TODO add your handling code here:
        String s = keyboardButtonA.getText();
        System.out.println(s);

        letterTemp = s.charAt(0);

    }// GEN-LAST:event_keyboardButtonAActionPerformed

    private void keyboardButtonAMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_keyboardButtonAMousePressed
        // TODO add your handling code here:
    }// GEN-LAST:event_keyboardButtonAMousePressed

    private void keyboardButtonAMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_keyboardButtonAMouseClicked
        // TODO add your handling code here:
    }// GEN-LAST:event_keyboardButtonAMouseClicked

    private void chatBoxSendButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_chatBoxSendButtonActionPerformed
        System.out.println(this.jTextField1.getText());
        String userName;
        try {
            userName = this.controller.getClientName();
        } catch (RemoteException ex) {
            userName = "unknown";
        }
        controller.broadcastChat(userName, this.jTextField1.getText());

        // TODO add your handling code here:
    }// GEN-LAST:event_chatBoxSendButtonActionPerformed

    private void chatBoxCleanButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_chatBoxCleanButtonActionPerformed
        this.chatBox.setText("");
        // TODO add your handling code here:
    }// GEN-LAST:event_chatBoxCleanButtonActionPerformed

    private void mainMenuLoginButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_mainMenuLoginButtonActionPerformed
        LoginScreen loginScreen = new LoginScreen(controller);

        /*
		 * try {
		 * 
		 * // TODO add your handling code here: //gameServer.startGame(); controller. }
		 * catch (RemoteException ex) {
		 * Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, ex);
		 * System.out.println("Problem occured in starting the Game"); }
         */
    }// GEN-LAST:event_mainMenuLoginButtonActionPerformed

    private void player1NameTextfieldActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_player1NameTextfieldActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_player1NameTextfieldActionPerformed

    private void player2NameTextfieldActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_player2NameTextfieldActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_player2NameTextfieldActionPerformed

    private void player3NameTextfieldActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_player3NameTextfieldActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_player3NameTextfieldActionPerformed

    private void player4NameTextfieldActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_player4NameTextfieldActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_player4NameTextfieldActionPerformed

    private void mainMenuStartButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_mainMenuStartButtonActionPerformed
        // TODO add your handling code here:
        /*
		 * if (controller.startGame()) { this.EnableKeyboard(); }
         */
    }// GEN-LAST:event_mainMenuStartButtonActionPerformed

    public void addGridButtonListActionListener(java.awt.event.ActionListener evt) {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                gridButtonList.get(i).get(j).addActionListener(evt);
            }
        }
    }

    public void GridButtonPressed() {

        addGridButtonListActionListener((java.awt.event.ActionEvent evt) -> {

            // get the pressed button
            GridButton currentButton = (GridButton) evt.getSource();
            if (currentButton.getButtonState() == NOT_ASSIGNED && letterTemp != ' ') {
                currentButton.setLetter(letterTemp);
                currentButton.setButtonState(currentButton.ASSIGNED);
                button = currentButton;

            } else {
                letterTemp = ' ';
                currentButton.setLetter(letterTemp);
                currentButton.setButtonState(currentButton.NOT_ASSIGNED);
                //button = currentButton;
            }

        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Controller;
    private javax.swing.JPanel GridContainerPanel;
    public static javax.swing.JTextField Player1scoreTextfield;
    public static javax.swing.JTextField Player2scoreTextfield;
    public static javax.swing.JTextField Player3scoreTextfield;
    public static javax.swing.JTextField Player4scoreTextfield;
    private javax.swing.JTextArea chatBox;
    private javax.swing.JButton chatBoxCleanButton;
    private javax.swing.JLabel chatBoxLabel;
    public static javax.swing.JButton chatBoxSendButton;
    public static javax.swing.JButton jButtonAskGame;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    public static javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPaneNamelist;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JButton keyboardButtonA;
    private javax.swing.JButton keyboardButtonB;
    private javax.swing.JButton keyboardButtonC;
    private javax.swing.JButton keyboardButtonD;
    private javax.swing.JButton keyboardButtonE;
    private javax.swing.JButton keyboardButtonEndTurn;
    private javax.swing.JButton keyboardButtonF;
    private javax.swing.JButton keyboardButtonG;
    private javax.swing.JButton keyboardButtonH;
    private javax.swing.JButton keyboardButtonI;
    private javax.swing.JButton keyboardButtonJ;
    private javax.swing.JButton keyboardButtonK;
    private javax.swing.JButton keyboardButtonL;
    private javax.swing.JButton keyboardButtonM;
    private javax.swing.JButton keyboardButtonN;
    private javax.swing.JButton keyboardButtonO;
    private javax.swing.JButton keyboardButtonP;
    private javax.swing.JButton keyboardButtonQ;
    private javax.swing.JButton keyboardButtonR;
    private javax.swing.JButton keyboardButtonS;
    private javax.swing.JButton keyboardButtonT;
    private javax.swing.JButton keyboardButtonU;
    private javax.swing.JButton keyboardButtonV;
    private javax.swing.JButton keyboardButtonW;
    private javax.swing.JButton keyboardButtonWord;
    private javax.swing.JButton keyboardButtonX;
    private javax.swing.JButton keyboardButtonY;
    private javax.swing.JButton keyboardButtonZ;
    private javax.swing.JLabel keyboardLabel;
    private javax.swing.JLabel mainMenuLabel;
    public static javax.swing.JButton mainMenuLoginButton;
    public static javax.swing.JButton mainMenuPassButton;
    public static javax.swing.JButton mainMenuStartButton;
    public static javax.swing.JTextField player1NameTextfield;
    public static javax.swing.JButton player1TurnLabel;
    public static javax.swing.JTextField player2NameTextfield;
    public static javax.swing.JButton player2TurnLabel;
    public static javax.swing.JTextField player3NameTextfield;
    public static javax.swing.JButton player3TurnLabel;
    public static javax.swing.JTextField player4NameTextfield;
    public static javax.swing.JButton player4TurnLabel;
    // End of variables declaration//GEN-END:variables

    public void GridLayoutButtonCreator(int boardSize) {
        int i = 0;
        int j = 0;
        for (i = 0; i < boardSize; i++) {
            gridButtonList.add(new ArrayList<>());
            for (j = 0; j < boardSize; j++) {
                gridButtonList.get(i).add(new GridButton(i, j, ' '));
                gridButtonList.get(i).get(j).setButtonState(GridButton.NOT_ASSIGNED);
                gridButtonList.get(i).get(j).setBackground(Color.LIGHT_GRAY);
                gridButtonList.get(i).get(j).setMargin(new Insets(0, 0, 0, 0));
                gridButtonList.get(i).get(j).setPositionRow(i);
                gridButtonList.get(i).get(j).setPositionColumn(j);

                gridButtonList.get(i).get(j).setEnabled(false);

                GridContainerPanel.add(gridButtonList.get(i).get(j));

            }

        }
        gridButtonList.get(9).get(9).setEnabled(true);
        gridButtonList.get(9).get(9).setBackground(Color.WHITE);
        // gridButtonList.get(9).get(9).setButtonState(GridButton.ASSIGNED);

        // gridButtonList.get(9).get(9).setLetter('A');
    }

    public void UpdateChatbox(String msg) {
//		String queueText = String.join(System.lineSeparator(), msg);
//		System.out.println(queueText);		
        this.chatBox.append(msg);
        this.chatBox.append(System.lineSeparator());
    }

    public void DisableKeyboard() {
        keyboardButtonA.setEnabled(false);
        keyboardButtonB.setEnabled(false);
        keyboardButtonC.setEnabled(false);
        keyboardButtonD.setEnabled(false);
        keyboardButtonE.setEnabled(false);
        keyboardButtonF.setEnabled(false);
        keyboardButtonG.setEnabled(false);
        keyboardButtonH.setEnabled(false);
        keyboardButtonI.setEnabled(false);
        keyboardButtonJ.setEnabled(false);
        keyboardButtonK.setEnabled(false);
        keyboardButtonL.setEnabled(false);
        keyboardButtonM.setEnabled(false);
        keyboardButtonN.setEnabled(false);
        keyboardButtonO.setEnabled(false);
        keyboardButtonP.setEnabled(false);
        keyboardButtonQ.setEnabled(false);
        keyboardButtonR.setEnabled(false);
        keyboardButtonS.setEnabled(false);
        keyboardButtonT.setEnabled(false);
        keyboardButtonU.setEnabled(false);
        keyboardButtonV.setEnabled(false);
        keyboardButtonW.setEnabled(false);
        keyboardButtonX.setEnabled(false);
        keyboardButtonY.setEnabled(false);
        keyboardButtonZ.setEnabled(false);
        keyboardButtonEndTurn.setEnabled(false);
        keyboardButtonWord.setEnabled(false);

    }

    public void EnableKeyboard() {
        keyboardButtonA.setEnabled(true);
        keyboardButtonB.setEnabled(true);
        keyboardButtonC.setEnabled(true);
        keyboardButtonD.setEnabled(true);
        keyboardButtonE.setEnabled(true);
        keyboardButtonF.setEnabled(true);
        keyboardButtonG.setEnabled(true);
        keyboardButtonH.setEnabled(true);
        keyboardButtonI.setEnabled(true);
        keyboardButtonJ.setEnabled(true);
        keyboardButtonK.setEnabled(true);
        keyboardButtonL.setEnabled(true);
        keyboardButtonM.setEnabled(true);
        keyboardButtonN.setEnabled(true);
        keyboardButtonO.setEnabled(true);
        keyboardButtonP.setEnabled(true);
        keyboardButtonQ.setEnabled(true);
        keyboardButtonR.setEnabled(true);
        keyboardButtonS.setEnabled(true);
        keyboardButtonT.setEnabled(true);
        keyboardButtonU.setEnabled(true);
        keyboardButtonV.setEnabled(true);
        keyboardButtonW.setEnabled(true);
        keyboardButtonX.setEnabled(true);
        keyboardButtonY.setEnabled(true);
        keyboardButtonZ.setEnabled(true);
        keyboardButtonEndTurn.setEnabled(true);
        keyboardButtonWord.setEnabled(true);

    }

    public boolean changeTurn(TurnData turnData) {
        button = gridButtonList.get(turnData.row).get(turnData.column);
        button.setLetter(turnData.letter);
        button.setButtonState(button.ASSIGNED);
        setPlayersTurnColors(turnData.nextUser);
        controller.setValidMoves(button);
        //button.setLetter(' ', true);
        if (turnData.isTurn) {
            EnableKeyboard();
            mainMenuPassButton.setEnabled(true);
        }
        return true;
    }

    public void updateScoreForPlayer(int score, String username) {
        if (username.equals(player1NameTextfield.getText())) {
            Player1scoreTextfield.setText(String.valueOf(score));

        } else if (username.equals(player2NameTextfield.getText())) {
            Player2scoreTextfield.setText(String.valueOf(score));
        } else if (username.equals(player3NameTextfield.getText())) {
            Player3scoreTextfield.setText(String.valueOf(score));
        } else if (username.equals(player4NameTextfield.getText())) {
            Player4scoreTextfield.setText(String.valueOf(score));
        }
        //ystem.out.println("Score for " + userName + " is " + score);
    }

    public void setPlayersTurnColors(String username) {
        if (username.equals(player1NameTextfield.getText())) {
            player1TurnLabel.setBackground(Color.GREEN);
            player2TurnLabel.setBackground(Color.RED);
            player3TurnLabel.setBackground(Color.RED);
            player4TurnLabel.setBackground(Color.RED);
        } else if (username.equals(player2NameTextfield.getText())) {
            player1TurnLabel.setBackground(Color.RED);
            player2TurnLabel.setBackground(Color.GREEN);
            player3TurnLabel.setBackground(Color.RED);
            player4TurnLabel.setBackground(Color.RED);
        } else if (username.equals(player3NameTextfield.getText())) {
            player1TurnLabel.setBackground(Color.RED);
            player2TurnLabel.setBackground(Color.RED);
            player3TurnLabel.setBackground(Color.GREEN);
            player4TurnLabel.setBackground(Color.RED);
        } else if (username.equals(player4NameTextfield.getText())) {
            player1TurnLabel.setBackground(Color.RED);
            player2TurnLabel.setBackground(Color.RED);
            player3TurnLabel.setBackground(Color.RED);
            player4TurnLabel.setBackground(Color.GREEN);
        }
    }

    public void CreateRankingScreen(ArrayList<String> players) {
        if (players.size() == 1) {
            player1TurnLabel.setVisible(true);
            player1NameTextfield.setVisible(true);
            Player1scoreTextfield.setVisible(true);
            //Player1scoreTextfield.setText();
            player1NameTextfield.setText(players.get(0));
            player1TurnLabel.setBackground(Color.GREEN);
        }
        if (players.size() == 2) {
            player1TurnLabel.setVisible(true);
            player1NameTextfield.setVisible(true);
            Player1scoreTextfield.setVisible(true);
            player1NameTextfield.setText(players.get(0));
            player1TurnLabel.setBackground(Color.GREEN);

            player2TurnLabel.setVisible(true);
            player2NameTextfield.setVisible(true);
            Player2scoreTextfield.setVisible(true);
            player2NameTextfield.setText(players.get(1));
        }
        if (players.size() == 3) {
            player1TurnLabel.setVisible(true);
            player1NameTextfield.setVisible(true);
            Player1scoreTextfield.setVisible(true);
            player1NameTextfield.setText(players.get(0));
            player1TurnLabel.setBackground(Color.GREEN);

            player2TurnLabel.setVisible(true);
            player2NameTextfield.setVisible(true);
            Player2scoreTextfield.setVisible(true);
            player2NameTextfield.setText(players.get(1));

            player3TurnLabel.setVisible(true);
            player3NameTextfield.setVisible(true);
            Player3scoreTextfield.setVisible(true);
            player3NameTextfield.setText(players.get(2));
        }

        if (players.size() == 4) {
            player1TurnLabel.setVisible(true);
            player1NameTextfield.setVisible(true);
            Player1scoreTextfield.setVisible(true);
            player1NameTextfield.setText(players.get(0));
            player1TurnLabel.setBackground(Color.GREEN);

            player2TurnLabel.setVisible(true);
            player2NameTextfield.setVisible(true);
            Player2scoreTextfield.setVisible(true);
            player2NameTextfield.setText(players.get(1));

            player3TurnLabel.setVisible(true);
            player3NameTextfield.setVisible(true);
            Player3scoreTextfield.setVisible(true);
            player3NameTextfield.setText(players.get(2));

            player4TurnLabel.setVisible(true);
            player4NameTextfield.setVisible(true);
            Player4scoreTextfield.setVisible(true);
            player4NameTextfield.setText(players.get(3));
        }
    }

    public boolean invite(String inviter) {
        // JFrame jf = new JFrame();
        // jf.setAlwaysOnTop(true);
        int option = JOptionPane.showConfirmDialog(this, inviter + " has invited you to a game. Do you want to join?",
                "Invitation Recieved!", JOptionPane.YES_NO_OPTION);
        if (option == 0) {
            return true;
        }
        return false;
    }

    public void removeUserFromPlayerList(String userName) {
        if (userName.equals(player1NameTextfield.getText())) {
            player1TurnLabel.setVisible(false);
            player1NameTextfield.setVisible(false);
            Player1scoreTextfield.setVisible(false);
        } else if (userName.equals(player2NameTextfield.getText())) {
            player2TurnLabel.setVisible(false);
            player2NameTextfield.setVisible(false);
            Player2scoreTextfield.setVisible(false);
        } else if (userName.equals(player3NameTextfield.getText())) {
            player3TurnLabel.setVisible(false);
            player3NameTextfield.setVisible(false);
            Player3scoreTextfield.setVisible(false);
        } else if (userName.equals(player4NameTextfield.getText())) {
            player4TurnLabel.setVisible(false);
            player4NameTextfield.setVisible(false);
            Player4scoreTextfield.setVisible(false);
        }
    }

}

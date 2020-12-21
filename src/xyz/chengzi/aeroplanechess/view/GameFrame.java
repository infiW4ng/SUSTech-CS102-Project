package xyz.chengzi.aeroplanechess.view;

import xyz.chengzi.aeroplanechess.controller.GameController;
import xyz.chengzi.aeroplanechess.listener.GameStateListener;

import javax.swing.*;

public class GameFrame extends JFrame implements GameStateListener {
    private static final String[] PLAYER_NAMES = {"Yellow", "Blue", "Green", "Red"};

    private final JLabel statusLabel = new JLabel();

    public GameFrame(GameController controller) {
        controller.registerListener(this);

        setTitle("Remove Java C# Strong");
        setSize(772, 825);
        setLocationRelativeTo(null); // Center the window
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(null);

        statusLabel.setLocation(0, 758);
        statusLabel.setFont(statusLabel.getFont().deriveFont(18.0f));
        statusLabel.setSize(400, 20);
        add(statusLabel);

        DiceSelectorComponent diceSelectorComponent = new DiceSelectorComponent();
        diceSelectorComponent.setLocation(396, 758);
        add(diceSelectorComponent);

        JButton buttonRoll = new JButton("roll");
        JButton buttonAdd = new JButton("+");
        JButton buttonMin = new JButton("-");
        JButton buttonMul = new JButton("*");
        JButton buttonDiv = new JButton("/");
        buttonRoll.addActionListener((e) -> {
            if (diceSelectorComponent.isRandomDice()) {
                Integer[] dice = controller.rollDice();
                if (dice != null) {
                    statusLabel.setText(String.format("[%s] Rolled a %c %c (%d %d) | Choose Op",
                            PLAYER_NAMES[controller.getCurrentPlayer()], '\u267F' + dice[1], '\u267F' + dice[2], dice[1], dice[2]));
                } else {
                    JOptionPane.showMessageDialog(this, "You have already rolled the dice");
                }
            } else {
                controller.setDice((Integer)diceSelectorComponent.getSelectedDice());
                statusLabel.setText(String.format("[%s] Ordered | Move %d steps",
                        PLAYER_NAMES[controller.getCurrentPlayer()], controller.getDice()[0]));
            }
        });
        buttonAdd.addActionListener((e) -> {
            Integer[] dice = controller.getDice();
            if (dice != null && dice[3] != null) {
                controller.opDice(3);
                statusLabel.setText(String.format("[%s] Rolled a %c %c (%d %d) | Move %d steps",
                        PLAYER_NAMES[controller.getCurrentPlayer()], '\u267F' + dice[1], '\u267F' + dice[2], dice[1], dice[2], dice[3]));
            } else {
                JOptionPane.showMessageDialog(this, "Illegal Operation");
            }
        });
        buttonMin.addActionListener((e) -> {
            Integer[] dice = controller.getDice();
            if (dice != null && dice[4] != null) {
                controller.opDice(4);
                statusLabel.setText(String.format("[%s] Rolled a %c %c (%d %d) | Move %d steps",
                        PLAYER_NAMES[controller.getCurrentPlayer()], '\u267F' + dice[1], '\u267F' + dice[2], dice[1], dice[2], dice[4]));
            } else {
                JOptionPane.showMessageDialog(this, "Illegal Operation");
            }
        });
        buttonMul.addActionListener((e) -> {
            Integer[] dice = controller.getDice();
            if (dice != null && dice[5] != null) {
                controller.opDice(5);
                statusLabel.setText(String.format("[%s] Rolled a %c %c (%d %d) | Move %d steps",
                        PLAYER_NAMES[controller.getCurrentPlayer()], '\u267F' + dice[1], '\u267F' + dice[2], dice[1], dice[2], dice[5]));
            } else {
                JOptionPane.showMessageDialog(this, "Illegal Operation");
            }
        });
        buttonDiv.addActionListener((e) -> {
            Integer[] dice = controller.getDice();
            if (dice != null && dice[6] != null) {
                controller.opDice(6);
                statusLabel.setText(String.format("[%s] Rolled a %c %c (%d %d) | Move %d steps",
                        PLAYER_NAMES[controller.getCurrentPlayer()], '\u267F' + dice[1], '\u267F' + dice[2], dice[1], dice[2], dice[6]));
            } else {
                JOptionPane.showMessageDialog(this, "Illegal Operation");
            }
        });

        buttonRoll.setLocation(668, 756);
        buttonRoll.setFont(buttonRoll.getFont().deriveFont(18.0f));
        buttonRoll.setSize(90, 30);
        buttonAdd.setLocation(200, 300);
        buttonAdd.setFont(buttonAdd.getFont().deriveFont(18.0f));
        buttonAdd.setSize(50, 50);
        buttonMin.setLocation(250, 300);
        buttonMin.setFont(buttonMin.getFont().deriveFont(18.0f));
        buttonMin.setSize(50, 50);
        buttonMul.setLocation(300, 300);
        buttonMul.setFont(buttonMul.getFont().deriveFont(18.0f));
        buttonMul.setSize(50, 50);
        buttonDiv.setLocation(350, 300);
        buttonDiv.setFont(buttonDiv.getFont().deriveFont(18.0f));
        buttonDiv.setSize(50, 50);
        add(buttonRoll);
        add(buttonAdd);
        add(buttonMin);
        add(buttonDiv);
        add(buttonMul);
    }


    @Override
    public void onPlayerStartRound(int player) {
        statusLabel.setText(String.format("[%s] Please roll the dice", PLAYER_NAMES[player]));
    }

    @Override
    public void onPlayerEndRound(int player) {

    }
}

package effects.audio.gui.menus;

import effects.audio.*;
import effects.audio.Window;
import effects.audio.effectoperations.EffectOperationManager;
import effects.audio.gui.GuiUtil;
import effects.audio.params.Parameter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

public class ParameterMenu extends JPanel implements KeyListener {
    public EffectType effectType;
    public File selectedFile;
    private final ArrayList<ParameterField> parameterFields = new ArrayList<>();

    private static final Color FIELD_BACKGROUND = new Color(248, 249, 250);
    private static final Color FIELD_BORDER      = new Color(206, 212, 218);
    private static final Color FIELD_FOCUS       = new Color(46, 125, 50);

    public ParameterMenu(EffectType effectType, File selectedFile) {
        super(new BorderLayout());
        this.effectType = effectType;
        this.selectedFile = selectedFile;

        setupLayout();
    }

    private void setupLayout() {
        JPanel mainCard = GuiUtil.buildCardLayout(this, 50, 60, 30);

        mainCard.add(createTitlePanel(),     BorderLayout.NORTH);
        mainCard.add(createParameterGrid(),  BorderLayout.CENTER);
        mainCard.add(createButtonPanel(),    BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Configure Parameters", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(27, 94, 32));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel subtitleLabel = new JLabel(
                String.format("Fine-tune settings for %s effect", effectType.getPrettyName()),
                SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(108, 117, 125));

        titlePanel.add(titleLabel,    BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.CENTER);

        return titlePanel;
    }

    private JPanel createParameterGrid() {
        JPanel gridContainer = new JPanel(new BorderLayout());
        gridContainer.setOpaque(false);

        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setOpaque(false);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 12, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        for (Parameter<?> parameter : effectType.getParameters()) {
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.3;
            gbc.anchor = GridBagConstraints.LINE_END;

            JLabel label = new JLabel(parameter.prettyName + ":");
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            label.setForeground(GuiUtil.TEXT_DARK);
            gridPanel.add(label, gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7;
            gbc.anchor = GridBagConstraints.LINE_START;

            JTextField field = createStyledTextField(parameter.value.toString());
            field.addKeyListener(this);
            parameterFields.add(new ParameterField(field, parameter));
            gridPanel.add(field, gbc);

            row++;
        }

        gridContainer.add(gridPanel, BorderLayout.CENTER);
        return gridContainer;
    }

    private JTextField createStyledTextField(String text) {
        JTextField field = new JTextField(text, 25) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(FIELD_BACKGROUND);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                if (hasFocus()) {
                    g2.setColor(FIELD_FOCUS);
                    g2.setStroke(new BasicStroke(2));
                } else {
                    g2.setColor(FIELD_BORDER);
                    g2.setStroke(new BasicStroke(1));
                }
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(GuiUtil.TEXT_DARK);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        field.setPreferredSize(new Dimension(300, 40));

        return field;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton backButton  = GuiUtil.createStyledButton("Back",         GuiUtil.PRIMARY_COLOR, GuiUtil.PRIMARY_HOVER);
        JButton applyButton = GuiUtil.createStyledButton("Apply Effect",  GuiUtil.SUCCESS_COLOR, GuiUtil.SUCCESS_HOVER);

        backButton.addActionListener(_ -> Window.setWindow(new MainMenu()));
        applyButton.addActionListener(_ -> {
            if (!updateParameterValues()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter valid values for all parameters.",
                        "Invalid Parameters",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            LoadingScreen screen = new LoadingScreen(effectType, selectedFile);
            Window.setWindow(screen);
            EffectOperationManager.init(screen);
        });

        buttonPanel.add(backButton);
        buttonPanel.add(applyButton);

        return buttonPanel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        GuiUtil.paintBackground(g, this::repaint, getWidth(), getHeight());
    }

    /** @return {@code false} if any field contains an unparseable value. */
    @SuppressWarnings("unchecked")
    private boolean updateParameterValues() {
        for (ParameterField pf : parameterFields) {
            Parameter<?> parameter = pf.parameter();
            String input = pf.field().getText();
            try {
                switch (parameter.valueClass.getSimpleName()) {
                    case "Integer" -> ((Parameter<Integer>) parameter).value = Integer.parseInt(input);
                    case "Float"   -> ((Parameter<Float>)   parameter).value = Float.parseFloat(input);
                    case "Boolean" -> ((Parameter<Boolean>) parameter).value = Boolean.parseBoolean(input);
                    case "Double"  -> ((Parameter<Double>)  parameter).value = Double.parseDouble(input);
                    case "String"  -> ((Parameter<String>)  parameter).value = input;
                    default -> throw new UnsupportedOperationException(
                            "Invalid parameter class: " + parameter.valueClass.getSimpleName());
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void keyTyped(KeyEvent event) {
        if (event.getKeyChar() == KeyEvent.VK_ESCAPE)
            Window.setWindow(new MainMenu());
    }

    @Override public void keyPressed(KeyEvent event)  {}
    @Override public void keyReleased(KeyEvent event) {}

    private record ParameterField(JTextField field, Parameter<?> parameter) {}
}
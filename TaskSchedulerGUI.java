package ui;

import model.Task;
import service.DeadlineMonitor;
import service.TaskDAO;
import service.TaskScheduler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TaskSchedulerGUI {

    private static final Color APP_BACKGROUND = new Color(240, 244, 248);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color PRIMARY = new Color(33, 91, 166);
    private static final Color PRIMARY_DARK = new Color(23, 65, 120);
    private static final Color TEXT_PRIMARY = new Color(36, 44, 56);
    private static final Color TEXT_SECONDARY = new Color(107, 119, 140);
    private static final Color BORDER = new Color(221, 228, 237);
    private static final Color SUCCESS = new Color(51, 140, 93);
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private static JTable table;
    private static DefaultTableModel model;
    private static JLabel totalTasksValue;
    private static JLabel pendingTasksValue;
    private static JLabel completedTasksValue;

    public static void loadTable() {
        model.setRowCount(0);

        TaskDAO dao = new TaskDAO();
        List<Task> taskList = dao.getAllTasks();

        int pendingCount = 0;
        int completedCount = 0;

        int priorityRank = 1;

        for (Task t : taskList) {
            String status = t.getStatus();

            if ("Completed".equalsIgnoreCase(status)) {
                completedCount++;
            } else {
                pendingCount++;
            }

            model.addRow(new Object[] {
                    t.getId(),
                    t.getTitle(),
                    priorityRank,
                    t.getDeadline() == null ? "-" : DISPLAY_FORMAT.format(t.getDeadline()),
                    t.getDuration() + " hrs",
                    status
            });

            priorityRank++;
        }

        totalTasksValue.setText(String.valueOf(taskList.size()));
        pendingTasksValue.setText(String.valueOf(pendingCount));
        completedTasksValue.setText(String.valueOf(completedCount));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            DeadlineMonitor monitor = new DeadlineMonitor();
            monitor.start();

            JFrame frame = new JFrame("Smart Task Scheduler");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(980, 640);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(createMainContent(frame));

            loadTable();
            frame.setVisible(true);
        });
    }

    private static JPanel createMainContent(JFrame frame) {
        JPanel root = new JPanel(new BorderLayout(20, 20));
        root.setBackground(APP_BACKGROUND);
        root.setBorder(new EmptyBorder(22, 22, 22, 22));

        root.add(createHeaderPanel(), BorderLayout.NORTH);
        root.add(createCenterPanel(frame), BorderLayout.CENTER);
        root.add(createFooterPanel(), BorderLayout.SOUTH);

        return root;
    }

    private static JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Smart Task Scheduler");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Plan deadlines, manage priorities, and track progress in one place.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_SECONDARY);

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.add(title);
        titleBlock.add(Box.createRigidArea(new Dimension(0, 6)));
        titleBlock.add(subtitle);

        JPanel summaryRow = new JPanel(new GridLayout(1, 3, 14, 0));
        summaryRow.setOpaque(false);

        totalTasksValue = new JLabel("0");
        pendingTasksValue = new JLabel("0");
        completedTasksValue = new JLabel("0");

        summaryRow.add(createStatCard("Total Tasks", totalTasksValue, PRIMARY));
        summaryRow.add(createStatCard("Pending", pendingTasksValue, new Color(214, 139, 30)));
        summaryRow.add(createStatCard("Completed", completedTasksValue, SUCCESS));

        header.add(titleBlock, BorderLayout.WEST);
        header.add(summaryRow, BorderLayout.EAST);

        return header;
    }

    private static JPanel createCenterPanel(JFrame frame) {
        JPanel center = new JPanel(new BorderLayout(18, 0));
        center.setOpaque(false);

        center.add(createActionPanel(frame), BorderLayout.WEST);
        center.add(createTablePanel(), BorderLayout.CENTER);

        return center;
    }

    private static JPanel createActionPanel(JFrame frame) {
        JPanel actions = createCardPanel();
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        actions.setPreferredSize(new Dimension(230, 0));

        JLabel panelTitle = new JLabel("Quick Actions");
        panelTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panelTitle.setForeground(TEXT_PRIMARY);

        JLabel panelHint = new JLabel("<html>     </html>");
        panelHint.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelHint.setForeground(TEXT_SECONDARY);

        JButton addTaskBtn = createAccentButton("Add New Task");
        JButton refreshBtn = createSecondaryButton("Refresh Tasks");
        JButton updateBtn = createSecondaryButton("Mark as Completed");
        JButton deleteBtn = createDangerButton("Delete Selected");

        actions.add(panelTitle);
        actions.add(Box.createRigidArea(new Dimension(0, 8)));
        actions.add(panelHint);
        actions.add(Box.createRigidArea(new Dimension(0, 24)));
        actions.add(addTaskBtn);
        actions.add(Box.createRigidArea(new Dimension(0, 12)));
        actions.add(refreshBtn);
        actions.add(Box.createRigidArea(new Dimension(0, 12)));
        actions.add(updateBtn);
        actions.add(Box.createRigidArea(new Dimension(0, 12)));
        actions.add(deleteBtn);
        actions.add(Box.createVerticalGlue());

        JLabel sideNote = new JLabel("<html><b>Tip:</b> Select a row first to delete or update status </html>");
        sideNote.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sideNote.setForeground(TEXT_SECONDARY);
        actions.add(sideNote);

        addTaskBtn.addActionListener(e -> showAddTaskDialog(frame));

        refreshBtn.addActionListener(e -> {
            loadTable();
            JOptionPane.showMessageDialog(frame, "Task list refreshed successfully.");
        });

        updateBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Please select a task row first.");
                return;
            }

            int id = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
            TaskDAO dao = new TaskDAO();
            dao.updateTaskStatus(id, "Completed");

            loadTable();
            JOptionPane.showMessageDialog(frame, "Task marked as completed.");
        });

        deleteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Please select a task row first.");
                return;
            }

            int id = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());

            int confirm = JOptionPane.showConfirmDialog(
                    frame,
                    "Are you sure to delete this task?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                TaskDAO dao = new TaskDAO();
                dao.deleteTask(id);
                loadTable();
                JOptionPane.showMessageDialog(frame, "Task deleted successfully.");
            }
        });

        return actions;
    }

    private static JPanel createTablePanel() {
        JPanel tableCard = createCardPanel();
        tableCard.setLayout(new BorderLayout(0, 16));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);

        JLabel title = new JLabel("Task Overview");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_PRIMARY);

        JLabel caption = new JLabel("Deadlines and current progress of all scheduled tasks");
        caption.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        caption.setForeground(TEXT_SECONDARY);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(title);
        titleBox.add(Box.createRigidArea(new Dimension(0, 4)));
        titleBox.add(caption);

        tableHeader.add(titleBox, BorderLayout.WEST);

        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.setColumnIdentifiers(new String[] {
                "ID",
                "Title",
                "Priority No.",
                "Deadline",
                "Duration",
                "Status"
        });

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT_PRIMARY);
        table.setGridColor(new Color(234, 238, 243));
        table.setSelectionBackground(new Color(222, 233, 247));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(245, 248, 252));
        table.getTableHeader().setForeground(TEXT_PRIMARY);
        table.getTableHeader().setReorderingAllowed(false);
        table.setDefaultRenderer(Object.class, new TaskTableRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);

        tableCard.add(tableHeader, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        return tableCard;
    }

    private static JPanel createFooterPanel() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        footer.setOpaque(false);

        JLabel footerText = new JLabel("Tasks nearing deadline will trigger automatic alerts and provide a pop up message");
        footerText.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        footerText.setForeground(TEXT_SECONDARY);

        footer.add(footerText);
        return footer;
    }

    private static JPanel createStatCard(String labelText, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(12, 14, 12, 14)
        ));
        card.setPreferredSize(new Dimension(145, 86));

        JPanel accentBar = new JPanel();
        accentBar.setBackground(accent);
        accentBar.setPreferredSize(new Dimension(8, 8));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(TEXT_SECONDARY);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(TEXT_PRIMARY);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(label);
        content.add(Box.createRigidArea(new Dimension(0, 6)));
        content.add(valueLabel);

        card.add(accentBar, BorderLayout.WEST);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(18, 18, 18, 18)
        ));
        return panel;
    }

    private static JButton createPrimaryButton(String text) {
        JButton button = baseButton(text);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        return button;
    }

    private static JButton createAccentButton(String text) {
        JButton button = baseButton(text);
        button.setBackground(new Color(228, 237, 248));
        button.setForeground(PRIMARY_DARK);
        return button;
    }

    private static JButton createSecondaryButton(String text) {
        JButton button = baseButton(text);
        button.setBackground(new Color(236, 241, 247));
        button.setForeground(TEXT_PRIMARY);
        return button;
    }

    private static JButton createDangerButton(String text) {
        JButton button = baseButton(text);
        button.setBackground(new Color(246, 231, 231));
        button.setForeground(new Color(145, 52, 52));
        return button;
    }

    private static JButton baseButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(new EmptyBorder(12, 16, 12, 16));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        return button;
    }

    private static void showAddTaskDialog(JFrame parentFrame) {
        JDialog dialog = new JDialog(parentFrame, "Add New Task", true);
        dialog.setSize(460, 420);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setLayout(new BorderLayout());
        dialog.setResizable(false);

        JPanel formCard = createCardPanel();
        formCard.setLayout(new BorderLayout(0, 18));
        formCard.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel formContent = new JPanel();
        formContent.setOpaque(false);
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("Create Task");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        heading.setForeground(TEXT_PRIMARY);

        JLabel description = new JLabel("Enter task details below.");
        description.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        description.setForeground(TEXT_SECONDARY);

        JTextField titleField = new JTextField();
        JTextField durationField = new JTextField();
        JTextField deadlineField = new JTextField();
        JComboBox<String> statusBox = new JComboBox<>(new String[] {"Pending", "Completed"});

        formContent.add(heading);
        formContent.add(Box.createRigidArea(new Dimension(0, 4)));
        formContent.add(description);
        formContent.add(Box.createRigidArea(new Dimension(0, 18)));
        formContent.add(createFormField("Task Title", titleField));
        formContent.add(Box.createRigidArea(new Dimension(0, 12)));
        formContent.add(createFormField("Duration (hours)", durationField));
        formContent.add(Box.createRigidArea(new Dimension(0, 12)));
        formContent.add(createFormField("Deadline (yyyy-MM-dd HH:mm)", deadlineField));
        formContent.add(Box.createRigidArea(new Dimension(0, 12)));
        formContent.add(createFormField("Initial Status", statusBox));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton cancelButton = createSecondaryButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(100, 40));

        JButton saveButton = createPrimaryButton("Submit Task");
        saveButton.setPreferredSize(new Dimension(130, 40));

        actions.add(cancelButton);
        actions.add(saveButton);

        formCard.add(formContent, BorderLayout.CENTER);
        formCard.add(actions, BorderLayout.SOUTH);

        cancelButton.addActionListener(e -> dialog.dispose());

        saveButton.addActionListener(e -> {
            try {
                String taskTitle = titleField.getText().trim();
                String durationText = durationField.getText().trim();
                String deadlineText = deadlineField.getText().trim();
                String status = statusBox.getSelectedItem().toString();

                if (taskTitle.isEmpty() || durationText.isEmpty() || deadlineText.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "All fields are required.");
                    return;
                }

                int duration;
                try {
                    duration = Integer.parseInt(durationText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Duration must be a valid number.");
                    return;
                }

                if (duration <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Duration should be greater than 0.");
                    return;
                }

                LocalDateTime deadline;
                try {
                    deadline = LocalDateTime.parse(deadlineText.replace(" ", "T"));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid deadline format. Use yyyy-MM-dd HH:mm");
                    return;
                }

                if (deadline.isBefore(LocalDateTime.now())) {
                    JOptionPane.showMessageDialog(dialog, "Past deadline is not allowed.");
                    return;
                }

                TaskScheduler scheduler = new TaskScheduler();
                int priority = scheduler.calculatePriority(deadline);

                Task task = new Task();
                task.setTitle(taskTitle);
                task.setDuration(duration);
                task.setDeadline(deadline);
                task.setPriority(priority);
                task.setStatus(status);
                task.setUserId(1);

                TaskDAO dao = new TaskDAO();
                dao.addTask(task);

                loadTable();
                JOptionPane.showMessageDialog(dialog, "Task saved successfully.");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Unexpected error occurred while saving the task.");
                ex.printStackTrace();
            }
        });

        dialog.setContentPane(formCard);
        dialog.setVisible(true);
    }

    private static JPanel createFormField(String labelText, Component field) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 6));
        wrapper.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXT_PRIMARY);

        if (field instanceof JTextField) {
            ((JTextField) field).setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }

        if (field instanceof JComboBox) {
            field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }

        field.setPreferredSize(new Dimension(0, 34));

        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(field, BorderLayout.CENTER);
        return wrapper;
    }

    private static class TaskTableRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            Component component = super.getTableCellRendererComponent(
                    table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column
            );

            setBorder(new EmptyBorder(0, 10, 0, 10));

            if (isSelected) {
                component.setBackground(new Color(222, 233, 247));
                component.setForeground(TEXT_PRIMARY);
            } else {
                component.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 251, 253));
                component.setForeground(TEXT_PRIMARY);
            }

            if (column == 2) {
                setHorizontalAlignment(SwingConstants.CENTER);
                component.setForeground(TEXT_PRIMARY);
            } else if (column == 5) {
                setHorizontalAlignment(SwingConstants.CENTER);
                String status = value == null ? "" : value.toString();
                if ("Completed".equalsIgnoreCase(status)) {
                    component.setForeground(SUCCESS);
                } else {
                    component.setForeground(PRIMARY_DARK);
                }
            } else if (column == 0 || column == 4) {
                setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                setHorizontalAlignment(SwingConstants.LEFT);
            }

            return component;
        }
    }
}

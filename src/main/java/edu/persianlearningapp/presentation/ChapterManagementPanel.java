package edu.persianlearningapp.presentation;

import edu.persianlearningapp.business.BusinessException;
import edu.persianlearningapp.business.ChapterBO;
import edu.persianlearningapp.domain.Chapter;
import edu.persianlearningapp.observer.LearningEventSource;
import edu.persianlearningapp.observer.Observable;
import edu.persianlearningapp.observer.Observer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class ChapterManagementPanel extends JPanel implements Observer {

    private final ChapterBO chapterBO;
    private final DefaultListModel<Chapter> chapterModel = new DefaultListModel<Chapter>();
    private final JList<Chapter> chapterList = new JList<Chapter>(chapterModel);

    public ChapterManagementPanel( ChapterBO chapterBO, LearningEventSource eventSource) {
        this.chapterBO = chapterBO;

        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel("ابواب کا انتظام", JLabel.CENTER);
        add(heading, BorderLayout.NORTH);

        chapterList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean selected, boolean focused) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, selected, focused);

                Chapter chapter = (Chapter) value;
                label.setText(chapter.getId() + "  |  " + chapter.getTitle());
                label.setComponentOrientation(
                        ComponentOrientation.RIGHT_TO_LEFT);
                return label;
            }
        });

        add(new JScrollPane(chapterList), BorderLayout.CENTER);
        add(createButtons(), BorderLayout.SOUTH);

        applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        eventSource.addObserver(this);
        loadChapters();
    }

    private JPanel createButtons() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton createButton = new JButton("نیا باب");
        JButton viewButton = new JButton("باب دیکھیں");
        JButton renameButton = new JButton("عنوان تبدیل کریں");
        JButton deleteButton = new JButton("حذف کریں");
        JButton refreshButton = new JButton("فہرست تازہ کریں");

        createButton.addActionListener(event -> createChapter());
        viewButton.addActionListener(event -> viewChapter());
        renameButton.addActionListener(event -> renameChapter());
        deleteButton.addActionListener(event -> deleteChapter());
        refreshButton.addActionListener(event -> loadChapters());

        buttons.add(createButton);
        buttons.add(viewButton);
        buttons.add(renameButton);
        buttons.add(deleteButton);
        buttons.add(refreshButton);

        return buttons;
    }

    private void createChapter() {
        String title = JOptionPane.showInputDialog(this, "باب کا فارسی عنوان درج کریں:", "نیا باب", JOptionPane.PLAIN_MESSAGE);

        if (title == null) {
            return;
        }

        try {
            int chapterId = chapterBO.createChapter(title);
            showMessage("باب محفوظ ہوگیا۔ شناخت: " + chapterId);
        } catch (BusinessException e) {
            showError("عنوان خالی یا ۲۵۵ حروف سے زیادہ نہیں ہونا چاہیے۔");
        } catch (RuntimeException e) {
            showError("باب محفوظ نہیں ہو سکا۔ ڈیٹا بیس کا کنکشن چیک کریں۔");
        }
    }

    private void viewChapter() {
        Chapter selected = selectedChapter();
        if (selected == null) {
            return;
        }

        try {
            Chapter chapter = chapterBO.getChapter(selected.getId());
            showMessage("باب نمبر: " + chapter.getId()
                    + "\nعنوان: " + chapter.getTitle()
                    + "\nتخلیق کی تاریخ: " + chapter.getCreatedAt()
                    + "\nآخری ترمیم: " + chapter.getUpdatedAt());
        } catch (RuntimeException e) {
            showError("باب کی تفصیل نہیں کھل سکی۔");
        }
    }

    private void renameChapter() {
        Chapter selected = selectedChapter();
        if (selected == null) {
            return;
        }

        String title = JOptionPane.showInputDialog(
                this, "نیا فارسی عنوان درج کریں:", selected.getTitle());

        if (title == null) {
            return;
        }

        try {
            chapterBO.renameChapter(selected.getId(), title);
            showMessage("باب کا عنوان تبدیل ہوگیا۔");
        } catch (BusinessException e) {
            showError("عنوان خالی یا ۲۵۵ حروف سے زیادہ نہیں ہونا چاہیے۔");
        } catch (RuntimeException e) {
            showError("عنوان تبدیل نہیں ہو سکا۔");
        }
    }

    private void deleteChapter() {
        Chapter selected = selectedChapter();
        if (selected == null) {
            return;
        }

        Object[] options = {"حذف کریں", "منسوخ کریں"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "کیا آپ یہ باب حذف کرنا چاہتے ہیں؟ اس کے اجزاء اور لغت بھی حذف ہوں گے۔",
                "باب حذف کریں",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[1]);

        if (choice != 0) {
            return;
        }

        try {
            chapterBO.deleteChapter(selected.getId(), true);
            showMessage("باب حذف ہوگیا۔");
        } catch (RuntimeException e) {
            showError("باب حذف نہیں ہو سکا۔");
        }
    }

    private Chapter selectedChapter() {
        Chapter selected = chapterList.getSelectedValue();
        if (selected == null) {
            showError("پہلے فہرست سے ایک باب منتخب کریں۔");
        }
        return selected;
    }

    private void loadChapters() {
        try {
            List<Chapter> chapters = chapterBO.listChapters();
            chapterModel.clear();

            for (Chapter chapter : chapters) {
                chapterModel.addElement(chapter);
            }
        } catch (RuntimeException e) {
            showError("ابواب کی فہرست نہیں کھل سکی۔ ڈیٹا بیس چیک کریں۔");
        }
    }

    @Override
    public void update(Observable subject) {
        if (SwingUtilities.isEventDispatchThread()) {
            loadChapters();
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    loadChapters();
                }
            });
        }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(
                this, message, "فارسی سیکھنے کی ایپ",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this, message, "خرابی",
                JOptionPane.ERROR_MESSAGE);
    }
}
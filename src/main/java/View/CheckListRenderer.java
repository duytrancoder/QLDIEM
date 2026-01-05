package View;

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class CheckListRenderer extends JCheckBox implements ListCellRenderer<CheckListItem> {
    @Override
    public Component getListCellRendererComponent(JList<? extends CheckListItem> list, CheckListItem value,
            int index, boolean isSelected, boolean cellHasFocus) {
        setEnabled(list.isEnabled());
        setSelected(value.isSelected());
        setFont(list.getFont());
        setBackground(list.getBackground());
        setForeground(list.getForeground());
        setText(value.toString());
        return this;
    }
}

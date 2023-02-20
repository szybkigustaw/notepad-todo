import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class DatePicker {
    int month = java.util.Calendar.getInstance().get(Calendar.MONTH);
    int year = java.util.Calendar.getInstance().get(Calendar.YEAR);
    JLabel l = new JLabel("", JLabel.CENTER);
    String day = "";
    JDialog d;
    JButton[] button = new JButton[49];

    public DatePicker(JFrame parent){
        d = new JDialog();
        d.setModal(true);
        String[] header = { "Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat" };
        JPanel p1 = new JPanel(new GridLayout(7,7));
        p1.setPreferredSize(new Dimension(430, 120));
        for(int x = 0; x < button.length; x++){
            final int selection = x;
            button[x] = new JButton();
            button[x].setFocusPainted(false);
            button[x].setBackground(Color.white);
            if(x > 6) {
                button[x].addActionListener(e -> {
                    day = button[selection].getActionCommand();
                    d.dispose();
                });
            }
            if(x < 7){
                button[x].setText(header[x]);
                button[x].setForeground(Color.red);
            }
            p1.add(button[x]);
        }
        JPanel p2 = new JPanel(new GridLayout(1,3));
        JButton prev = new JButton("<<");
        prev.addActionListener(e -> {
            month--;
            displayDate();
        });
        p2.add(prev);

        p2.add(l);

        JButton next = new JButton(">>");
        next.addActionListener(e -> {
            month++;
            displayDate();
        });
        p2.add(next);

        d.add(p1, BorderLayout.CENTER);
        d.add(p2, BorderLayout.CENTER);
        d.pack();
        d.setLocationRelativeTo(parent);
        d.setVisible(true);

    }

    public void displayDate(){
        for(int x = 7, x < button.length; x++){
            button[x].setText("");
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, 1);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int x = 6 + dayOfWeek, day = 1; day <= daysInMonth; x++, day++){
                button[x].setText("" + day);
            }
            l.setText(sdf.format(cal.getTime()));
            d.setTitle("Date Picker");
        }
    }

    public String setPickedDate(){
        if(day.equals("")) return day;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, Integer.parseInt(day));
        return sdf.format(cal.getTime());
    }
}

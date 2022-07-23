package Pages;

import ClientSide.ClientConfig;
import ClientSide.DataHandler;
import Listeners.ExitListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

public class MainPage extends JPanel {
    int width = ClientConfig.mainFrameWidth;
    int height = ClientConfig.mainFrameHeight;

    public JMenuBar jMenuBar;
    public JMenu registration;
    public JMenu eduService;
    public JMenu gradeService;
    public JMenu userProfile;
    public JButton mainPage;
    public JMenuItem lessonsList;
    public JMenuItem teachersList;
    public JMenuItem weeklyPlan;
    public JMenuItem examsList;
    public JMenu requests;

    public Timer timer;
    public JLabel showTime;
    public JButton exit;
    public JLabel lastLoginTime;
    public JLabel imageIcon;
    public JLabel name;
    public JLabel email;

    public MainPage() {
        initPanel();
        initCom();
        align();
        addListener();
        GuiController.getInstance().addJPanel(this);
    }

    public void initPanel() {

        this.setBounds(0, 0, width, height);
        this.setVisible(true);
        this.setLayout(null);

    }
    public void initCom(){
        showTime = new JLabel();
        timer = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showTime.setText(new Date().toString());
            }
        });
        timer.start();

        exit = new JButton("EXIT");
        lastLoginTime = new JLabel(DataHandler.getInstance().getLastLoginTime());
        imageIcon = new JLabel(DataHandler.getInstance().getImageIcon());
        name = new JLabel("yourName : " + DataHandler.getInstance().getFirstname());
        email = new JLabel("yourEmail : " +DataHandler.getInstance().getEmail());

        //menu items
        jMenuBar = new JMenuBar();
        registration = new JMenu("REGISTRATION");
        gradeService = new JMenu("GRADE SERVICE");
        eduService = new JMenu("EDU SERVICE");
        weeklyPlan = new JMenuItem("WEEKLY PLAN");
        eduService.add(weeklyPlan);
        examsList = new JMenuItem("EXAMS");
        eduService.add(examsList);
        requests = new JMenu("REQUESTS");
        eduService.add(requests);
        userProfile = new JMenu("PROFILE");
        mainPage = new JButton("MAIN PANEL");
        teachersList = new JMenuItem("TEACHERS");
        lessonsList =new JMenuItem("LESSONS");
        registration.add(lessonsList);
        registration.add(teachersList);
        jMenuBar.add(gradeService);
        jMenuBar.add(eduService);
        jMenuBar.add(userProfile);
        jMenuBar.add(registration);
        


    }
    public void align(){
        mainPage.setBounds(ClientConfig.mainFrameWidth - 500,0,150,30);
        this.add(mainPage);
        jMenuBar.setBounds(80,0,ClientConfig.mainFrameWidth - 200,30);
        this.add(jMenuBar);
        showTime.setBounds(0,35,200,30);
        this.add(showTime);
        exit.setBounds(0,0,80,30);
        this.add(exit);
        imageIcon.setBounds(0,70,100,100);
        this.add(imageIcon);
        name.setBounds(0,200,200,30);
        this.add(name);
        email.setBounds(0,230,300,30);
        this.add(email);
    }
    public void addListener(){
        //
        ExitListener exitListener = new ExitListener();
        exit.addActionListener(exitListener);

        //
    }
}
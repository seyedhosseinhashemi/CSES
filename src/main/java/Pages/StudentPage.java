package Pages;

import ClientSide.DataHandler;
import Listeners.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class StudentPage extends MainPage{
    public JTable studyStatus;
    public JLabel showStatus;

    public JMenuItem studyEvidenceReq;
    public JMenuItem minorReq;
    public JMenuItem leaveReq;
    public JMenuItem recommendReq;
    public JMenuItem temporaryGrades;
    public JMenuItem eduStatus;
    public JMenuItem userProfileMenu;
    public JMenuItem eduSchedule;
    public JMenu cw;
    public JMenu myLessons;
    JMenuItem takeLesson;


    public StudentPage() {
        super();
        DataHandler.getInstance().updateUserLessons();
        DataHandler.getInstance().updateCwLessonsEduSubject();
        DataHandler.getInstance().updateHomeWorks();
        DataHandler.getInstance().updateUploadedHm();
        initTable();
        initMenubar();
        addMoreListeners();
    }




    private void initTable() {
        showStatus = new JLabel("education status");
        showStatus.setBounds(200,170,250,30);
        this.add(showStatus);
        String educationStatus = DataHandler.getInstance().getEducationStatus();
        String supervisorName = DataHandler.getInstance().getSupervisorName();
        String signupPermit = "NAMALOOM";
        if (DataHandler.getInstance().isSignUpPermit()) {
            signupPermit = "TRUE";
        } else {
            signupPermit = "FALSE";
        }
        String signupTime = DataHandler.getInstance().getSignupTime();

        String data[][] = {
                {"EDU STATUS :", educationStatus},
                {"SUPERVISOR :",supervisorName},
                {"SIGNUP PERMIT :",signupPermit},
                {"SIGNUP TIME :",signupTime}
        };

        String column[] = {" EDU STATUS "," _ "};
        studyStatus = new JTable(data,column);
        studyStatus.setBounds(200,200,500,70);
        JScrollPane jScrollPane = new JScrollPane(studyStatus);
        this.add(studyStatus);
    }
    private void initMenubar() {
        eduStatus = new JMenuItem("EDU STATUS");
        temporaryGrades = new JMenuItem("TEMPORARY GRADES LIST");
        userProfileMenu = new JMenuItem("PROFILE");
        leaveReq = new JMenuItem("LEAVE REQ");
        minorReq = new JMenuItem("MINOR");
        studyEvidenceReq = new JMenuItem("STUDY EVIDENCE");
        recommendReq = new JMenuItem("RECOMMEND REQ");
        takeLesson = new JMenuItem("CHOOSE LESSON");
        cw = new JMenu("COURSE WARE");
        myLessons = new JMenu("MY LESSONS");
        eduSchedule = new JMenuItem("EDU SCHEDULE");
        //
        initCWMyLesson();
        //
        cw.add(myLessons);
        cw.add(eduSchedule);
        requests.add(leaveReq);
        requests.add(recommendReq);
        requests.add(minorReq);
        requests.add(studyEvidenceReq);
        gradeService.add(temporaryGrades);
        gradeService.add(eduStatus);
        userProfile.add(userProfileMenu);
        eduService.add(takeLesson);
        jMenuBar.add(cw);
    }
    public void initCWMyLesson(){
        //
        for (List<String> i:
                DataHandler.getInstance().getUserLessons()) {
            JMenuItem jMenuItem = new JMenuItem(i.get(1));
            boolean isContained = false;
            for (Component j:
                    myLessons.getMenuComponents()) {
                if (j instanceof JMenuItem){
                    if (((JMenuItem) j).getText().equals(jMenuItem.getText())){
                        isContained = true;
                    }
                }
            }
            if (!isContained){
                jMenuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        StudentCwLessonPage studentCwLessonPage = new StudentCwLessonPage(jMenuItem.getText());
                        GuiController.getInstance().userCurrentPanel = studentCwLessonPage;
                        GuiController.getInstance().getFrame().add(studentCwLessonPage,1);
                        repaint();
                        revalidate();
                    }
                });
                myLessons.add(jMenuItem);
            }
        }
        //
    }

    private void addMoreListeners() {
        StudentRecReqListener studentRecReqListener = new StudentRecReqListener();
        recommendReq.addActionListener(studentRecReqListener);
        studyEvidenceReq.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiController.getInstance().changePanelTo(PanelType.STUDYEVIDENCEREQPAGE);
            }
        });
        MinorReqListener minorReqListener = new MinorReqListener();
        minorReq.addActionListener(minorReqListener);
        LeaveReqListener leaveReqListener = new LeaveReqListener();
        leaveReq.addActionListener(leaveReqListener);
        TemporaryGradesListener temporaryGradesListener = new TemporaryGradesListener();
        temporaryGrades.addActionListener(temporaryGradesListener);
        StudentEduStatusListener studentEduStatusListener = new StudentEduStatusListener();
        eduStatus.addActionListener(studentEduStatusListener);

        userProfileMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiController.getInstance().changePanelTo(PanelType.STUDENT_PROFILE_PAGE);
            }
        });
        takeLesson.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiController.getInstance().changePanelTo(PanelType.TAKE_LESSON_PAGE);
            }
        });
        eduSchedule.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiController.getInstance().changePanelTo(PanelType.CW_SCHEDULE_PAGE);
            }
        });
    }
}

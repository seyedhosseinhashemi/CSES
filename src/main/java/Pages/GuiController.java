package Pages;

import ClientSide.Client;

import javax.swing.*;
import java.util.ArrayList;

public class GuiController {
    Client client;
    public JOptionPane jOptionPane;
    public  JFrame frame;
    public JPanel userMainPanel;
    public JPanel userCurrentPanel;

    public  ArrayList<JPanel> jPanels = new ArrayList<>();


    public static GuiController guiController;

    private GuiController() {
        jOptionPane = new JOptionPane();
    }

    public static GuiController getInstance() {
        if (guiController == null) {
            guiController = new GuiController();
        }
        return guiController;
    }



    public void resetJPanels() {
        for (JPanel i :
                jPanels) {
            i.setVisible(false);
        }
        jPanels.clear();

    }
    public void changePanelTo(PanelType panelType){
        resetJPanels();
        switch (panelType){
            case STUDENTMAINPAGE  :
                NormalStudentPage normalStudentPage = new NormalStudentPage();
                userMainPanel = normalStudentPage;
                userCurrentPanel = normalStudentPage;
                frame.add(normalStudentPage);
                break;
            case TEACHERMAINPAGE:
                TeacherPage teacherPage = new TeacherPage();
                userMainPanel = teacherPage;
                userCurrentPanel = teacherPage;
                frame.add(teacherPage);
                break;
            case LESSONSLISTPAGE:
                LessonsListPage lessonsListPage = new LessonsListPage();
                userCurrentPanel = lessonsListPage;
                frame.add(lessonsListPage,1);
                break;
            case TEACHERSLISTPAGE:
                TeachersListPage teachersListPage = new TeachersListPage();
                userCurrentPanel = teachersListPage;
                frame.add(teachersListPage ,1);
                break;
            case WEEKLYPLANPAGE:
                WeeklyPlanPage weeklyPlanPage = new WeeklyPlanPage();
                userCurrentPanel = weeklyPlanPage;
                frame.add(weeklyPlanPage,1);
                break;
            case EXAMSLISTPAGE:
                ExamsListPage examsListPage = new ExamsListPage();
                userCurrentPanel =examsListPage;
                frame.add(examsListPage,1);
                break;
            case STUDENTRECREQPAGE:
                StudentRecReqPage studentRecReqPage = new StudentRecReqPage();
                userCurrentPanel = studentRecReqPage;
                frame.add(studentRecReqPage,1);
                break;
            case STUDYEVIDENCEREQPAGE:
                StudyEvidenceReqPage studyEvidenceReqPage = new StudyEvidenceReqPage();
                userCurrentPanel = studyEvidenceReqPage;
                frame.add(studyEvidenceReqPage , 1);
                break;
            case MINORREQPAGE:
                MinorReqPage minorReqPage = new MinorReqPage();
                userCurrentPanel = minorReqPage;
                frame.add(minorReqPage,1);
                break;
        }
        updateFrame();
    }
    public void exitButton(){
        resetJPanels();
        userMainPanel.setVisible(false);
        LoginPage loginPage = new LoginPage();
        frame.add(loginPage);
        updateFrame();
    }
    public void goToMainPanel(){
        resetJPanels();
        getFrame().add(getUserMainPanel() ,1);
        updateFrame();
    }

    public void addJPanel(JPanel jPanel) {
        jPanels.add(jPanel);
    }


    public  JFrame getFrame() {
        return frame;
    }

    public  void setFrame(JFrame frame) {
        this.frame = frame;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
    public void updateFrame(){
        frame.repaint();
        frame.revalidate();
        if (!(userMainPanel == null)){
            userMainPanel.repaint();
            userMainPanel.revalidate();
        }
        if (!(userCurrentPanel == null)){
            userCurrentPanel.repaint();
            userCurrentPanel.revalidate();
        }
    }

    public JOptionPane getjOptionPane() {
        return jOptionPane;
    }

    public void setjOptionPane(JOptionPane jOptionPane) {
        this.jOptionPane = jOptionPane;
    }

    public JPanel getUserMainPanel() {
        return userMainPanel;
    }

    public void setUserMainPanel(JPanel userMainPanel) {
        this.userMainPanel = userMainPanel;
    }
}

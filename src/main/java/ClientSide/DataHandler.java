package ClientSide;

import ClientSources.ImageResource;
import ClientSources.ResourceManager;
import Pages.GuiController;
import Pages.PanelType;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataHandler {
    String userName;
    String passWord;
    String token;
    //
    int id;
    int nc;
    String fullName;
    String firstname;
    String lastname;
    String relation;
    String email;
    String phoneNumber;
    String college;
    String lastLoginTime;
    ImageIcon imageIcon;
    //student
    String eduLevel;
    String educationStatus;
    String supervisorName;
    private String sp;
    boolean signUpPermit;
    String signupTime;
    double averageGrade;
    String entranceYear;
    //senior student
    String dormitoryReq = "FALSE";
    //teacher
    String roomNumber;
    String teacherLevel;

    List<List<String>> allLessons;
    List<List<String>> allTeachers;
    List<List<String>> userLessons;
    List<List<String>> recommendsList;
    List<List<String>> minorReqList;
    List<List<String>> leaveReqList;
    List<List<String>> userTemporaryGradesList;
    List<List<String>> allStudents;
    List<List<String>> recommendedLessonsList;
    List<List<String>> markedLessons;
    List<List<String>> reqMessages;
    List<List<String>> chats;
    List<List<String>> availablePeople;
    List<List<String>> cwLessonsEduSubjects;
    List<List<String>> homeWorks;
    List<List<String>> uploadedHm;
    private static DataHandler dataHandler;
    private DataHandler(){
        allLessons = new ArrayList<>();
        allTeachers = new ArrayList<>();
        userLessons = new ArrayList<>();
        recommendsList = new ArrayList<>();
        minorReqList = new ArrayList<>();
        leaveReqList = new ArrayList<>();
        userTemporaryGradesList = new ArrayList<>();
        allStudents = new ArrayList<>();
        recommendedLessonsList = new ArrayList<>();
        markedLessons = new ArrayList<>();
        reqMessages = new ArrayList<>();
        chats = new ArrayList<>();
        availablePeople = new ArrayList<>();
        cwLessonsEduSubjects = new ArrayList<>();
        homeWorks = new ArrayList<>();
        uploadedHm = new ArrayList<>();
        //
        GuiUpdater guiUpdater = new GuiUpdater();
        new Thread(guiUpdater).start();
        //
    }
    synchronized public static DataHandler getInstance(){
        if (dataHandler == null){
            dataHandler = new DataHandler();
        }
        return dataHandler;
    }
    public void analyzeOrder(List<String> orders){
        if (orders.get(0).equals(ClientReqType.LOGIN.toString())){
            DataHandler.getInstance().checkLoginRes(orders);
        }else if (orders.get(0).equals(ClientReqType.GETLESSONSLIST.toString())){
            initLessonsList(orders);
        }else if (orders.get(0).equals(ClientReqType.GETTEACHERSLIST.toString())){
            initTeachersList(orders);
        }else if (orders.get(0).equals(ClientReqType.GETUSERLESSONS.toString())){
            initUserLessons(orders);
        }else if (orders.get(0).equals(ClientReqType.GETRECOMMENDLIST.toString())){
            initRecommendList(orders);
        }else if (orders.get(0).equals(ClientReqType.RECOMMENDREQ.toString())){
            showRecReqResult(orders);
        }else if (orders.get(0).equals(ClientReqType.MINORREQLIST.toString())){
            initMinorReqList(orders);
        } else if (orders.get(0).equals(ClientReqType.MINORREQ.toString())){
            showMinorReqResult(orders);
        }else if (orders.get(0).equals(ClientReqType.LEAVEREQ.toString())){
            showLeaveReqResult(orders);
        }else if (orders.get(0).equals(ClientReqType.LEAVEREQLIST.toString())){
            initLeaveReqList(orders);
        }else if (orders.get(0).equals(ClientReqType.TEMPORARYGRADESLIST.toString())){
            initTemporaryGradesList(orders);
        }else if (orders.get(0).equals(ClientReqType.SHOW_RESULT.toString())){
            showResult(orders);
        }else if (orders.get(0).equals(ClientReqType.GET_STUDENTS_LIST.toString())){
            initStudentsList(orders);
        }else if (orders.get(0).equals(ClientReqType.GET_RECOMMENDED_LESSONS.toString())){
            initRecommendedLessonsList(orders);
        }else if (orders.get(0).equals(ClientReqType.GET_REQ_MESSAGES.toString())){
            initReqMessageList(orders);
        }else if (orders.get(0).equals(ClientReqType.GET_CHATS.toString())){
            initChatsList(orders);
        }else if (orders.get(0).equals(ClientReqType.DOWNLOAD_FILE.toString())){
            downloadFile(orders);
        }else if (orders.get(0).equals(ClientReqType.GET_AVAILABLE_PEOPLE.toString())){
            initAvailablePeopleList(orders);
        }else if (orders.get(0).equals(ClientReqType.GET_CW_EDU_SUBJECTS.toString())){
            initCwEduSubjects(orders);
        }else if (orders.get(0).equals(ClientReqType.GET_HM.toString())){
            initHomeWorks(orders);
        }else if (orders.get(0).equals(ClientReqType.GET_UPLOADED_HM.toString())){
            initUploadedHmList(orders);
        }else if (orders.get(0).equals(ClientReqType.SET_TOKEN.toString())){
            setToken(orders);
        }
    }



    private void showRecReqResult(List<String> orders) {
        if (orders.get(1).equals(ServerRespondType.SUCCESSFUL.toString())){
            GuiController.getInstance().getjOptionPane().showMessageDialog(null,"YOUR REQ HAS BEEN SAVED SUCCESSFULLY!");
        }else {
            GuiController.getInstance().getjOptionPane().showMessageDialog(null,"TEACHER ID HAS NOT FOUND!");
        }
    }


    public void checkLoginRes(List<String> orders){
        if (orders.get(1).equals(ServerRespondType.SUCCESSFUL.toString())){
            GuiController.getInstance().jOptionPane.showMessageDialog(null,"YOU HAD LOGEDIN SUCCESSFULY");
            this.id =Integer.parseInt(orders.get(2));
            this.nc = Integer.parseInt(orders.get(3));
            this.firstname = orders.get(4);
            this.lastname = orders.get(5);
            this.fullName = firstname + " " + lastname;
            this.relation = orders.get(6);
            this.email = orders.get(7);
            this.phoneNumber = orders.get(8);
            this.college = orders.get(9);
            this.lastLoginTime = orders.get(10);
            switch (this.relation){
                case "D":
                    this.educationStatus = orders.get(11);
                    this.supervisorName = orders.get(12);
                    this.sp = orders.get(13);
                    this.signupTime = orders.get(14);
                    try {
                        this.averageGrade = Double.parseDouble(orders.get(15));
                    }catch (Exception e){
                        this.averageGrade = 0.0;
                    }
                    this.entranceYear = orders.get(16);
                    this.eduLevel = orders.get(17);
                    if (eduLevel.equals("DA")){
                        GuiController.getInstance().changePanelTo(PanelType.SENIOR_STUDENT_MAIN_PAGE);
                    } else if (eduLevel.equals("DD")){
                        GuiController.getInstance().changePanelTo(PanelType.DOCTOR_STUDENT_MAIN_PAGE);
                    } else {
                        GuiController.getInstance().changePanelTo(PanelType.STUDENTMAINPAGE);
                    }

                    break;
                case "O":
                    this.teacherLevel = orders.get(11);
                    this.roomNumber = orders.get(12);
                    GuiController.getInstance().changePanelTo(PanelType.TEACHERMAINPAGE);
                    break;
                case "M":
                    this.teacherLevel = orders.get(11);
                    this.roomNumber = orders.get(12);
                    GuiController.getInstance().changePanelTo(PanelType.EDU_ASSISTANT_MAIN_PAGE);
                    break;
                case "R":
                    this.teacherLevel = orders.get(11);
                    this.roomNumber = orders.get(12);
                    GuiController.getInstance().changePanelTo(PanelType.EDU_MANAGER_MAIN_PAGE);
                    break;
                case "E":
                    GuiController.getInstance().changePanelTo(PanelType.MR_MOHSENI_MAIN_PAGE);
                    break;
                case "A":
                    GuiController.getInstance().changePanelTo(PanelType.EDU_ADMIN_MAIN_PAGE);
            }

        }else {
            GuiController.getInstance().jOptionPane.showMessageDialog(null,"USERNAME OR PASSWORD IS WRONG!");
        }
    }
    public void initLessonsList(List<String> orders){
        orders.remove(0);
        allLessons = new ArrayList<>();
        for (String i:
             orders) {
            if (i.equals(ServerRespondType.SUCCESSFUL.toString())){
                allLessons.add(new ArrayList<>());
            }else{
                allLessons.get(allLessons.size() - 1).add(i);
            }
        }


    }
    public void initTeachersList(List<String> orders){
        orders.remove(0);
        allTeachers = new ArrayList<>();
        for (String i:
                orders) {
            if (i.equals(ServerRespondType.SUCCESSFUL.toString())){
                allTeachers.add(new ArrayList<>());
            }else{
                allTeachers.get(allTeachers.size() - 1).add(i);
            }
        }
    }
    public void initUploadedHmList(List<String> orders){
        orders.remove(0);
        uploadedHm = new ArrayList<>();
        for (String i:
                orders) {
            if (i.equals(ServerRespondType.SUCCESSFUL.toString())){
                uploadedHm.add(new ArrayList<>());
            }else{
                uploadedHm.get(uploadedHm.size() - 1).add(i);
            }
        }
    }
    public void initRecommendedLessonsList(List<String> orders){
        orders.remove(0);
        recommendedLessonsList = new ArrayList<>();
        for (String i:
                orders) {
            if (i.equals(ServerRespondType.SUCCESSFUL.toString())){
                recommendedLessonsList.add(new ArrayList<>());
            }else{
                recommendedLessonsList.get(recommendedLessonsList.size() - 1).add(i);
            }
        }
    }
    private void initChatsList(List<String> orders) {
        orders.remove(0);
        chats = new ArrayList<>();
        for (String i:
                orders) {
            if (i.equals(ServerRespondType.SUCCESSFUL.toString())){
                chats.add(new ArrayList<>());
            }else{
                chats.get(chats.size() - 1).add(i);
            }
        }
        for (List<String> i:
             chats) {
            int j = i.get(2).lastIndexOf('.');
            if (i.get(2).substring(j+1).equals("msg")){
              i.set(2,i.get(2).replace(".msg",""));
            }
        }
        Collections.reverse(chats);
    }
    public void initCwEduSubjects(List<String> orders){
        orders.remove(0);
        cwLessonsEduSubjects = new ArrayList<>();
        for (String i:
                orders) {
            if (i.equals(ServerRespondType.SUCCESSFUL.toString())){
                cwLessonsEduSubjects.add(new ArrayList<>());
            }else{
                cwLessonsEduSubjects.get(cwLessonsEduSubjects.size() - 1).add(i);
            }
        }
        for (List<String> i:
                cwLessonsEduSubjects) {
            int j = i.get(1).lastIndexOf('.');
            if (i.get(1).substring(j+1).equals("msg")){
                i.set(1,i.get(1).replace(".msg",""));
            }
        }
        Collections.reverse(cwLessonsEduSubjects);
    }
    private void initAvailablePeopleList(List<String> orders) {
        orders.remove(0);
        availablePeople = new ArrayList<>();
        for (String i:
                orders) {
            if (i.equals(ServerRespondType.SUCCESSFUL.toString())){
                availablePeople.add(new ArrayList<>());
            }else{
                availablePeople.get(availablePeople.size() - 1).add(i);
            }
        }
    }
    public void initUserLessons(List<String> orders){
        orders.remove(0);
        userLessons = new ArrayList<>();
        for (String i:
             orders) {
            if (i.equals(ServerRespondType.SUCCESSFUL.toString())){
                userLessons.add(new ArrayList<>());
            }else{
                userLessons.get(userLessons.size() - 1).add(i);
            }
        }
    }
    private void initHomeWorks(List<String> orders) {
        orders.remove(0);
        homeWorks = new ArrayList<>();
        for (String i:
                orders) {
            if (i.equals(ServerRespondType.SUCCESSFUL.toString())){
                homeWorks.add(new ArrayList<>());
            }else{
                homeWorks.get(homeWorks.size() - 1).add(i);
            }
        }
    }
    private void initRecommendList(List<String> orders) {
        orders.remove(0);
        recommendsList = new ArrayList<>();
        for (String i:
                orders) {
            if (i.equals(ServerRespondType.SUCCESSFUL.toString())){
                recommendsList.add(new ArrayList<>());
            }else{
                recommendsList.get(recommendsList.size() - 1).add(i);
            }
        }
    }
    private void initStudentsList(List<String> orders) {
        orders.remove(0);
        allStudents = new ArrayList<>();
        for (String i:
                orders) {
            if (i.equals(ServerRespondType.SUCCESSFUL.toString())){
                allStudents.add(new ArrayList<>());
            }else{
                allStudents.get(allStudents.size() - 1).add(i);
            }
        }
    }
    private void initMinorReqList(List<String> orders) {
        orders.remove(0);
        minorReqList = new ArrayList<>();
        for (String i:
                orders) {
            if (i.equals(ServerRespondType.SUCCESSFUL.toString())){
                minorReqList.add(new ArrayList<>());
            }else{
                minorReqList.get(minorReqList.size() - 1).add(i);
            }
        }
    }
    public void initReqMessageList(List<String> orders){
        orders.remove(0);
        reqMessages = new ArrayList<>();
        for (String i:
                orders) {
            if (i.equals(ServerRespondType.SUCCESSFUL.toString())){
                reqMessages.add(new ArrayList<>());
            }else{
                reqMessages.get(reqMessages.size() - 1).add(i);
            }
        }
    }

    private void showMinorReqResult(List<String> orders) {
        try {
            if (orders.get(1).equals(ServerRespondType.SUCCESSFUL.toString())){
                GuiController.getInstance().getjOptionPane().showMessageDialog(null,"YOUR REQ HAS BEEN SAVED SUCCESSFULLY!");
            }else {
                GuiController.getInstance().getjOptionPane().showMessageDialog(null,"YOU ARE UNDER_DEFINED_SCORE!");
            }
        }catch (Exception e){

        }
    }
    private void initTemporaryGradesList(List<String> orders) {
        orders.remove(0);
        userTemporaryGradesList = new ArrayList<>();
        for (String i:
                orders) {
            if (i.equals(ServerRespondType.SUCCESSFUL.toString())){
                userTemporaryGradesList.add(new ArrayList<>());
            }else{
                userTemporaryGradesList.get(userTemporaryGradesList.size() - 1).add(i);
            }
        }
    }
    private void initLeaveReqList(List<String> orders) {
        orders.remove(0);
        leaveReqList = new ArrayList<>();
        for (String i:
                orders) {
            if (i.equals(ServerRespondType.SUCCESSFUL.toString())){
                leaveReqList.add(new ArrayList<>());
            }else{
                leaveReqList.get(leaveReqList.size() - 1).add(i);
            }
        }
    }

    private void showLeaveReqResult(List<String> orders) {
        if (orders.get(1).equals(ServerRespondType.SUCCESSFUL.toString())){
            GuiController.getInstance().getjOptionPane().showMessageDialog(null,"YOUR REQ HAS BEEN SAVED SUCCESSFULLY!");
        }else {
            GuiController.getInstance().getjOptionPane().showMessageDialog(null,"SOME THING WENT WRONG!");
        }
    }
    public void updateTemporaryGradesList(){
        List<String> req = new ArrayList<>();
        req.add(ClientReqType.TEMPORARYGRADESLIST.toString());
        GuiController.getInstance().getClient().getClientSender().sendMessage(req);
    }
    public void updateLeaveReqList(){
        List<String> req = new ArrayList<>();
        req.add(ClientReqType.LEAVEREQLIST.toString());
        GuiController.getInstance().getClient().getClientSender().sendMessage(req);
    }
    public void updateLessonsList(){
        List<String> orders = new ArrayList<>();
        orders.add(ClientReqType.GETLESSONSLIST.toString());
        GuiController.getInstance().getClient().getClientSender().sendMessage(orders);
    }
    public void updateTeachersList(){
        List<String> orders = new ArrayList<>();
        orders.add(ClientReqType.GETTEACHERSLIST.toString());
        GuiController.getInstance().getClient().getClientSender().sendMessage(orders);
    }
    public void updateUserLessons(){
        List<String> req = new ArrayList<>();
        req.add(ClientReqType.GETUSERLESSONS.toString());
        GuiController.getInstance().getClient().getClientSender().sendMessage(req);
    }
    public void updateRecommendsList(){
        List<String> req = new ArrayList<>();
        req.add(ClientReqType.GETRECOMMENDLIST.toString());
        GuiController.getInstance().getClient().getClientSender().sendMessage(req);
    }
    public void updateMinorReqList(){
        List<String> req = new ArrayList<>();
        req.add(ClientReqType.MINORREQLIST.toString());
        GuiController.getInstance().getClient().getClientSender().sendMessage(req);
    }
    public void updateStudentsList(){
        List<String> req = new ArrayList<>();
        req.add(ClientReqType.GET_STUDENTS_LIST.toString());
        GuiController.getInstance().getClient().getClientSender().sendMessage(req);
    }
    public void updateRecommendedLessonsList(){
        List<String> req = new ArrayList<>();
        req.add(ClientReqType.GET_RECOMMENDED_LESSONS.toString());
        GuiController.getInstance().getClient().getClientSender().sendMessage(req);
    }
    public void updateReqMessage(){
        List<String> req = new ArrayList<>();
        req.add(ClientReqType.GET_REQ_MESSAGES.toString());
        GuiController.getInstance().getClient().getClientSender().sendMessage(req);
    }
    public void updateChats(){
        List<String> req = new ArrayList<>();
        req.add(ClientReqType.GET_CHATS.toString());
        GuiController.getInstance().getClient().getClientSender().sendMessage(req);
    }
    public void  updateAvailablePeople(){
        List<String> req = new ArrayList<>();
        req.add(ClientReqType.GET_AVAILABLE_PEOPLE.toString());
        GuiController.getInstance().getClient().getClientSender().sendMessage(req);
    }
    public void updateAllStudents(){
        List<String> req = new ArrayList<>();
        req.add(ClientReqType.GET_STUDENTS_LIST.toString());
        GuiController.getInstance().getClient().getClientSender().sendMessage(req);
    }
    public void updateCwLessonsEduSubject(){
        List<String> req = new ArrayList<>();
        req.add(ClientReqType.GET_CW_EDU_SUBJECTS.toString());
        GuiController.getInstance().getClient().getClientSender().sendMessage(req);
    }
    public void updateHomeWorks(){
        List<String> req = new ArrayList<>();
        req.add(ClientReqType.GET_HM.toString());
        GuiController.getInstance().getClient().getClientSender().sendMessage(req);
    }
    public void updateUploadedHm(){
        List<String> req = new ArrayList<>();
        req.add(ClientReqType.GET_UPLOADED_HM.toString());
        GuiController.getInstance().getClient().getClientSender().sendMessage(req);
    }
    public void showResult(List<String> order){
        try {
            if (order.get(1).equals(ServerRespondType.SUCCESSFUL.toString())){
                GuiController.getInstance().getjOptionPane().showMessageDialog(null,"SUCCESSFUL!");
            }else if (order.get(1).equals(ServerRespondType.UNSUCCESSFUL.toString())){
                GuiController.getInstance().getjOptionPane().showMessageDialog(null,"UNSUCCESSFUL! (S.TH WENT WRONG)");
            }else {
                JOptionPane.showMessageDialog(null,order.get(1));
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("IDK WHATS WRONG WITH THIS!");
        }
    }
    public void sendReqMessage(String msg){
        List<String> req = new ArrayList<>();
        req.add(ClientReqType.SEND_REQ_MESSAGE.toString());
        req.add(msg);
        GuiController.getInstance().getClient().getClientSender().sendMessage(req);
    }
    public void downloadFile(List<String> orders){
        FileDownloader fileDownloader = new FileDownloader(orders);
        new Thread(fileDownloader).start();
    }
    public ImageIcon getImageIcon(){
        if (imageIcon == null){
            ImageIcon imageIcon = new ImageIcon(ResourceManager.get(ImageResource.NULL_PROFILE));
            return imageIcon ;
        }
        return imageIcon;
    }
    public void resetData(){
        dataHandler = null;
    }

    public List<List<String>> getMinorReqList() {
        return minorReqList;
    }

    public void setMinorReqList(List<List<String>> minorReqList) {
        this.minorReqList = minorReqList;
    }

    public List<List<String>> getLeaveReqList() {
        return leaveReqList;
    }

    public void setLeaveReqList(List<List<String>> leaveReqList) {
        this.leaveReqList = leaveReqList;
    }

    public List<List<String>> getRecommendsList() {
        return recommendsList;
    }

    public void setRecommendsList(List<List<String>> recommendsList) {
        this.recommendsList = recommendsList;
    }

    public List<List<String>> getAllTeachers() {
        return allTeachers;
    }

    public void setAllTeachers(List<List<String>> allTeachers) {
        this.allTeachers = allTeachers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNc() {
        return nc;
    }

    public void setNc(int nc) {
        this.nc = nc;
    }

    public String getEntranceYear() {
        return entranceYear;
    }

    public void setEntranceYear(String entranceYear) {
        this.entranceYear = entranceYear;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getFullName() {
        return fullName;
    }

    public double getAverageGrade() {
        return averageGrade;
    }

    public void setAverageGrade(double averageGrade) {
        this.averageGrade = averageGrade;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEducationStatus() {
        return educationStatus;
    }

    public void setEducationStatus(String educationStatus) {
        this.educationStatus = educationStatus;
    }

    public String getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    public boolean isSignUpPermit() {
        return this.sp.equals("true");
    }

    public void setSignUpPermit(boolean signUpPermit) {
        this.signUpPermit = signUpPermit;
        if (signUpPermit){
            this.sp = "true";
        }else {
            this.sp = "false";
        }
    }

    public String getSignupTime() {
        return signupTime;
    }

    public void setSignupTime(String signupTime) {
        this.signupTime = signupTime;
    }

    public List<List<String>> getAllLessons() {
        return allLessons;
    }

    public void setAllLessons(List<List<String>> lessons) {
        this.allLessons = lessons;
    }

    public List<List<String>> getUserLessons() {

        return userLessons;
    }

    public List<List<String>> getUserTemporaryGradesList() {
        return userTemporaryGradesList;
    }

    public void setUserTemporaryGradesList(List<List<String>> userTemporaryGradesList) {
        this.userTemporaryGradesList = userTemporaryGradesList;
    }

    public void setUserLessons(List<List<String>> userLessons) {
        this.userLessons = userLessons;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getTeacherLevel() {
        return teacherLevel;
    }

    public void setTeacherLevel(String teacherLevel) {
        this.teacherLevel = teacherLevel;
    }

    public String getDormitoryReq() {
        return dormitoryReq;
    }

    public String getEduLevel() {
        return eduLevel;
    }

    public List<List<String>> getAllStudents() {
        return allStudents;
    }

    public List<List<String>> getRecommendedLessonsList() {
        return recommendedLessonsList;
    }

    public void setRecommendedLessonsList(List<List<String>> recommendedLessonsList) {
        this.recommendedLessonsList = recommendedLessonsList;
    }

    public List<List<String>> getMarkedLessons() {
        return markedLessons;
    }

    public List<List<String>> getReqMessages() {
        return reqMessages;
    }

    public List<List<String>> getChats() {
        return chats;
    }

    public List<List<String>> getAvailablePeople() {
        return availablePeople;
    }

    public List<List<String>> getCwLessonsEduSubjects() {
        return cwLessonsEduSubjects;
    }

    public List<List<String>> getHomeWorks() {
        return homeWorks;
    }

    public List<List<String>> getUploadedHm() {
        return uploadedHm;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(List<String> tokens) {
        this.token = tokens.get(1);
    }
}

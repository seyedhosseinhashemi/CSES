package ServerSide;


import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class DataBase {
    Connection connection;

    private static DataBase dataBase;

    private DataBase() {
        try {
            connection = DriverManager.getConnection(Config.databaseUrl, Config.databaseUser, Config.databasePass);
        } catch (SQLException e) {
            e.printStackTrace();
            for (ClientHandler c:
                 Server.clients) {
                List<String> res = new ArrayList<>();
                res.add(ServerReqType.SHOW_RESULT.toString());
                res.add("CONNECTION TO THE DATA BASE HAS BEEN LOST !");
                c.sendMessage(res.toString());
                try {
                    c.kill();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static DataBase getInstance() {
        if (dataBase == null) {
            dataBase = new DataBase();
        }
        return dataBase;
    }

    synchronized void checkLogin(ClientHandler clientHandler, String username, String password) {
        List<String> respond = new ArrayList<>();
        Date date = new Date();
        int hour = date.getHours();
        int minute = date.getMinutes();
        int seconds = date.getSeconds();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from sut_members where id = ? and password =?");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                //updating client info
                respond.add(ServerReqType.LOGIN.toString());
                respond.add(RespondType.SUCCESSFUL.toString());
                respond.add(String.valueOf(resultSet.getInt("id")));
                respond.add(String.valueOf(resultSet.getInt("nc")));
                respond.add(resultSet.getString("firstname"));
                respond.add(resultSet.getString("lastname"));
                respond.add(resultSet.getString("relation"));
                respond.add(resultSet.getString("email"));
                respond.add(resultSet.getString("phonenumber"));
                respond.add(resultSet.getString("college"));
                respond.add(hour + ":" + minute + ":" + seconds);

                clientHandler.id = resultSet.getInt("id");
                clientHandler.nc = resultSet.getInt("nc");
                clientHandler.firstname = resultSet.getString("firstname");
                clientHandler.lastname = resultSet.getString("lastname");
                clientHandler.relation = resultSet.getString("relation");
                clientHandler.email = resultSet.getString("email");
                clientHandler.phoneNumber = resultSet.getString("phonenumber");
                clientHandler.college = resultSet.getString("college");
                clientHandler.lastLoginTime = hour + ":" + minute + ":" + seconds;

                switch (clientHandler.relation) {
                    case "D":
                        preparedStatement = connection.prepareStatement("select * from students where id = ?");
                        preparedStatement.setInt(1, clientHandler.id);
                        resultSet = preparedStatement.executeQuery();
                        if (resultSet.next()) {
                            respond.add(resultSet.getString("educational_status"));
                            respond.add(findMemberName(resultSet.getInt("supervisor_id")));
                            respond.add(resultSet.getString("signup_permit"));
                            respond.add(resultSet.getString("signup_time"));
                            respond.add(resultSet.getString("grade_average"));
                            respond.add(resultSet.getString("enter_year"));
                            respond.add(resultSet.getString("education_level"));

                            clientHandler.studentLvl = resultSet.getString("education_level");
                            clientHandler.enterYear = resultSet.getString("enter_year");
                            clientHandler.superVisorId = resultSet.getInt("supervisor_id");
                        }

                        clientHandler.isStudent = true;
                        System.out.println("STUDENT LOGEDIN!");
                        break;
                    case "O":
                        preparedStatement = connection.prepareStatement("select * from teachers where id = ?");
                        preparedStatement.setInt(1, clientHandler.id);
                        resultSet = preparedStatement.executeQuery();
                        if (resultSet.next()) {
                            respond.add(resultSet.getString("level"));
                            respond.add(resultSet.getString("roomid"));
                        }
                        clientHandler.isTeacher = true;
                        System.out.println("TEACHER LOGEDIN!");
                        break;
                    case "M":
                        preparedStatement = connection.prepareStatement("select * from teachers where id = ?");
                        preparedStatement.setInt(1, clientHandler.id);
                        resultSet = preparedStatement.executeQuery();
                        if (resultSet.next()) {
                            respond.add(resultSet.getString("level"));
                            respond.add(resultSet.getString("roomid"));
                        }
                        clientHandler.isTeacher = true;
                        clientHandler.isEduAssistant = true;
                        System.out.println("ASSISTANCE LOGEDIN!");
                        break;
                    case "R":
                        preparedStatement = connection.prepareStatement("select * from teachers where id = ?");
                        preparedStatement.setInt(1, clientHandler.id);
                        resultSet = preparedStatement.executeQuery();
                        if (resultSet.next()) {
                            respond.add(resultSet.getString("level"));
                            respond.add(resultSet.getString("roomid"));
                        }
                        clientHandler.isTeacher = true;
                        //clientHandler.isEduAssistant = true;
                        clientHandler.isEduManager = true;
                        System.out.println("MANAGER LOGEDIN!");
                        break;
                    case "E":
                        clientHandler.isMrMohseni = true;
                        break;
                }

                preparedStatement = connection.prepareStatement("update sut_members set lastlogintime = ? where id = ?");
                preparedStatement.setString(1, hour + ":" + minute + ":" + seconds);
                preparedStatement.setString(2, username);
                preparedStatement.executeUpdate();


            } else {
                respond.add(ServerReqType.LOGIN.toString());
                respond.add(RespondType.UNSUCCESSFUL.toString());
            }
            clientHandler.sendMessage(respond.toString());
            respond.clear();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    synchronized public String findMemberName(int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from sut_members where id = ?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString("firstname") + " " + resultSet.getString("lastname");
        }
        return "NULL";
    }

    synchronized public void getLessonsList(ClientHandler clientHandler) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from lessons");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<String> respond = new ArrayList<>();
        respond.add(ServerReqType.GETLESSONSLIST.toString());
        while (resultSet.next()) {
            respond.add(RespondType.SUCCESSFUL.toString());
            respond.add(resultSet.getString("lessonid"));
            respond.add(resultSet.getString("name"));
            respond.add(resultSet.getString("prereq"));
            respond.add(resultSet.getString("teacherid"));
            respond.add(resultSet.getString("college"));
            respond.add(resultSet.getString("units"));
            respond.add(resultSet.getString("level"));
            respond.add(resultSet.getString("capacity"));
            respond.add(resultSet.getString("days"));
            respond.add(resultSet.getString("time"));
            respond.add(resultSet.getString("examdate"));

        }
        clientHandler.sendMessage(respond.toString());
        respond.clear();
    }

    synchronized public void getTeachersList(ClientHandler clientHandler) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from sut_members join teachers on sut_members.id = teachers.id");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<String> respond = new ArrayList<>();
        respond.add(ServerReqType.GETTEACHERSLIST.toString());
        while (resultSet.next()) {
            respond.add(RespondType.SUCCESSFUL.toString());
            respond.add(resultSet.getString("id"));
            respond.add(resultSet.getString("firstname") + " " + resultSet.getString("lastname"));
            respond.add(resultSet.getString("email"));
            respond.add(resultSet.getString("college"));
            respond.add(resultSet.getString("phonenumber"));
            respond.add(resultSet.getString("level"));
        }
        clientHandler.sendMessage(respond.toString());
        respond.clear();
    }

    synchronized public void getUserLessons(ClientHandler clientHandler) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from lessons join student_lessons on lessons.lessonid = student_lessons.lessonid where (student_lessons.id = ? and lessons.teacherid = student_lessons.teacherid) or lessons.teacherid = ?");
        preparedStatement.setInt(1, clientHandler.id);
        preparedStatement.setInt(2, clientHandler.id);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<String> respond = new ArrayList<>();
        respond.add(ServerReqType.GETUSERLESSONS.toString());
        while (resultSet.next()) {
            respond.add(RespondType.SUCCESSFUL.toString());
            respond.add(resultSet.getString("lessonid"));
            respond.add(resultSet.getString("name"));
            respond.add(resultSet.getString("prereq"));
            respond.add(resultSet.getString("lessons.teacherid"));
            respond.add(resultSet.getString("college"));
            respond.add(resultSet.getString("units"));
            respond.add(resultSet.getString("level"));
            respond.add(resultSet.getString("capacity"));
            respond.add(resultSet.getString("days"));
            respond.add(resultSet.getString("time"));
            respond.add(resultSet.getString("examdate"));
            respond.add(resultSet.getString("score"));
        }
        clientHandler.sendMessage(respond.toString());
        respond.clear();
    }

    synchronized public void getRecommendList(ClientHandler clientHandler) throws SQLException {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        List<String> respond = new ArrayList<>();
        respond.add(ServerReqType.GETRECOMMENDLIST.toString());
        if (clientHandler.isStudent) {
            preparedStatement = connection.prepareStatement("select * from recommend_req join sut_members on recommend_req.teacherid = sut_members.id where recommend_req.studentid = ?");
            preparedStatement.setInt(1, clientHandler.id);
        } else if (clientHandler.isTeacher) {
            preparedStatement = connection.prepareStatement("select * from recommend_req join sut_members on recommend_req.studentid = sut_members.id where recommend_req.teacherid = ?");
            preparedStatement.setInt(1, clientHandler.id);

        } else {
            preparedStatement = connection.prepareStatement("");
        }
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            respond.add(RespondType.SUCCESSFUL.toString());
            respond.add(resultSet.getString("id"));
            respond.add(resultSet.getString("firstname") + " " + resultSet.getString("lastname"));
            respond.add(resultSet.getString("result"));
        }
        clientHandler.sendMessage(respond.toString());
        respond.clear();
    }

    synchronized public void setRecommendReq(ClientHandler clientHandler, String teacherID) throws SQLException {
        List<String> res = new ArrayList<>();
        res.add(ServerReqType.RECOMMENDREQ.toString());
        if (clientHandler.isStudent) {
            String check = findMemberRelation(Integer.parseInt(teacherID));
            if (check.equals("O")) {
                PreparedStatement preparedStatement = connection.prepareStatement("insert into recommend_req values (? , ? , default)");
                preparedStatement.setInt(1, clientHandler.id);
                preparedStatement.setInt(2, Integer.parseInt(teacherID));
                preparedStatement.execute();
                res.add(RespondType.SUCCESSFUL.toString());
            } else {
                res.add(RespondType.UNSUCCESSFUL.toString());
            }
            clientHandler.sendMessage(res.toString());
            getRecommendList(clientHandler);
        }

    }

    synchronized public String findMemberRelation(int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select relation from sut_members where id = ?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString("relation");
        }
        return "NULL";
    }

    synchronized public void setMinorReq(ClientHandler clientHandler, List<String> orders) throws SQLException {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        List<String> respond = new ArrayList<>();
        respond.add(ServerReqType.MINORREQ.toString());
        if (clientHandler.isStudent) {
            preparedStatement = connection.prepareStatement("select grade_average from students where id = ?");
            preparedStatement.setInt(1, clientHandler.id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                double score = resultSet.getDouble("grade_average");
                if (score >= 14.0) {
                    preparedStatement = connection.prepareStatement("insert into minor_req values (?,?,?,default,default)");
                    preparedStatement.setInt(1, clientHandler.id);
                    preparedStatement.setString(2, clientHandler.college);
                    preparedStatement.setString(3, orders.get(1));
                    preparedStatement.execute();
                    respond.add(RespondType.SUCCESSFUL.toString());
                } else {
                    respond.add(RespondType.UNSUCCESSFUL.toString());
                }
            } else {
                respond.add(RespondType.UNSUCCESSFUL.toString());
            }
        } else if (clientHandler.isEduAssistant) {
            preparedStatement = connection.prepareStatement("select * from minor_req where studentid = ? and origin_college = ?");
            preparedStatement.setString(1, orders.get(1));
            preparedStatement.setString(2, clientHandler.college);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                preparedStatement = connection.prepareStatement("update minor_req set origin_result = ? where studentid = ?");
                preparedStatement.setString(1, orders.get(2));
                preparedStatement.setString(2, orders.get(1));
                preparedStatement.executeUpdate();
            }
            preparedStatement = connection.prepareStatement("select * from minor_req where studentid = ? and aim_college = ?");
            preparedStatement.setString(1, orders.get(1));
            preparedStatement.setString(2, clientHandler.college);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                preparedStatement = connection.prepareStatement("update minor_req set aim_result = ? where studentid = ?");
                preparedStatement.setString(1, orders.get(2));
                preparedStatement.setString(2, orders.get(1));
                preparedStatement.executeUpdate();
            }
            respond.add(RespondType.SUCCESSFUL.toString());
        }
        sendSuccessMessage(clientHandler);
        getMinorReqList(clientHandler);
        clientHandler.sendMessage(respond.toString());
        respond.clear();
    }

    synchronized public void sendSuccessMessage(ClientHandler clientHandler) {
        clientHandler.sendMessage(ServerReqType.SHOW_RESULT.toString() + ", " + RespondType.SUCCESSFUL.toString());
    }

    synchronized public void sendUnSuccessMessage(ClientHandler clientHandler) {
        clientHandler.sendMessage(ServerReqType.SHOW_RESULT.toString() + ", " + RespondType.UNSUCCESSFUL.toString());
    }

    synchronized public void getMinorReqList(ClientHandler clientHandler) throws SQLException {
        List<String> res = new ArrayList<>();
        res.add(ServerReqType.MINORREQLIST.toString());
        if (clientHandler.isStudent) {
            PreparedStatement preparedStatement = connection.prepareStatement("select aim_college , aim_result , origin_result from minor_req where studentid = ?");
            preparedStatement.setInt(1, clientHandler.id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                res.add(RespondType.SUCCESSFUL.toString());
                res.add(resultSet.getString("aim_college"));
                res.add(resultSet.getString("origin_result") + " / " + resultSet.getString("aim_result"));
            }
        } else if (clientHandler.isEduAssistant) {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from minor_req where aim_college = ? or origin_college = ?");
            preparedStatement.setString(1, clientHandler.college);
            preparedStatement.setString(2, clientHandler.college);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                res.add(RespondType.SUCCESSFUL.toString());
                res.add(resultSet.getString("studentid"));
                res.add(resultSet.getString("origin_college"));
                res.add(resultSet.getString("aim_college"));
                res.add(resultSet.getString("origin_result"));
                res.add(resultSet.getString("aim_result"));
            }
        }
        clientHandler.sendMessage(res.toString());
        res.clear();
    }

    synchronized public void setLeaveReq(ClientHandler clientHandler, List<String> order) throws SQLException {
        List<String> res = new ArrayList<>();
        res.add(ServerReqType.LEAVEREQ.toString());

        if (clientHandler.isStudent) {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from leave_req where studentid = ?");
            preparedStatement.setInt(1, clientHandler.id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                res.add(RespondType.SUCCESSFUL.toString());
                preparedStatement = connection.prepareStatement("insert into leave_req values (?,default)");
                preparedStatement.setInt(1, clientHandler.id);
                preparedStatement.execute();
            } else {
                res.add(RespondType.UNSUCCESSFUL.toString());
            }
        } else if (clientHandler.isEduAssistant) {
            PreparedStatement preparedStatement = connection.prepareStatement("update leave_req,students set leave_req.result = ?,students.educational_status = ? where leave_req.studentid = ? and students.id = ?");
            preparedStatement.setString(1, "ACCEPTED");
            preparedStatement.setString(2, "LEAVED");
            preparedStatement.setString(3, order.get(1));
            preparedStatement.setString(4, order.get(1));
            preparedStatement.executeUpdate();
            res.add(RespondType.SUCCESSFUL.toString());
        }
        getLeaveReqList(clientHandler);
        clientHandler.sendMessage(res.toString());
        res.clear();
    }

    synchronized public void getLeaveReqList(ClientHandler clientHandler) throws SQLException {
        List<String> res = new ArrayList<>();
        res.add(ServerReqType.LEAVEREQLIST.toString());
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        if (clientHandler.isStudent) {
            preparedStatement = connection.prepareStatement("select * from leave_req where studentid = ?");
            preparedStatement.setInt(1, clientHandler.id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                res.add(RespondType.SUCCESSFUL.toString());
                res.add("-_-_-_-_-");
                res.add(resultSet.getString("result"));
            }
        } else if (clientHandler.isEduAssistant) {
            preparedStatement = connection.prepareStatement("select * from leave_req join sut_members on leave_req.studentid = sut_members.id where sut_members.college = ?");
            preparedStatement.setString(1, clientHandler.college);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                res.add(RespondType.SUCCESSFUL.toString());
                res.add(resultSet.getString("sut_members.id"));
                res.add(resultSet.getString("sut_members.firstname") + " " + resultSet.getString("sut_members.lastname"));
                res.add(resultSet.getString("leave_req.result"));
            }
        }
        clientHandler.sendMessage(res.toString());
    }

    synchronized public void setObjection(ClientHandler clientHandler, List<String> order) throws SQLException {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        if (clientHandler.isStudent) {
            preparedStatement = connection.prepareStatement("update student_lessons set student_objection = ? where id = ? and lessonid = ?");
            preparedStatement.setString(1, order.get(order.size() - 1));
            preparedStatement.setInt(2, clientHandler.id);
            preparedStatement.setInt(3, Integer.parseInt(order.get(1)));
            preparedStatement.executeUpdate();
        } else if (clientHandler.isTeacher) {
            preparedStatement = connection.prepareStatement("update student_lessons set teacher_answer = ? where id = ? and lessonid = ?");
            preparedStatement.setString(1, order.get(order.size() - 1));
            preparedStatement.setString(2, order.get(2));
            preparedStatement.setString(3, order.get(1));
            preparedStatement.executeUpdate();
        }
    }

    synchronized public void getTemporaryGradesList(ClientHandler clientHandler) throws SQLException {
        List<String> res = new ArrayList<>();
        res.add(ServerReqType.TEMPORARYGRADESLIST.toString());

        PreparedStatement preparedStatement;
        ResultSet resultSet;

        if (clientHandler.isStudent) {
            preparedStatement = connection.prepareStatement("select * from student_lessons where id = ?");
            preparedStatement.setInt(1, clientHandler.id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                res.add(RespondType.SUCCESSFUL.toString());
                res.add(resultSet.getString("lessonid"));
                res.add(resultSet.getString("score"));
                res.add(resultSet.getString("student_objection"));
                res.add(resultSet.getString("teacher_answer"));
            }
        } else if (clientHandler.isTeacher && !clientHandler.isEduAssistant) {
            preparedStatement = connection.prepareStatement("select * from sut_members join (student_lessons join lessons on lessons.lessonid = student_lessons.lessonid )on student_lessons.id = sut_members.id where lessons.teacherid = ?");
            preparedStatement.setInt(1, clientHandler.id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                res.add(RespondType.SUCCESSFUL.toString());
                res.add(resultSet.getString("sut_members.id"));
                res.add(resultSet.getString("sut_members.firstname") + " " + resultSet.getString("sut_members.lastname"));
                res.add(resultSet.getString("student_lessons.lessonid"));
                res.add(resultSet.getString("lessons.name"));
                res.add(resultSet.getString("student_lessons.score"));
                res.add(resultSet.getString("student_lessons.student_objection"));
                res.add(resultSet.getString("student_lessons.teacher_answer"));
            }
        } else if (clientHandler.isEduAssistant) {
            preparedStatement = connection.prepareStatement("select * from sut_members join (student_lessons join lessons on lessons.lessonid = student_lessons.lessonid )on student_lessons.id = sut_members.id where sut_members.college = ?");
            preparedStatement.setString(1, clientHandler.college);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                res.add(RespondType.SUCCESSFUL.toString());
                res.add(resultSet.getString("sut_members.id"));
                res.add(resultSet.getString("sut_members.firstname") + " " + resultSet.getString("sut_members.lastname"));
                res.add(resultSet.getString("lessons.teacherid"));
                res.add(resultSet.getString("student_lessons.lessonid"));
                res.add(resultSet.getString("lessons.name"));
                res.add(resultSet.getString("student_lessons.score"));
                res.add(resultSet.getString("student_lessons.student_objection"));
                res.add(resultSet.getString("student_lessons.teacher_answer"));
            }
        }

        clientHandler.sendMessage(res.toString());
    }

    synchronized public void setUserEmail(ClientHandler clientHandler, List<String> order) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("update sut_members set email = ? where id = ?");
        preparedStatement.setString(1, order.get(1));
        preparedStatement.setInt(2, clientHandler.id);
        preparedStatement.executeUpdate();
    }

    synchronized public void setUserPhoneNumber(ClientHandler clientHandler, List<String> order) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("update sut_members set phonenumber = ? where id = ?");
        preparedStatement.setString(1, order.get(1));
        preparedStatement.setInt(2, clientHandler.id);
        preparedStatement.executeUpdate();
    }

    synchronized public void setRecResult(ClientHandler clientHandler, List<String> order) throws SQLException {
        if (clientHandler.isTeacher) {
            PreparedStatement preparedStatement = connection.prepareStatement("update recommend_req set result = ? where teacherid = ? and studentid = ?");
            preparedStatement.setString(1, order.get(2));
            preparedStatement.setInt(2, clientHandler.id);
            preparedStatement.setInt(3, Integer.parseInt(order.get(1)));
            preparedStatement.executeUpdate();
        }
    }

    synchronized public void setTemporaryGrades(ClientHandler clientHandler, List<String> order) throws SQLException {
        if (clientHandler.isTeacher) {
            PreparedStatement preparedStatement = connection.prepareStatement("update student_lessons set score = ? where id = ? and lessonid = ? and teacherid = ?");
            preparedStatement.setDouble(1, Double.parseDouble(order.get(order.size() - 1)));
            preparedStatement.setInt(2, Integer.parseInt(order.get(1)));
            preparedStatement.setInt(3, Integer.parseInt(order.get(2)));
            preparedStatement.setInt(4, clientHandler.id);
            preparedStatement.executeUpdate();
        }
    }

    synchronized public void deleteLesson(ClientHandler clientHandler, List<String> order) throws SQLException {
        if (clientHandler.isEduAssistant) {
            PreparedStatement preparedStatement = connection.prepareStatement("delete from lessons where lessonid = ?");
            preparedStatement.setInt(1, Integer.parseInt(order.get(1)));
            preparedStatement.execute();
            List<String> res = new ArrayList<>();
            res.add(ServerReqType.SHOW_RESULT.toString());
            res.add(RespondType.SUCCESSFUL.toString());
            clientHandler.sendMessage(res.toString());
        }
    }

    synchronized public void addLesson(ClientHandler clientHandler, List<String> order) throws SQLException {
        if (clientHandler.isEduAssistant) {
            int j = 0;
            PreparedStatement preparedStatement = connection.prepareStatement("insert into lessons values (?,?,?,?,?,?,?,?,?,?,?,?)");
            for (String i :
                    order) {
                if (j != 0) {
                    preparedStatement.setString(j, i);
                }
                j++;
            }
            preparedStatement.execute();
            sendSuccessMessage(clientHandler);
        }

    }

    synchronized public void addStudent(ClientHandler clientHandler, List<String> order) throws SQLException {
        if (clientHandler.isEduAssistant) {
            int j = 0;
            int k = 1;
            boolean firstTime = true;
            PreparedStatement preparedStatement = connection.prepareStatement("insert into sut_members values (?,?,?,?,?,?,?,?,?,?,?)");
            for (String i :
                    order) {
                if (j > 0 && j < 12) {
                    preparedStatement.setString(j, i);

                } else if (j >= 12) {
                    if (firstTime) {
                        preparedStatement.execute();
                        preparedStatement = connection.prepareStatement("insert into students values (?,?,?,?,?,?,?,?)");
                        firstTime = false;
                    }
                    preparedStatement.setString(k, i);
                    k++;
                }
                j++;
            }
            preparedStatement.execute();
            clientHandler.sendMessage(ServerReqType.SHOW_RESULT.toString() + ", " + RespondType.SUCCESSFUL.toString());
        }
    }

    synchronized public void addTeacher(ClientHandler clientHandler, List<String> order) throws SQLException {
        if (clientHandler.isEduAssistant) {
            int j = 0;
            int k = 1;
            boolean firstTime = true;
            PreparedStatement preparedStatement = connection.prepareStatement("insert into sut_members values (?,?,?,?,?,?,?,?,?,?,?)");
            for (String i :
                    order) {
                if (j > 0 && j < 12) {
                    preparedStatement.setString(j, i);

                } else if (j >= 12) {
                    if (firstTime) {
                        preparedStatement.execute();
                        preparedStatement = connection.prepareStatement("insert into teachers values (?,?,?)");
                        firstTime = false;
                    }
                    preparedStatement.setString(k, i);
                    k++;
                }
                j++;
            }
            preparedStatement.execute();
            clientHandler.sendMessage(ServerReqType.SHOW_RESULT.toString() + ", " + RespondType.SUCCESSFUL.toString());
        }
    }

    synchronized public void deleteTeacher(ClientHandler clientHandler, List<String> order) throws SQLException {
        if (clientHandler.isEduManager) {
            PreparedStatement preparedStatement = connection.prepareStatement("delete sut_members,teachers from sut_members inner join teachers on sut_members.id = teachers.id where sut_members.id = ? and teachers.id = ?");
            preparedStatement.setString(1, order.get(1));
            preparedStatement.setString(2, order.get(1));
            preparedStatement.execute();
            sendSuccessMessage(clientHandler);
        }
    }

    synchronized public void upgradeToAssistance(ClientHandler clientHandler, List<String> order) throws SQLException {
        if (clientHandler.isEduManager) {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from edu_assistant join sut_members on edu_assistant.id = sut_members.id where sut_members.college = ?");
            preparedStatement.setString(1, clientHandler.college);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                sendUnSuccessMessage(clientHandler);
                return;
            } else {
                preparedStatement = connection.prepareStatement("update sut_members set relation = ? where id = ?");
                preparedStatement.setString(1, "M");
                preparedStatement.setString(2, order.get(1));
                preparedStatement.executeUpdate();
                preparedStatement = connection.prepareStatement("insert into edu_assistant values (?)");
                preparedStatement.setString(1, order.get(1));
                preparedStatement.execute();
                sendSuccessMessage(clientHandler);
            }
        }
    }

    synchronized public void getStudentsList(ClientHandler clientHandler) throws SQLException {
        if (clientHandler.isEduAssistant) {
            List<String> respond = new ArrayList<>();
            respond.add(ServerReqType.GET_STUDENTS_LIST.toString());
            PreparedStatement preparedStatement = connection.prepareStatement("select * from students join sut_members on students.id = sut_members.id where sut_members.college = ?");
            preparedStatement.setString(1, clientHandler.college);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                respond.add(RespondType.SUCCESSFUL.toString());
                respond.add(resultSet.getString("students.id"));
                respond.add(resultSet.getString("students.signup_time"));
                respond.add(resultSet.getString("students.enter_year"));
                respond.add(resultSet.getString("students.education_level"));
            }
            clientHandler.sendMessage(respond.toString());
        }else if (clientHandler.isMrMohseni){
            List<String> respond = new ArrayList<>();
            respond.add(ServerReqType.GET_STUDENTS_LIST.toString());
            PreparedStatement preparedStatement = connection.prepareStatement("select * from students join sut_members on students.id = sut_members.id");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                respond.add(RespondType.SUCCESSFUL.toString());
                respond.add(resultSet.getString("students.id"));
                respond.add(findMemberName(Integer.parseInt(resultSet.getString("students.id"))));
                respond.add(resultSet.getString("students.education_level"));
                respond.add(resultSet.getString("students.enter_year"));
                respond.add(resultSet.getString("students.grade_average"));
                respond.add(resultSet.getString("students.signup_time"));
                respond.add(resultSet.getString("students.signup_permit"));
                respond.add(resultSet.getString("students.educational_status"));
                respond.add(resultSet.getString("sut_members.phonenumber"));
                respond.add(resultSet.getString("sut_members.email"));
                respond.add(resultSet.getString("sut_members.lastlogintime"));
            }
            clientHandler.sendMessage(respond.toString());
        }
    }

    synchronized public void setChooseTime(ClientHandler clientHandler, List<String> order) throws SQLException {
        if (clientHandler.isEduAssistant) {
            PreparedStatement preparedStatement = connection.prepareStatement("update students set signup_time = ? where enter_year=? and education_level = ?");
            preparedStatement.setString(1, order.get(1));
            preparedStatement.setString(2, order.get(2));
            preparedStatement.setString(3, order.get(3));
            preparedStatement.executeUpdate();

            sendSuccessMessage(clientHandler);
        }
    }

    synchronized public void getRecommendedLessonsList(ClientHandler clientHandler) throws SQLException {
        if (clientHandler.isStudent) {
            List<String> respond = new ArrayList<>();
            respond.add(ServerReqType.GET_RECOMMENDED_LESSONS.toString());
            PreparedStatement preparedStatement1 = connection.prepareStatement("select * from lessons join passed_lessons on lessons.prereq = passed_lessons.lessonid where ((passed_lessons.studentid = ?  and lessons.prereq = passed_lessons.lessonid) or (lessons.college = ? and lessons.level = ?)) and lessons.capacity > 0");
            preparedStatement1.setInt(1, clientHandler.id);
            preparedStatement1.setString(2, clientHandler.college);
            preparedStatement1.setString(3, clientHandler.studentLvl.split("")[1]);
            ResultSet resultSet = preparedStatement1.executeQuery();
            while (resultSet.next()) {
                respond.add(RespondType.SUCCESSFUL.toString());
                respond.add(resultSet.getString("lessonid"));
                respond.add(resultSet.getString("name"));
                respond.add(resultSet.getString("prereq"));
                respond.add(resultSet.getString("teacherid"));
                respond.add(resultSet.getString("college"));
                respond.add(resultSet.getString("units"));
                respond.add(resultSet.getString("level"));
                respond.add(resultSet.getString("capacity"));
                respond.add(resultSet.getString("days"));
                respond.add(resultSet.getString("time"));
                respond.add(resultSet.getString("examdate"));
            }
            clientHandler.sendMessage(respond.toString());
        }
    }

    synchronized public boolean takeLesson(ClientHandler clientHandler, List<String> order) throws SQLException {
        if (clientHandler.isStudent) {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from lessons where lessonid = ? and teacherid=?");
            preparedStatement.setString(1, order.get(1));
            preparedStatement.setString(2, order.get(2));
            ResultSet resultSet = preparedStatement.executeQuery();
            PreparedStatement preparedStatement1 = connection.prepareStatement("select * from lessons join student_lessons on student_lessons.lessonid = lessons.lessonid and student_lessons.teacherid = lessons.teacherid where student_lessons.id = ?");
            preparedStatement1.setInt(1, clientHandler.id);
            ResultSet resultSet1 = preparedStatement1.executeQuery();
            PreparedStatement preparedStatement2 = connection.prepareStatement("select * from passed_lessons where studentid = ?");
            preparedStatement2.setInt(1, clientHandler.id);
            ResultSet resultSet2 = preparedStatement2.executeQuery();
            List<String> respond = new ArrayList<>();
            respond.add(ServerReqType.SHOW_RESULT.toString());
            if (resultSet.next()) {
                try {
                    boolean preReqCheck = false;
                    boolean classTimeCheck = true;
                    boolean examDateTime = true;
                    while (resultSet2.next()) {
                        if (resultSet.getString("prereq").equals(resultSet2.getString("passed_lessons.lessonid")) || resultSet.getString("prereq").equals("0")) {
                            preReqCheck = true;
                        }
                    }
                    while (resultSet1.next()) {

                        if (resultSet.getString("days").equals(resultSet1.getString("lessons.days")) && resultSet.getString("time").equals(resultSet1.getString("lessons.time"))) {
                            classTimeCheck = false;
                        }
                        if (resultSet.getString("examdate").equals(resultSet1.getString("lessons.examdate"))) {
                            examDateTime = false;
                        }

                    }
                    if (Integer.parseInt(resultSet.getString("capacity")) > 0) {
                        if (Integer.parseInt(resultSet.getString("prereq")) == 0 || preReqCheck) {
                            if (classTimeCheck) {
                                if (examDateTime) {
                                    preparedStatement = connection.prepareStatement("update lessons set capacity = ? where lessonid = ? and teacherid =?");
                                    preparedStatement.setInt(1, Integer.parseInt(resultSet.getString("capacity")) - 1);
                                    preparedStatement.setString(2, order.get(1));
                                    preparedStatement.setString(3, order.get(2));
                                    preparedStatement.executeUpdate();
                                    preparedStatement = connection.prepareStatement("insert into student_lessons values (?,?,?,default ,default ,default )");
                                    preparedStatement.setInt(1, clientHandler.id);
                                    preparedStatement.setString(2, order.get(1));
                                    preparedStatement.setString(3, order.get(2));
                                    preparedStatement.execute();
                                    sendSuccessMessage(clientHandler);
                                    return true;
                                } else {
                                    respond.add("LESSON EXAM DATE HAS OVERLAP WITH OTHER LESSONS !");
                                }
                            } else {
                                respond.add("LESSON CLASS TIME HAS OVERLAP WITH OTHER LESSONS !");
                            }
                        } else {
                            respond.add("LESSON PRE REQ DIDNT OBSERVED !");
                        }
                    } else {
                        respond.add("LESSON CAPACITY IS FULL !");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    sendUnSuccessMessage(clientHandler);
                }
            }
            if (respond.size() >= 2) {
                clientHandler.sendMessage(respond.toString());
            }
            respond.clear();
        }
        return false;
    }

    synchronized public void addReqMessage(ClientHandler clientHandler, List<String> order) throws SQLException {
        if (clientHandler.isStudent) {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from sut_members where relation = ? and college = ?");
            preparedStatement.setString(1, "M");
            preparedStatement.setString(2, clientHandler.college);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                preparedStatement = connection.prepareStatement("insert into req_message values (default,?,?,?,default)");
                preparedStatement.setInt(1, clientHandler.id);
                preparedStatement.setString(2, resultSet.getString("id"));
                preparedStatement.setString(3, order.get(1));
                preparedStatement.execute();
                sendSuccessMessage(clientHandler);
            } else {
                sendUnSuccessMessage(clientHandler);
            }
        }
    }

    synchronized public void changeLessonGp(ClientHandler clientHandler, List<String> order) throws SQLException {
        if (clientHandler.isStudent) {
            if (takeLesson(clientHandler, order)) {
                PreparedStatement preparedStatement = connection.prepareStatement("delete from student_lessons where id=? and lessonid=? and teacherid <> ?");
                preparedStatement.setInt(1, clientHandler.id);
                preparedStatement.setString(2, order.get(1));
                preparedStatement.setString(3, order.get(2));
                preparedStatement.execute();
            }
        }
    }

    synchronized public void removeTookLesson(ClientHandler clientHandler, List<String> order) throws SQLException {
        if (clientHandler.isStudent) {
            PreparedStatement preparedStatement = connection.prepareStatement("delete from student_lessons where id=? and lessonid=? and teacherid=?");
            preparedStatement.setInt(1, clientHandler.id);
            preparedStatement.setString(2, order.get(1));
            preparedStatement.setString(3, order.get(2));
            preparedStatement.execute();
            sendSuccessMessage(clientHandler);
        }
    }

    synchronized public void getReqMessages(ClientHandler clientHandler) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select  * from req_message where sender_id = ? or receiver_id = ?");
        preparedStatement.setInt(1, clientHandler.id);
        preparedStatement.setInt(2, clientHandler.id);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<String> respond = new ArrayList<>();
        respond.add(ServerReqType.GET_REQ_MESSAGES.toString());
        while (resultSet.next()) {
            respond.add(RespondType.SUCCESSFUL.toString());
            respond.add(resultSet.getString("message_id"));
            respond.add(resultSet.getString("sender_id"));
            respond.add(resultSet.getString("receiver_id"));
            respond.add(resultSet.getString("message"));
            respond.add(resultSet.getString("result"));
        }
        clientHandler.sendMessage(respond.toString());
    }

    synchronized public void setReqMessageResult(ClientHandler clientHandler, List<String> order) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("update req_message set result = ? where receiver_id = ? and message_id = ?");
        preparedStatement.setString(1, order.get(2));
        preparedStatement.setInt(2, clientHandler.id);
        preparedStatement.setString(3, order.get(1));
        preparedStatement.executeUpdate();
        sendSuccessMessage(clientHandler);
    }

    synchronized public void getChats(ClientHandler clientHandler) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from chats where sender_id = ? or receiver_id = ?");
        preparedStatement.setInt(1, clientHandler.id);
        preparedStatement.setInt(2, clientHandler.id);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<String> respond = new ArrayList<>();
        respond.add(ServerReqType.GET_CHATS.toString());
        while (resultSet.next()) {
            respond.add(RespondType.SUCCESSFUL.toString());
            if (resultSet.getInt("sender_id") == clientHandler.id) {
                respond.add(resultSet.getString("receiver_id"));
                respond.add(findMemberName(resultSet.getInt("receiver_id")));
                respond.add("you : " + resultSet.getString("message"));
            } else {
                respond.add(resultSet.getString("sender_id"));
                respond.add(findMemberName(resultSet.getInt("sender_id")));
                respond.add(findMemberName(resultSet.getInt("sender_id")) + " : " + resultSet.getString("message"));
            }
        }
        clientHandler.sendMessage(respond.toString());
    }

    synchronized public void setMessage(ClientHandler clientHandler, List<String> order) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("insert into chats values (default ,?,?,?,?)");
        preparedStatement.setInt(1, clientHandler.id);
        preparedStatement.setString(2, order.get(1));
        if (findMessageSuffix(order.get(2)).equals("msg")) {
            preparedStatement.setString(3, order.get(2));
            preparedStatement.setString(4, "--");
        } else {
            preparedStatement.setString(3, order.get(2));
            //
            FileUpLoader fileUpLoader = new FileUpLoader(order);
            new Thread(fileUpLoader).start();
            //
            preparedStatement.setString(4, Config.uploadedFilesUrl + order.get(2));
        }
        preparedStatement.execute();
        getChats(clientHandler);
        for (ClientHandler i :
                Server.clients) {
            if (i.id == Integer.parseInt(order.get(1))) {
                getChats(i);
            }
        }
    }

    synchronized public String findMessageSuffix(String message) {
        int i = message.lastIndexOf('.');
        return message.substring(i + 1);
    }

    synchronized public void downloadFile(ClientHandler clientHandler, List<String> order) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from chats where message = ?");
        preparedStatement.setString(1, order.get(1));
        ResultSet resultSet = preparedStatement.executeQuery();
        List<String> respond = new ArrayList<>();
        respond.add(ServerReqType.DOWNLOAD_FILE.toString());
        if (resultSet.next()) {
            respond.add(resultSet.getString("message"));
            //
            try {
                File fileToDownload = new File(resultSet.getString("file_bytes"));
                FileInputStream fileInputStream = new FileInputStream(fileToDownload);
                int i;
                while (true) {
                    i = fileInputStream.read();
                    if (i != -1) {
                        respond.add(String.valueOf(i));
                    } else {
                        break;
                    }
                }
                fileInputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                sendUnSuccessMessage(clientHandler);
            } catch (IOException e) {
                e.printStackTrace();
                sendUnSuccessMessage(clientHandler);
            }
            //
            clientHandler.sendMessage(respond.toString());
        } else {
            sendUnSuccessMessage(clientHandler);
        }
    }


    synchronized public void getAvailablePeople(ClientHandler clientHandler) throws SQLException {
        List<String> respond = new ArrayList<>();
        respond.add(ServerReqType.GET_AVAILABLE_PEOPLE.toString());

        if (clientHandler.isStudent) {
            PreparedStatement preparedStatement1 = connection.prepareStatement("select * from sut_members join students on sut_members.id = students.id where ((sut_members.college = ? and students.enter_year = ?)  or sut_members.id = ?) and sut_members.id <> ?");
            preparedStatement1.setString(1, clientHandler.college);
            preparedStatement1.setString(2, clientHandler.enterYear);
            preparedStatement1.setInt(3, clientHandler.superVisorId);
            preparedStatement1.setInt(4, clientHandler.id);
            ResultSet resultSet1 = preparedStatement1.executeQuery();
            while (resultSet1.next()) {
                respond.add(RespondType.SUCCESSFUL.toString());
                respond.add(resultSet1.getString("sut_members.id"));
                respond.add(findMemberName(resultSet1.getInt("sut_members.id")));
            }
            clientHandler.sendMessage(respond.toString());
        } else if (clientHandler.isTeacher && !clientHandler.isEduAssistant && !clientHandler.isEduManager) {

            PreparedStatement preparedStatement = connection.prepareStatement("select * from students where supervisor_id = ? ");
            preparedStatement.setInt(1, clientHandler.id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                respond.add(RespondType.SUCCESSFUL.toString());
                respond.add(resultSet.getString("students.id"));
                respond.add(findMemberName(resultSet.getInt("students.id")));
            }
            clientHandler.sendMessage(respond.toString());
        } else if (clientHandler.isEduManager || clientHandler.isEduAssistant) {
            PreparedStatement preparedStatement2 = connection.prepareStatement("select * from sut_members where college = ? and id <> ?");
            preparedStatement2.setString(1, clientHandler.college);
            preparedStatement2.setInt(2, clientHandler.id);
            ResultSet resultSet2 = preparedStatement2.executeQuery();
            while (resultSet2.next()) {
                respond.add(RespondType.SUCCESSFUL.toString());
                respond.add(resultSet2.getString("sut_members.id"));
                respond.add(findMemberName(resultSet2.getInt("sut_members.id")));
            }
            clientHandler.sendMessage(respond.toString());
        }else if (clientHandler.isMrMohseni){
            PreparedStatement preparedStatement2 = connection.prepareStatement("select * from sut_members where id <> ?");
            preparedStatement2.setInt(1, clientHandler.id);
            ResultSet resultSet2 = preparedStatement2.executeQuery();
            while (resultSet2.next()) {
                respond.add(RespondType.SUCCESSFUL.toString());
                respond.add(resultSet2.getString("sut_members.id"));
                respond.add(findMemberName(resultSet2.getInt("sut_members.id")));
            }
            clientHandler.sendMessage(respond.toString());
        }
    }

    synchronized public void getCwEduSubjects(ClientHandler clientHandler) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from chats join student_lessons on chats.receiver_id = student_lessons.lessonid and student_lessons.teacherid = chats.sender_id where student_lessons.teacherid = ? or student_lessons.id = ?");
        preparedStatement.setInt(1,clientHandler.id);
        preparedStatement.setInt(2,clientHandler.id);
        List<String> respond = new ArrayList<>();
        respond.add(ServerReqType.GET_CW_EDU_SUBJECTS.toString());
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            respond.add(RespondType.SUCCESSFUL.toString());
            respond.add(resultSet.getString("student_lessons.lessonid"));
            respond.add(resultSet.getString("chats.message"));
        }
        clientHandler.sendMessage(respond.toString());
    }

    synchronized public void addNewHm(ClientHandler clientHandler, List<String> order) throws SQLException {
        if (clientHandler.isTeacher){
            PreparedStatement preparedStatement = connection.prepareStatement("insert into home_works values (default ,?,?,?,?,?,?,?,?)");
            preparedStatement.setString(1,order.get(1));
            preparedStatement.setInt(2,clientHandler.id);
            preparedStatement.setString(3,order.get(2));
            preparedStatement.setString(4,order.get(3));
            preparedStatement.setString(5,order.get(4));
            preparedStatement.setString(6,order.get(5));
            preparedStatement.setString(7,order.get(6));
            preparedStatement.setString(8,Config.uploadedFilesUrl + order.get(7));
            preparedStatement.execute();
            //
            sendSuccessMessage(clientHandler);
            //
        }
    }

    synchronized public void getHomeWorks(ClientHandler clientHandler) throws SQLException {
        PreparedStatement preparedStatement =connection.prepareStatement("select * from student_lessons join home_works on home_works.lesson_id = student_lessons.lessonid where student_lessons.id = ? or student_lessons.teacherid = ?");
        preparedStatement.setInt(1,clientHandler.id);
        preparedStatement.setInt(2,clientHandler.id);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<String> respond = new ArrayList<>();
        respond.add(ServerReqType.GET_HM.toString());
        while (resultSet.next()){
            respond.add(RespondType.SUCCESSFUL.toString());
            respond.add(resultSet.getString("home_works.lesson_id"));
            respond.add(resultSet.getString("home_works.start_time"));
            respond.add(resultSet.getString("home_works.finish_time"));
            respond.add(resultSet.getString("home_works.prefer_time"));
            respond.add(resultSet.getString("home_works.hm_name"));
            respond.add(resultSet.getString("home_works.exp"));
            respond.add(resultSet.getString("home_works.file"));
        }
        clientHandler.sendMessage(respond.toString());
    }

    synchronized public void getUploadedHm(ClientHandler clientHandler) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("?");
        ResultSet resultSet;
        if (clientHandler.isTeacher){
            preparedStatement = connection.prepareStatement("select * from uploaded_hm where teacher_id = ?");
        }else if (clientHandler.isStudent){
            preparedStatement = connection.prepareStatement("select * from uploaded_hm where sender_id = ?");
        }
        preparedStatement.setInt(1,clientHandler.id);
        resultSet = preparedStatement.executeQuery();
        List<String> respond =new ArrayList<>();
        respond.add(ServerReqType.GET_UPLOADED_HM.toString());
        while (resultSet.next()){
            respond.add(RespondType.SUCCESSFUL.toString());
            respond.add(resultSet.getString("sender_id"));
            respond.add(resultSet.getString("lesson_id"));
            respond.add(resultSet.getString("teacher_id"));
            respond.add(resultSet.getString("file"));
            respond.add(resultSet.getString("upload_time"));
            respond.add(resultSet.getString("score"));
        }
        clientHandler.sendMessage(respond.toString());
    }

    synchronized public void setHmScore(ClientHandler clientHandler, List<String> order) throws SQLException {
        if (clientHandler.isTeacher){
            PreparedStatement preparedStatement = connection.prepareStatement("update uploaded_hm set score = ? where sender_id = ? and lesson_id = ? and teacher_id = ?");
            preparedStatement.setString(1,order.get(3));
            preparedStatement.setString(2,order.get(1));
            preparedStatement.setString(3,order.get(2));
            preparedStatement.setInt(4,clientHandler.id);
            preparedStatement.executeUpdate();
            sendSuccessMessage(clientHandler);
        }
    }
}

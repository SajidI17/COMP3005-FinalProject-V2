import java.sql.*;
import java.util.Scanner;

public class DatabaseInterface {
    private static final String URL = "jdbc:postgresql://localhost:5432/HealthFitnessDatabase"; //Change HealthFitnessDatabase to the name of the database you have
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "admin";

    public static void main(String[] args){
        try{
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            int mainMenuItem = 1;
            Scanner obj = new Scanner(System.in);

            while(mainMenuItem != 0){
                System.out.println("Enter the number of the menu item: ");
                System.out.println("1. Member Functions");
                System.out.println("2. Trainer Functions");
                System.out.println("3. Admin Functions");
                System.out.println("0. Exit");
                mainMenuItem = obj.nextInt();

                if(mainMenuItem == 1){
                    memberMenu(connection);
                }
                else if(mainMenuItem == 2){
                    trainerMenu(connection);
                }
                else if(mainMenuItem == 3){
                    adminMenu(connection);
                }
            }


        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    //return -1 when not logged in
    //return userid if log in successful
    public static int userLogin(Connection connection) {
        try {
            Scanner obj = new Scanner(System.in);

            for(int i = 0; i < 10; i++) {
                System.out.println("Please enter your email (enter 0 to exit this menu): ");
                String email = obj.nextLine();

                if (email.equals("0")) {
                    return -1;
                }

                System.out.println("Please enter your password (enter 0 to exit this menu): ");
                String password = obj.nextLine();

                if (password.equals("0")) {
                    return -1;
                }

                String sqlStatement = "SELECT memberID FROM Member WHERE email = ? AND password = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
                preparedStatement.setString(1, email.toLowerCase());
                preparedStatement.setString(2, password);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    int memberID = resultSet.getInt("memberID");
                    System.out.println("Log in successful!");
                    return memberID;
                } else {
                    System.out.println("Invalid password or username");
                }
            }
        }
        catch(Exception e){
            System.out.println(e);
        }

        System.out.println("Too many log in attempts");
        return -1;
    }

    public static int createMember(Connection connection){
        try {
            Scanner obj = new Scanner(System.in);

            System.out.println("Enter your full name: ");
            String name = obj.nextLine();
            System.out.println("Enter your email: ");
            String email = obj.nextLine();
            System.out.println("Enter your password: ");
            String password = obj.nextLine();
            System.out.println("Enter your currentBodyFatPercentage (ie 20.50)");
            float bodyFat = obj.nextFloat();
            System.out.println("Enter your current weight (kg)");
            float weight = obj.nextFloat();
            System.out.println("Enter your desired weight goal (or enter 0 if there's no weight goal): ");
            float weightGoal = obj.nextFloat();

            String sqlStatement = "INSERT INTO Member (name, email, password, currentBodyFatPercentage, currentWeight, weightGoal)  VALUES (?, ?, ?, ?, ?, ?) RETURNING memberID";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            preparedStatement.setFloat(4, bodyFat);
            preparedStatement.setFloat(5, weight);
            preparedStatement.setFloat(6, weightGoal);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int user = resultSet.getInt("memberID");
                System.out.println("Insert Successful");
                return user;
            }
            else {
                System.out.println("Insert failed");
                return -1;
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
        return -1;
    }

    public static boolean updateCurrentWeight(Connection connection, int memberID){
        try {
            Scanner obj = new Scanner(System.in);

            System.out.println("Enter your new weight: ");
            float weight = obj.nextFloat();

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            //get current weight in profile
            String searchQuery = "SELECT currentWeight FROM Member WHERE memberID = ?";
            PreparedStatement searchStatement = connection.prepareStatement(searchQuery);
            searchStatement.setInt(1, memberID);
            ResultSet resultSet = searchStatement.executeQuery();
            resultSet.next();
            float oldWeight = resultSet.getFloat("currentWeight");


            //add old weight to healthstats table
            String sqlStatement = "INSERT INTO HealthStats (memberID, weight, timedate) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, memberID);
            preparedStatement.setFloat(2, oldWeight);
            preparedStatement.setTimestamp(3, timestamp);
            preparedStatement.executeUpdate();

            //update profile with current weight
            String updateSQL = "UPDATE Member SET currentWeight = ? WHERE memberID = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateSQL);
            updateStatement.setFloat(1, weight);
            updateStatement.setInt(2, memberID);
            updateStatement.executeUpdate();
        }
        catch(Exception e){
            System.out.println(e);
        }
        return true;
    }

    public static boolean updateProfile(Connection connection, int memberID){
        try{
            Scanner obj = new Scanner(System.in);

            System.out.println("Enter the column number you want to update: ");
            System.out.println("1 - name, 2 - email, 3 - password, 4 - currentBodyFatPercentage, 5 - currentWeight, 6 - weightGoal:");

            String updateSQL;
            PreparedStatement updateStatement;
            int userSelection = obj.nextInt();
            obj.nextLine(); //clear newline from nextInt

            if(userSelection == 1){ //update name
                updateSQL = "UPDATE Member SET name = ? WHERE memberID = ?";
                updateStatement = connection.prepareStatement(updateSQL);
                System.out.println("Enter the new name: ");
                String name = obj.nextLine();
                updateStatement.setString(1, name);
            }
            else if(userSelection == 2){ //update email
                updateSQL = "UPDATE Member SET email = ? WHERE memberID = ?";
                updateStatement = connection.prepareStatement(updateSQL);
                System.out.println("Enter the new email: ");
                String email = obj.nextLine();
                updateStatement.setString(1, email);
            }
            else if(userSelection == 3){//update password
                updateSQL = "UPDATE Member SET password = ? WHERE memberID = ?";
                updateStatement = connection.prepareStatement(updateSQL);
                System.out.println("Enter the new password: ");
                String password = obj.nextLine();
                updateStatement.setString(1, password);
            }
            else if(userSelection == 4){//update body fat percentage
                updateSQL = "UPDATE Member SET currentBodyFatPercentage = ? WHERE memberID = ?";
                updateStatement = connection.prepareStatement(updateSQL);
                System.out.println("Enter the new body fat percentage: ");
                Float bodyFat = obj.nextFloat();
                updateStatement.setFloat(1, bodyFat);
            }
            else if(userSelection == 5){//update current weight
                return updateCurrentWeight(connection, memberID);
            }
            else { //update weight goal
                updateSQL = "UPDATE Member SET weightGoal = ? WHERE memberID = ?";
                updateStatement = connection.prepareStatement(updateSQL);
                System.out.println("Enter the new weight goal: ");
                Float weightgoal = obj.nextFloat();
                updateStatement.setFloat(1, weightgoal);
            }
            updateStatement.setInt(2, memberID);
            updateStatement.executeUpdate();
        }
        catch(Exception e){
            System.out.println(e);
        }
        return true;
    }

    public static boolean addNewRoutine(Connection connection, int memberID){
        try {
            Scanner obj = new Scanner(System.in);

            System.out.println("Enter the date and time of the routine (format: yyyy-mm-dd hh:mm:ss): ");
            String dateString = obj.nextLine();
            Timestamp time = Timestamp.valueOf(dateString);

            System.out.println("Enter hours spent: ");
            float hoursSpent = obj.nextFloat();

            System.out.println("Enter average heart BPM");
            float averageBPM = obj.nextFloat();

            System.out.println("Enter calories burnt");
            int caloriesBurnt = obj.nextInt();

            //Insert values into table
            String sqlStatement = "INSERT INTO Routines (memberID, timeDate, hoursSpent, averageBPM, caloriesBurnt) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, memberID);
            preparedStatement.setTimestamp(2, time);
            preparedStatement.setFloat(3, hoursSpent);
            preparedStatement.setFloat(4, averageBPM);
            preparedStatement.setInt(5, caloriesBurnt);

            preparedStatement.executeUpdate();

            System.out.println("New routine added!");
        }
        catch(Exception e){
            System.out.println(e);
        }
        return true;
    }

    public static boolean addGroupSession(Connection connection, int trainerID){
        try {
            Scanner obj = new Scanner(System.in);

            System.out.println("Enter total spots:");
            int totalSpots = obj.nextInt();
            obj.nextLine();

            System.out.println("Enter session start date (format: yyyy-mm-dd hh:mm:ss): ");
            String startDateString = obj.nextLine();
            Timestamp startTime = Timestamp.valueOf(startDateString);

            System.out.println("Enter session end date (format: yyyy-mm-dd hh:mm:ss): ");
            String endDateString = obj.nextLine();
            Timestamp endTime = Timestamp.valueOf(endDateString);

            System.out.println("Enter session title: ");
            String title = obj.nextLine();

            String sqlStatement = "INSERT INTO TrainingSessions (trainerID, totalSpots, currentSpots, status, sessionStartDate, sessionEndDate, sessionTitle) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, trainerID);
            preparedStatement.setInt(2, totalSpots);
            preparedStatement.setInt(3, 0);//no spots taken initially
            preparedStatement.setBoolean(4, true); //session is active initially
            preparedStatement.setTimestamp(5, startTime);
            preparedStatement.setTimestamp(6, endTime);
            preparedStatement.setString(7, title);

            preparedStatement.executeUpdate();

            System.out.println("New training session added!");

        }
        catch(Exception e){
            System.out.println(e);
        }
        return true;
    }

    public static boolean updateGroupSessionSchedule(Connection connection){
        try {
            Scanner obj = new Scanner(System.in);

            System.out.println("Enter the sessionID of the group training you want to update: ");
            int sessionID = obj.nextInt();
            obj.nextLine();

            //check if group session exists
            String searchQuery = "SELECT * FROM TrainingSessions WHERE sessionID = ?";
            PreparedStatement searchStatement = connection.prepareStatement(searchQuery);
            searchStatement.setInt(1, sessionID);
            ResultSet resultSet = searchStatement.executeQuery();

            if(!resultSet.next()){
                System.out.println("Session does not exist");
                return false;
            }

            //get user input
            System.out.println("Enter NEW session start date (format: yyyy-mm-dd hh:mm:ss): ");
            String startDateString = obj.nextLine();
            Timestamp startTime = Timestamp.valueOf(startDateString);

            System.out.println("Enter NEW session end date (format: yyyy-mm-dd hh:mm:ss): ");
            String endDateString = obj.nextLine();
            Timestamp endTime = Timestamp.valueOf(endDateString);

            //update session with new start time and end time
            String sqlStatement = "UPDATE TrainingSessions SET sessionStartDate = ?, sessionEndDate = ? WHERE sessionID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setTimestamp(1, startTime);
            preparedStatement.setTimestamp(2, endTime);
            preparedStatement.setInt(3, sessionID);

            preparedStatement.executeUpdate();

            System.out.println("Schedule updated successfully!");
        }
        catch(Exception e){
            System.out.println(e);
        }
        return true;
    }

    public static boolean bookGroupSession(Connection connection, int memberID){
        try {
            Scanner obj = new Scanner(System.in);

            System.out.println("Enter the sessionID of the session you want to book: ");
            int sessionID = obj.nextInt();

            //check if user has already booked for the session
            String searchQuery = "SELECT * FROM MemberTrainingRegistration WHERE memberID = ? AND sessionID = ?";
            PreparedStatement searchStatement = connection.prepareStatement(searchQuery);
            searchStatement.setInt(1, memberID);
            searchStatement.setInt(2, sessionID);
            ResultSet resultSet = searchStatement.executeQuery();

            if(resultSet.next()){
                System.out.println("You have already booked for this session");
                return false;
            }


            //update the currentspots for the training session if spots are available
            String sqlStatement = "UPDATE TrainingSessions SET currentSpots = currentSpots + 1 WHERE sessionID = ? AND currentSpots < totalSpots";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, sessionID);
            int result = preparedStatement.executeUpdate();

            if(result == 0){//return false if session was not found or did not exist
                System.out.println("The session if full or does not exist");
                return false;
            }

            //create a record of user registering for group session
            String insertSQLStatement = "INSERT INTO MemberTrainingRegistration (memberID, sessionID) VALUES (?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertSQLStatement);
            insertStatement.setInt(1, memberID);
            insertStatement.setInt(2, sessionID);
            insertStatement.executeUpdate();

            System.out.println("You have booked successfully!");
        }
        catch(Exception e){
            System.out.println(e);
        }
        return true;
    }

    public static boolean cancelGroupSession(Connection connection, int memberID){
        try {
            Scanner obj = new Scanner(System.in);

            System.out.println("Enter the sessionID of the session you want to delete: ");
            int sessionID = obj.nextInt();

            //delete record of registering for group sessiojn
            String sqlStatement = "DELETE FROM MemberTrainingRegistration WHERE memberID = ? AND sessionID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, memberID);
            preparedStatement.setInt(2, sessionID);
            int result = preparedStatement.executeUpdate();

            if(result == 0){//return false if booking was not deleted
                System.out.println("You did not book for this session or the session did not exist");
                return false;
            }

            //update the currentspots by -1
            String sqlUpdateStatement = "UPDATE TrainingSessions SET currentSpots = currentSpots - 1 WHERE sessionID = ?";
            PreparedStatement preparedUpdateStatement = connection.prepareStatement(sqlUpdateStatement);
            preparedUpdateStatement.setInt(1, sessionID);
            preparedUpdateStatement.executeUpdate();

            System.out.println("You have successfully cancelled your booking");
        }
        catch(Exception e){
            System.out.println(e);
        }
        return true;
    }

    public static boolean updateTrainerAvailability(Connection connection, int trainerID){
        try {
            Scanner obj = new Scanner(System.in);
            System.out.println("Enter the NEW start time (format -> hh:mm)");
            String startTimeString = obj.nextLine();
            Time startTime = Time.valueOf(startTimeString + ":00");

            System.out.println("Enter the NEW end time (format -> hh:mm)");
            String endTimeString = obj.nextLine();
            Time endTime = Time.valueOf(endTimeString + ":00");

            String sqlStatement = "UPDATE Trainer SET availabilityStartTime = ?, availabilityEndTime = ? WHERE trainerID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setTime(1, startTime);
            preparedStatement.setTime(2, endTime);
            preparedStatement.setInt(3, trainerID);

            int result = preparedStatement.executeUpdate();
            System.out.println("Availability updated successfully!");
        }
        catch(Exception e){
            System.out.println(e);
        }
        return true;
    }

    public static boolean bookPersonalTrainingSession(Connection connection, int memberID){
        try{
            Scanner obj = new Scanner(System.in);

            System.out.println("Enter the trainerID of the trainer you want to train with: ");
            int trainerID = obj.nextInt();
            obj.nextLine();

            System.out.println("Enter the START time of the personal training session (format: yyyy-mm-dd hh:mm:ss)");
            String startTimeString = obj.nextLine();
            Timestamp startTime = Timestamp.valueOf(startTimeString);

            System.out.println("Enter the END time of the personal training session (format: yyyy-mm-dd hh:mm:ss)");
            String endTimeString = obj.nextLine();
            Timestamp endTime = Timestamp.valueOf(endTimeString);

            //Check if trainer is available
            String query = "SELECT * FROM Trainer WHERE trainerID = ? AND availabilityStartTime <= ? AND availabilityEndTime >= ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, trainerID);
            statement.setTimestamp(2, startTime);
            statement.setTimestamp(3, endTime);
            ResultSet resultSet = statement.executeQuery();


            if(!resultSet.next()){
                System.out.println("Trainer does not exist or is not available at that time");
                return false;
            }

            //check if the slot is already booked
            query = "SELECT * FROM MemberPersonalTrainingRegistration WHERE trainerID = ? AND ((sessionStartDate <= ? AND sessionEndDate >= ?) OR (sessionStartDate <= ? AND sessionEndDate >= ?))";
            statement = connection.prepareStatement(query);
            statement.setInt(1, trainerID);
            statement.setTimestamp(2, startTime);
            statement.setTimestamp(3, startTime);
            statement.setTimestamp(4, endTime);
            statement.setTimestamp(5, endTime);
            resultSet = statement.executeQuery();

            if(resultSet.next()){
                System.out.println("The time slot is already booked");
                return false;
            }

            //everything is good, book the session
            query = "INSERT INTO MemberPersonalTrainingRegistration (trainerID, memberID, sessionStartDate, sessionEndDate) VALUES (?, ?, ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setInt(1, trainerID);
            statement.setInt(2, memberID);
            statement.setTimestamp(3, startTime);
            statement.setTimestamp(4, endTime);
            statement.executeUpdate();

            System.out.println("Successfully booked!");
        }
        catch(Exception e){
            System.out.println(e);
        }
        return true;
    }

    public static boolean cancelPersonalTrainingSession(Connection connection){
        try {
            Scanner obj = new Scanner(System.in);

            System.out.println("Enter the Session ID of the booking you want to cancel: ");
            int sessionID = obj.nextInt();
            obj.nextLine();

            String query = "DELETE FROM MemberPersonalTrainingRegistration WHERE sessionID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, sessionID);
            int result = statement.executeUpdate();

            if(result == 0){ //return false if booking was not deleted
                System.out.println("Booking was not found");
                return false;
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
        return true;
    }

    public static void memberMenu(Connection connection){
        Scanner obj = new Scanner(System.in);
        int menuItem = 1;
        while (menuItem != 0){
            System.out.println("1. Create User");
            System.out.println("2. Log in");
            System.out.println("0. Exit");
            menuItem = obj.nextInt();

            int userID = -1;
            if(menuItem == 1){
                userID = createMember(connection);
            }
            else if(menuItem == 2){
                userID = userLogin(connection);
            }

            if(userID != -1){
                System.out.println("Logged in as user with id: " + userID);
                int secondMenuItem = 1;
                while (secondMenuItem != 0){
                    System.out.println("1. View Profile");
                    System.out.println("2. Update Profile (name, email, password, currentBodyFatPercentage, currentWeight, weightGoal)");
                    System.out.println("3. View Dashboard");
                    System.out.println("4. Add Routine");
                    System.out.println("5. View your Group Training Sessions");
                    System.out.println("6. Book Group Training Sessions");
                    System.out.println("7. Cancel Group Training Session");
                    System.out.println("8. View your Personal Training Sessions");
                    System.out.println("9. Book Personal Training Sessions");
                    System.out.println("10. Cancel Personal Training Sessions");
                    System.out.println("0. Exit");
                    secondMenuItem = obj.nextInt();

                    switch(secondMenuItem){
                        case 1:
                            printProfile(connection, userID);
                            break;
                        case 2:
                            updateProfile(connection, userID);
                            break;
                        case 3:
                            printDashboard(connection, userID);
                            break;
                        case 4:
                            addNewRoutine(connection, userID);
                            break;
                        case 5:
                            printGroupSessions(connection, userID);
                            break;
                        case 6:
                            printAllGroupSessions(connection);
                            bookGroupSession(connection, userID);
                            break;
                        case 7:
                            printGroupSessions(connection, userID);
                            cancelGroupSession(connection, userID);
                            break;
                        case 8:
                            printPersonalSessions(connection, userID);
                            break;
                        case 9:
                            printAllPersonalSessions(connection);
                            bookPersonalTrainingSession(connection, userID);
                            break;
                        case 10:
                            printPersonalSessions(connection, userID);
                            cancelPersonalTrainingSession(connection);
                    }
                }
            }
        }
    }

    public static void printProfile(Connection connection, int memberID){
        try {
            //prints the user profile
            String searchQuery = "SELECT * FROM Member WHERE memberID = ?";
            PreparedStatement statement = connection.prepareStatement(searchQuery);
            statement.setInt(1, memberID);
            ResultSet resultSet = statement.executeQuery();

            //print results
            while(resultSet.next()){
                System.out.println("Member ID: " + resultSet.getInt("memberID"));
                System.out.println("\tName: " + resultSet.getString("name"));
                System.out.println("\tEmail: " + resultSet.getString("email"));
                System.out.println("\tPassword: " + resultSet.getString("password"));
                System.out.println("\tCurrent Body Fat Percentage: " + resultSet.getFloat("currentBodyFatPercentage"));
                System.out.println("\tCurrent Weight: " + resultSet.getFloat("currentWeight"));
                System.out.println("\tWeight Goal: " + resultSet.getFloat("weightGoal"));
                System.out.println("--------------------");
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public static void printGroupSessions(Connection connection, int memberID){
        try {
            //MemberTrainingRegistration does not contain full information about group session
            //Must join with TrainingSessions to print full information about a user's booked group session
            //Joining MemberTrainingRegistration with TrainingSessions by the sessionID whenever memberID matches
            String searchQuery = "SELECT m.registrationID, t.sessionID, t.trainerID, t.totalSpots, t.currentSpots, t.status, t.sessionStartDate, t.sessionEndDate, t.sessionTitle FROM MemberTrainingRegistration m JOIN TrainingSessions t ON m.sessionID = t.sessionID WHERE m.memberID = ?";
            PreparedStatement statement = connection.prepareStatement(searchQuery);
            statement.setInt(1, memberID);
            ResultSet resultSet = statement.executeQuery();

            //print results
            while(resultSet.next()){
                System.out.println("Session Title: " + resultSet.getString("sessionTitle"));
                System.out.println("\tRegistration ID: " + resultSet.getInt("registrationID"));
                System.out.println("\tSession ID: " + resultSet.getInt("sessionID"));
                System.out.println("\tTrainer ID: " + resultSet.getInt("trainerID"));
                System.out.println("\tTotal Spots: " + resultSet.getInt("totalSpots"));
                System.out.println("\tCurrent Spots: " + resultSet.getInt("currentSpots"));
                System.out.println("\tStatus: " + resultSet.getBoolean("status"));
                System.out.println("\tSession Start Date: " + resultSet.getTimestamp("sessionStartDate"));
                System.out.println("\tSession End Date: " + resultSet.getTimestamp("sessionEndDate"));
                System.out.println("-----------");
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public static void printPersonalSessions(Connection connection, int memberID){
        try{
            String searchQuery = "SELECT * FROM MemberPersonalTrainingRegistration WHERE memberID = ?";
            PreparedStatement statement = connection.prepareStatement(searchQuery);
            statement.setInt(1, memberID);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                System.out.println("Session ID: " + resultSet.getInt("sessionID"));
                System.out.println("Trainer ID: " + resultSet.getInt("trainerID"));
                System.out.println("Member ID: " + resultSet.getInt("memberID"));
                System.out.println("Session Start Date: " + resultSet.getTimestamp("sessionStartDate"));
                System.out.println("Session End Date: " + resultSet.getTimestamp("sessionEndDate"));
                System.out.println("----------");
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public static void printDashboard(Connection connection, int memberID){
        try {
            //print all routines
            String searchQuery = "SELECT * FROM Routines WHERE memberID = ?";
            PreparedStatement statement = connection.prepareStatement(searchQuery);
            statement.setInt(1, memberID);
            ResultSet resultSet = statement.executeQuery();

            System.out.println("\nROUTINES: ");
            while (resultSet.next()) {
                System.out.println("Routine ID: " + resultSet.getInt("routineID"));
                System.out.println("\tTime and Date: " + resultSet.getTimestamp("timeDate"));
                System.out.println("\tHours Spent: " + resultSet.getFloat("hoursSpent"));
                System.out.println("\tAverage BPM: " + resultSet.getFloat("averageBPM"));
                System.out.println("\tCalories Burnt: " + resultSet.getInt("caloriesBurnt"));
                System.out.println("-----------");
            }

            //print all health stats
            searchQuery = "SELECT * FROM HealthStats WHERE memberID = ?";
            statement = connection.prepareStatement(searchQuery);
            statement.setInt(1, memberID);
            resultSet = statement.executeQuery();

            System.out.println("\nHEALTH STATS: ");
            while (resultSet.next()) {
                System.out.println("Health Stat ID: " + resultSet.getInt("healthStatID"));
                System.out.println("\tWeight: " + resultSet.getFloat("weight"));
                System.out.println("\tTime and Date: " + resultSet.getTimestamp("timeDate"));
                System.out.println("-----------");
            }

            //find the max caloriesburnt and hours spent in a routine
            searchQuery = "SELECT MAX(caloriesBurnt), MAX(hoursSpent) FROM Routines WHERE memberID = ?";
            statement = connection.prepareStatement(searchQuery);
            statement.setInt(1, memberID);
            resultSet = statement.executeQuery();
            if(resultSet.next()){
                System.out.println("Highest Calories Burnt: " + resultSet.getInt(1));
                System.out.println("Highest Hours Spent: " + resultSet.getFloat(2));
            }

        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public static void printAllGroupSessions(Connection connection){
        try {
            //Just joining, so I can print the trainerName along with the group session
            String searchQuery = "SELECT ts.*, t.name AS trainerName FROM TrainingSessions ts JOIN Trainer t ON ts.trainerID = t.trainerID";
            PreparedStatement statement = connection.prepareStatement(searchQuery);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                System.out.println("Session Title: " + resultSet.getString("sessionTitle"));
                System.out.println("Trainer Name: " + resultSet.getString("trainerName"));
                System.out.println("Session ID: " + resultSet.getInt("sessionID"));
                System.out.println("Trainer ID: " + resultSet.getInt("trainerID"));
                System.out.println("Total Spots: " + resultSet.getInt("totalSpots"));
                System.out.println("Current Spots: " + resultSet.getInt("currentSpots"));
                System.out.println("Status: " + resultSet.getBoolean("status"));
                System.out.println("Session Start Date: " + resultSet.getTimestamp("sessionStartDate"));
                System.out.println("Session End Date: " + resultSet.getTimestamp("sessionEndDate"));
                System.out.println("-----------");
            }

        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public static void printAllPersonalSessions(Connection connection){
        try {
            String searchQuery = "SELECT * FROM Trainer T";
            PreparedStatement statement = connection.prepareStatement(searchQuery);
            ResultSet resultSet = statement.executeQuery();

            //Print the trainer's availability
            System.out.println("TRAINER AVAILABILITY: \n");
            while (resultSet.next()) {
                System.out.println("Trainer Name: " + resultSet.getString("name"));
                System.out.println("\tTrainer ID: " + resultSet.getInt("trainerID"));
                System.out.println("\tAvailability Start Time (all week): " + resultSet.getTime("availabilityStartTime"));
                System.out.println("\tAvailability End Time (all week): " + resultSet.getTime("availabilityENDTime"));
                System.out.println("\tEmail: " + resultSet.getString("email"));
                System.out.println("-----------");
            }

            //Print all booked training sessions with a trainer from a member
            searchQuery = "SELECT m.*, t.name as trainerName FROM MemberPersonalTrainingRegistration as m JOIN Trainer T ON m.trainerID = t.trainerID";
            statement = connection.prepareStatement(searchQuery);
            resultSet = statement.executeQuery();

            System.out.println("\nBOOKED TIME SLOTS: \n");
            while (resultSet.next()) {
                System.out.println("Trainer Name: " + resultSet.getString("trainerName"));
                System.out.println("\tSession ID: " + resultSet.getInt("sessionID"));
                System.out.println("\tTrainer ID: " + resultSet.getInt("trainerID"));
                System.out.println("\tSession Start Date: " + resultSet.getTimestamp("sessionStartDate"));
                System.out.println("\tSession End Date: " + resultSet.getTimestamp("sessionEndDate"));
                System.out.println("-----------");
            }

        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    public static void trainerMenu(Connection connection){
        Scanner obj = new Scanner(System.in);
        int menuItem = 1;
        printAllTrainers(connection);
        System.out.println("Please enter the trainer to sign in as: ");
        int trainerID = obj.nextInt();
        while (menuItem != 0) {


            System.out.println("1. View All Trainers");
            System.out.println("2. View Personal profile");
            System.out.println("3. Search Member");
            System.out.println("4. Create a Group Training Session");
            System.out.println("5. Update Availability");
            System.out.println("0. Exit");
            menuItem = obj.nextInt();

            switch (menuItem){
                case 1:
                    printAllTrainers(connection);
                    break;
                case 2:
                    printTrainer(connection, trainerID);
                    break;
                case 3:
                    searchMember(connection);
                    break;
                case 4:
                    addGroupSession(connection, trainerID);
                    break;
                case 5:
                    updateTrainerAvailability(connection, trainerID);
                    break;
            }
        }
    }

    public static void printAllTrainers(Connection connection){
        try {
            String searchQuery = "SELECT * FROM Trainer";
            PreparedStatement statement = connection.prepareStatement(searchQuery);
            ResultSet resultSet = statement.executeQuery();

            System.out.println("TRAINERS: \n");
            while (resultSet.next()) {
                System.out.println("Trainer Name: " + resultSet.getString("name"));
                System.out.println("\tTrainer ID: " + resultSet.getInt("trainerID"));
                System.out.println("\tAvailability Start Time (all week): " + resultSet.getTime("availabilityStartTime"));
                System.out.println("\tAvailability End Time (all week): " + resultSet.getTime("availabilityENDTime"));
                System.out.println("\tEmail: " + resultSet.getString("email"));
                System.out.println("-----------");
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public static void printTrainer(Connection connection, int trainerID){
        try {
            String searchQuery = "SELECT * FROM Trainer WHERE trainerID = ?";
            PreparedStatement statement = connection.prepareStatement(searchQuery);
            statement.setInt(1, trainerID);
            ResultSet resultSet = statement.executeQuery();

            System.out.println("TRAINER: \n");
            while (resultSet.next()) {
                System.out.println("Trainer Name: " + resultSet.getString("name"));
                System.out.println("\tTrainer ID: " + resultSet.getInt("trainerID"));
                System.out.println("\tAvailability Start Time (all week): " + resultSet.getTime("availabilityStartTime"));
                System.out.println("\tAvailability End Time (all week): " + resultSet.getTime("availabilityENDTime"));
                System.out.println("\tEmail: " + resultSet.getString("email"));
                System.out.println("-----------");
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public static void searchMember(Connection connection){
        try {
            Scanner obj = new Scanner(System.in);

            System.out.println("Enter the members name");
            String name = obj.nextLine();

            String searchQuery = "SELECT * FROM Member WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(searchQuery);
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();

            //print results
            while(resultSet.next()){
                System.out.println("Member ID: " + resultSet.getInt("memberID"));
                System.out.println("\tName: " + resultSet.getString("name"));
                System.out.println("\tEmail: " + resultSet.getString("email"));
                System.out.println("\tPassword: " + resultSet.getString("password"));
                System.out.println("\tCurrent Body Fat Percentage: " + resultSet.getFloat("currentBodyFatPercentage"));
                System.out.println("\tCurrent Weight: " + resultSet.getFloat("currentWeight"));
                System.out.println("\tWeight Goal: " + resultSet.getFloat("weightGoal"));
                System.out.println("--------------------");
            }
        }
        catch(Exception e){
            System.out.println(e);
        }


    }
    public static void adminMenu(Connection connection){
        Scanner obj = new Scanner(System.in);
        int menuItem = 1;
        while (menuItem != 0) {
            System.out.println("1. View All Room Bookings");
            System.out.println("2. Create New Room Booking");
            System.out.println("3. View All Equipment");
            System.out.println("4. Update Equipment Maintenance");
            System.out.println("5. Update Group Training Session Schedule");
            System.out.println("6. Print All Recorded Bills");
            System.out.println("7. Generate Bill for User");
            System.out.println("0. Exit");
            menuItem = obj.nextInt();

            switch (menuItem){
                case 1:
                    printAllRooms(connection);
                    break;
                case 2:
                    createNewRoomBooking(connection);
                    break;
                case 3:
                    printAllEquipment(connection);
                    break;
                case 4:
                    updateEquipmentStatus(connection);
                    break;
                case 5:
                    printAllGroupSessions(connection);
                    updateGroupSessionSchedule(connection);
                    break;
                case 6:
                    printAllBills(connection);
                    break;
                case 7:
                    generateBill(connection);
                    break;
            }
        }

    }

    public static void printAllRooms(Connection connection){
        try {
            String searchQuery = "SELECT * FROM RoomBooking";
            PreparedStatement statement = connection.prepareStatement(searchQuery);
            ResultSet resultSet = statement.executeQuery();

            System.out.println("ROOM BOOKINGS: \n");
            while (resultSet.next()) {
                System.out.println("Booking ID: " + resultSet.getInt("bookingID"));
                System.out.println("\tRoom Number: " + resultSet.getInt("roomNum"));
                System.out.println("\tBooking Start Time: " + resultSet.getTimestamp("sessionStartDate"));
                System.out.println("\tBooking End Time: " + resultSet.getTimestamp("sessionEndDate"));
                System.out.println("-----------");
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public static boolean createNewRoomBooking(Connection connection){
        try {
            Scanner obj = new Scanner(System.in);

            System.out.println("Enter the room number of the room you want to book: ");
            int roomNum = obj.nextInt();
            obj.nextLine();

            System.out.println("Enter the START time for the room booking (format: yyyy-mm-dd hh:mm:ss)");
            String startTimeString = obj.nextLine();
            Timestamp startTime = Timestamp.valueOf(startTimeString);

            System.out.println("Enter the END time for the room booking (format: yyyy-mm-dd hh:mm:ss)");
            String endTimeString = obj.nextLine();
            Timestamp endTime = Timestamp.valueOf(endTimeString);

            //First check to see if there's any possible conflicts before adding
            String query = "SELECT * FROM RoomBooking WHERE roomNum = ? AND ((sessionStartDate <= ? AND sessionEndDate >= ?) OR (sessionStartDate <= ? AND sessionEndDate >= ?))";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, roomNum);
            statement.setTimestamp(2, startTime);
            statement.setTimestamp(3, startTime);
            statement.setTimestamp(4, endTime);
            statement.setTimestamp(5, endTime);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                System.out.println("The time slot is already booked");
                return false;
            }

            //everything is good, book the session
            query = "INSERT INTO RoomBooking (roomNum, sessionStartDate, sessionEndDate) VALUES (?, ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setInt(1, roomNum);
            statement.setTimestamp(2, startTime);
            statement.setTimestamp(3, endTime);
            statement.executeUpdate();

            System.out.println("Successfully booked!");
        }
        catch(Exception e){
            System.out.println(e);
            return false;
        }
        return true;
    }

    public static void printAllEquipment(Connection connection){
        try {
            String searchQuery = "SELECT * FROM Equipment";
            PreparedStatement statement = connection.prepareStatement(searchQuery);
            ResultSet resultSet = statement.executeQuery();

            System.out.println("EQUIPMENT: \n");
            while (resultSet.next()) {
                System.out.println("Equipment ID: " + resultSet.getInt("equipmentID"));
                System.out.println("\tEquipment Name: " + resultSet.getString("equipmentName"));
                System.out.println("\tMaintenance Notes: " + resultSet.getString("maintenanceStatus"));
                System.out.println("-----------");
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public static boolean updateEquipmentStatus(Connection connection){
        try {
            Scanner obj = new Scanner(System.in);

            System.out.println("Enter the equipment ID you want to update or insert: ");
            int equipmentID = obj.nextInt();
            obj.nextLine();

            System.out.println("Enter the equipment name you want to update or insert: ");
            String name = obj.nextLine();

            System.out.println("Enter any maintenance notes: ");
            String notes = obj.nextLine();

            String query = "UPDATE Equipment SET equipmentName = ?, maintenanceStatus = ? WHERE equipmentID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, notes);
            statement.setInt(3, equipmentID);
            statement.executeUpdate();

            query = "INSERT INTO Equipment (equipmentID, equipmentName, maintenanceStatus) VALUES (?, ?, ?) ON CONFLICT (equipmentID) DO NOTHING";
            statement = connection.prepareStatement(query);
            statement.setInt(1, equipmentID);
            statement.setString(2, name);
            statement.setString(3, notes);
            statement.executeUpdate();

            System.out.println("Successfully updated / inserted!");
        }
        catch(Exception e){
            System.out.println(e);
            return false;
        }
        return true;
    }

    public static void generateBill(Connection connection){
        try {
            Scanner obj = new Scanner(System.in);

            System.out.println("Enter the ID of the member you want to generate a bill for: ");
            int memberID = obj.nextInt();

            //Check if user exists first
            String query = "SELECT * FROM Member WHERE memberID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, memberID);
            ResultSet resultSet = statement.executeQuery();

            if(!resultSet.next()){
                System.out.println("User does not exist");
                return;
            }
            System.out.println("Enter the total bill for the user: ");
            float billAmount = obj.nextFloat();
            obj.nextLine();

            System.out.println("Enter payment method: (cash, credit card details (dont actually), etc)");
            String paymentDetails = obj.nextLine();

            query = "INSERT INTO Billing (memberID, billDetails, billAmount, paid) VALUES (?, ?, ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setInt(1, memberID);
            statement.setString(2, paymentDetails);
            statement.setFloat(3, billAmount);
            statement.setBoolean(4, true);
            statement.executeUpdate();

            System.out.println("Record of payment created!");
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public static void printAllBills(Connection connection){
        try {
            String searchQuery = "SELECT * FROM Billing";
            PreparedStatement statement = connection.prepareStatement(searchQuery);
            ResultSet resultSet = statement.executeQuery();

            System.out.println("Billing: \n");
            while (resultSet.next()) {
                System.out.println("Bill ID: " + resultSet.getInt("billID"));
                System.out.println("\tMember ID: " + resultSet.getInt("memberID"));
                System.out.println("\tBill Details: " + resultSet.getString("billDetails"));
                System.out.println("\tBill Amount: " + resultSet.getFloat("billAmount"));
                System.out.println("\tBill Paid: " + resultSet.getBoolean("paid"));
                System.out.println("-----------");
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}







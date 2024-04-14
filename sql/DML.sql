-- Member table
INSERT INTO Member (name, email, password, currentBodyFatPercentage, currentWeight, weightGoal) 
VALUES ('Bob Smith', 'bob@example.com', 'password123', 20.5, 85, 75);

INSERT INTO Member (name, email, password, currentBodyFatPercentage, currentWeight, weightGoal) 
VALUES ('Test Person', 'test@example.com', 'password456', 18.5, 60, 55);

-- Routines table
INSERT INTO Routines (memberID, timeDate, hoursSpent, averageBPM, caloriesBurnt) 
VALUES (1, CURRENT_TIMESTAMP, 1, 120, 500);

INSERT INTO Routines (memberID, timeDate, hoursSpent, averageBPM, caloriesBurnt) 
VALUES (2, CURRENT_TIMESTAMP, 1.5, 110, 600);

-- HealthStats table
INSERT INTO HealthStats (memberID, weight, timeDate)
VALUES (1, 85, CURRENT_TIMESTAMP);

INSERT INTO HealthStats (memberID, weight, timeDate)
VALUES (2, 60, CURRENT_TIMESTAMP);

-- Trainer table
INSERT INTO Trainer (name, email, availabilityStartTime, availabilityENDTime) 
VALUES ('Trainer Bob', 'bob@example.com', '09:00:00', '17:00:00');

INSERT INTO Trainer (name, email, availabilityStartTime, availabilityENDTime) 
VALUES ('Trainer Alice', 'alice@example.com', '12:00:00', '20:00:00');

-- TrainingSessions table
INSERT INTO TrainingSessions (trainerID, totalSpots, currentSpots, status, sessionStartDate, sessionEndDate, sessionTitle) 
VALUES (1, 10, 1, TRUE, '2024-04-01 12:00:00', '2024-04-01 13:30:00', 'Afternoon Workout');

INSERT INTO TrainingSessions (trainerID, totalSpots, currentSpots, status, sessionStartDate, sessionEndDate, sessionTitle) 
VALUES (2, 10, 1, TRUE, '2024-04-10 08:00:00', '2024-04-10 10:00:00', 'Awesome Workout');

-- MemberPersonalTrainingRegistration table
INSERT INTO MemberPersonalTrainingRegistration (trainerID, memberID, sessionStartDate, sessionEndDate) 
VALUES (1, 1, '2024-04-15 10:00:00', '2024-04-15 11:30:00');

INSERT INTO MemberPersonalTrainingRegistration (trainerID, memberID, sessionStartDate, sessionEndDate) 
VALUES (2, 2, '2024-04-20 14:00:00', '2024-04-20 15:00:00');

-- MemberTrainingRegistration table
INSERT INTO MemberTrainingRegistration (memberID, sessionID) 
VALUES (1, 1);

INSERT INTO MemberTrainingRegistration (memberID, sessionID) 
VALUES (2, 2);

-- Equipment table
INSERT INTO Equipment (equipmentID, equipmentName, maintenanceStatus) 
VALUES (1, 'Treadmill', 'Need to fix, corporate has been contacted');

INSERT INTO Equipment (equipmentID, equipmentName, maintenanceStatus) 
VALUES (2, 'Dumbbells', 'Little bit worn');

-- Billing table
INSERT INTO Billing (memberID, billDetails, billAmount, paid) 
VALUES (1, 'Membership Fee', 100, TRUE);

INSERT INTO Billing (memberID, billDetails, billAmount, paid) 
VALUES (2, 'Personal Training Fee', 50, TRUE);

-- RoomBooking table
INSERT INTO RoomBooking (roomNum, sessionStartDate, sessionEndDate) 
VALUES (101, '2024-04-13 12:00:00', '2024-04-13 12:30:00');

INSERT INTO RoomBooking (roomNum, sessionStartDate, sessionEndDate) 
VALUES (102, '2024-04-14 15:00:00', '2024-04-14 16:45:00');
CREATE TABLE Member (
    memberID SERIAL PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100),
    password VARCHAR(100),
    currentBodyFatPercentage REAL,
    currentWeight REAL,
    weightGoal REAL
);

CREATE TABLE Routines (
    routineID SERIAL PRIMARY KEY,
    memberID INT,
    timeDate TIMESTAMP,
    hoursSpent REAL,
    averageBPM REAL,
    caloriesBurnt INT,

    FOREIGN KEY (memberID) REFERENCES Member(memberID)
);

CREATE TABLE HealthStats (
    healthStatID SERIAL PRIMARY KEY,
    memberID INT,
    weight REAL,
    timeDate TIMESTAMP,

    FOREIGN KEY (memberID) REFERENCES Member(memberID)
);

CREATE TABLE Trainer (
    trainerID SERIAL PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100),
    availabilityStartTime TIME,
    availabilityENDTime TIME
);

CREATE TABLE TrainingSessions (
    sessionID SERIAL PRIMARY KEY,
    trainerID INT,
    totalSpots INT,
    currentSpots INT,
    status BOOLEAN,
    sessionStartDate TIMESTAMP,
    sessionEndDate TIMESTAMP,
    sessionTitle VARCHAR(100),

    FOREIGN KEY (trainerID) REFERENCES Trainer(trainerID)
);

CREATE TABLE MemberPersonalTrainingRegistration (
    sessionID SERIAL PRIMARY KEY,
    trainerID INT,
    memberID INT,
    sessionStartDate TIMESTAMP,
    sessionEndDate TIMESTAMP,

    FOREIGN KEY (trainerID) REFERENCES Trainer(trainerID),
    FOREIGN KEY (memberID) REFERENCES Member(memberID)
);

CREATE TABLE MemberTrainingRegistration (
    registrationID SERIAL PRIMARY KEY,
    memberID INT,
    sessionID INT,

    FOREIGN KEY (memberID) REFERENCES Member(memberID),
    FOREIGN KEY (sessionID) REFERENCES TrainingSessions(sessionID)
);

CREATE TABLE Equipment (
    equipmentID INT UNIQUE PRIMARY KEY,
    equipmentName VARCHAR(100),
    maintenanceStatus VARCHAR(100)
);

CREATE TABLE Billing (
    billID SERIAL PRIMARY KEY,
    memberID INT,
    billDetails VARCHAR(100),
    billAmount REAL,
    paid BOOLEAN,

    FOREIGN KEY (memberID) REFERENCES Member(memberID)
);

CREATE TABLE RoomBooking (
    bookingID SERIAL PRIMARY KEY,
    roomNum INTEGER,
    sessionStartDate TIMESTAMP,
    sessionEndDate TIMESTAMP
); 
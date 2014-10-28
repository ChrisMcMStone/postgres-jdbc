Create Table Titles (

titleID serial NOT NULL,
titleString varchar(20) NOT NULL,

PRIMARY KEY (titleID)
);

Create Table Student (

studentID serial NOT NULL, 
titleID seria NOT NULL,
forename varchar(35) NOT NULL,
familyName varchar(35) NOT NULL,
dateOfBirth date NOT NULL CHECK(dateOfBirth < current_date),

PRIMARY KEY (studentID),
FOREIGN KEY (titleID) REFERENCES Titles(titleID)
);

Create Table Lecturer (

lecturerID serial NOT NULL,
titleID serial NOT NULL,
forename varchar(35) NOT NULL,
familyName varchar(35) NOT NULL,

PRIMARY KEY (lecturerID),
FOREIGN KEY (titleID) REFERENCES Titles(titleID)
);

Create Table Module (

moduleID serial NOT NULL,
moduleName varchar(50) NOT NULL,
moduleDescription text NOT NULL,
lecturerID serial NOT NULL,

PRIMARY KEY (moduleID),
FOREIGN KEY (lecturerID) REFERENCES Lecturer(lecturerID)
);

Create Table Type (
typeID serial NOT NULL,
typeString varchar(20) NOT NULL,

PRIMARY KEY (typeID)
);

Create Table Marks (

studentID serial NOT NULL,
moduleID serial NOT NULL,
year smallint NOT NULL,
typeID serial NOT NULL,
mark smallint CHECK(mark <= 100),
notes text,

FOREIGN KEY (studentID) REFERENCES Student(studentID),
FOREIGN KEY (moduleID) REFERENCES Module(moduleID),
FOREIGN KEY (typeID) REFERENCES Type(typeID)
);

Create Table StudentContact (

studentID serial NOT NULL,
eMailAddress varchar(254) NOT NULL,
postalAddress varchar(8) NOT NULL,

FOREIGN KEY (studentID) REFERENCES Student(studentID)
);

Create Table NextOfKinContact (

studentID serial NOT NULL,
eMailAddress varchar(254) NOT NULL,
postalAddress varchar(8) NOT NULL,

FOREIGN KEY (studentID) REFERENCES Student(studentID)
);

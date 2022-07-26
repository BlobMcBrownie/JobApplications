DROP TABLE IF EXISTS demo;

DROP TABLE IF EXISTS University CASCADE;
CREATE TABLE IF NOT EXISTS University (
	uniCode INTEGER PRIMARY KEY,
	uniName VARCHAR(114) NOT NULL,
	country CHAR(2) NOT NULL,
	website VARCHAR(66),
	QSranking INTEGER,
	CONSTRAINT university_check_QSranking CHECK
		((QSranking is NULL) OR (QSranking > 0))
) WITH (
  OIDS=FALSE
);

DROP TABLE IF EXISTS Cooperation CASCADE;
CREATE TABLE IF NOT EXISTS Cooperation (
	uniCode1 INTEGER NOT NULL,
	uniCode2 INTEGER NOT NULL,
	PRIMARY KEY (uniCode1, uniCode2),
	CONSTRAINT Cooperation_fk0 FOREIGN KEY (uniCode1) REFERENCES University(uniCode),
	CONSTRAINT Cooperation_fk1 FOREIGN KEY (uniCode2) REFERENCES University(uniCode),
	CONSTRAINT Cooperation_fkeys CHECK
		(uniCode1 <> uniCode2)
) WITH (
  OIDS=FALSE
);

DROP TABLE IF EXISTS Rector CASCADE;
CREATE TABLE IF NOT EXISTS Rector (
	fullName VARCHAR(50) NOT NULL,
	uniCode INTEGER NOT NULL,
	PRIMARY KEY (fullName, uniCode),
	CONSTRAINT Rector_fk 
		FOREIGN KEY (uniCode) 
		REFERENCES University(uniCode)
		ON UPDATE CASCADE
		ON DELETE CASCADE
) WITH (
  OIDS=FALSE
);

DROP TABLE IF EXISTS Title CASCADE;
CREATE TABLE IF NOT EXISTS Title (
	fullName VARCHAR(50) NOT NULL,
	uniCode INTEGER NOT NULL,
	title VARCHAR(10) NOT NULL,
	PRIMARY KEY (fullName, uniCode, title),
	CONSTRAINT Title_fk0 
		FOREIGN KEY (fullName, uniCode) 
		REFERENCES Rector(fullName, uniCode)
		ON UPDATE CASCADE
		ON DELETE CASCADE
) WITH (
  OIDS=FALSE
);

DROP TABLE IF EXISTS Program CASCADE;
CREATE TABLE IF NOT EXISTS Program (
	programCode INTEGER,
	programName VARCHAR(50) NOT NULL,
	inLanguage VARCHAR(10) NOT NULL,
	startSemester VARCHAR(10) NOT NULL,
	livingExpenses INTEGER NOT NULL,
	tuition INTEGER NOT NULL,
	studySupplies INTEGER NOT NULL,
	PRIMARY KEY (programCode),
	CONSTRAINT program_check_startSemester CHECK
		(startSemester IN ('S', 'W')),
	CONSTRAINT program_check_costs CHECK
		((livingExpenses >= 0) AND (tuition >= 0) AND (studySupplies >= 0))
) WITH (
  OIDS=FALSE
);

DROP TABLE IF EXISTS Offer CASCADE;
CREATE TABLE IF NOT EXISTS Offer (
	uniCode INTEGER,
	programCode INTEGER,
	PRIMARY KEY (uniCode, programCode),
	CONSTRAINT Offer_fk0 FOREIGN KEY (uniCode) REFERENCES University(uniCode),
	CONSTRAINT Offer_fk1 FOREIGN KEY (programCode) REFERENCES Program(programCode)
) WITH (
  OIDS=FALSE
);

DROP TABLE IF EXISTS Course CASCADE;
CREATE TABLE IF NOT EXISTS Course (
	courseCode INTEGER PRIMARY KEY,
	courseName VARCHAR(80),
	teacher VARCHAR(50),
	credits INTEGER NOT NULL,
	semester CHAR(1) NOT NULL,
	UNIQUE (courseName,teacher),
	CONSTRAINT course_check_semester CHECK
		(semester IN ('S', 'W')),
	CONSTRAINT course_check_credits CHECK
		((credits >= 0) AND (credits <= 30))
) WITH (
  OIDS=FALSE
);

DROP TABLE IF EXISTS VoluntaryCourse CASCADE;
CREATE TABLE IF NOT EXISTS VoluntaryCourse (
	courseCode INTEGER NOT NULL,
	CONSTRAINT VoluntaryCourse_fk0 
		FOREIGN KEY (courseCode) 
		REFERENCES Course(courseCode)
		ON UPDATE CASCADE
		ON DELETE CASCADE
) WITH (
  OIDS=FALSE
);

DROP TABLE IF EXISTS MandatoryCourse CASCADE;
CREATE TABLE IF NOT EXISTS MandatoryCourse (
	courseCode INTEGER NOT NULL,
	coreORspecialization VARCHAR(1) NOT NULL,
	CONSTRAINT MandatoryCourse_fk0
		FOREIGN KEY (courseCode)
		REFERENCES Course(courseCode)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	CONSTRAINT mandatoryCourse_check_coreORspecialization CHECK
		(coreORspecialization IN ('C', 'S'))
) WITH (
  OIDS=FALSE
);

DROP TABLE IF EXISTS Provision CASCADE;
CREATE TABLE IF NOT EXISTS Provision (
	programCode INTEGER NOT NULL,
	courseCode INTEGER NOT NULL,
	CONSTRAINT Provision_fk0 FOREIGN KEY (programCode) REFERENCES Program(programCode),
	CONSTRAINT Provision_fk1 FOREIGN KEY (courseCode) REFERENCES Course(courseCode)
) WITH (
  OIDS=FALSE
);

DROP TABLE IF EXISTS Exam CASCADE;
CREATE TABLE IF NOT EXISTS Exam (
	courseCode INTEGER,
	examLength INTEGER,
	PRIMARY KEY (courseCode, examLength),
	CONSTRAINT Exam_fk0 
		FOREIGN KEY (courseCode) 
		REFERENCES Course(courseCode)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	CONSTRAINT exam_check_length CHECK
		((examLength IS NULL) OR (examLength > 0))
) WITH (
  OIDS=FALSE
);

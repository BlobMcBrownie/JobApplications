-- transaction

/*T1*/ BEGIN TRANSACTION ISOLATION LEVEL REPEATABLE READ;
/*T2*/ BEGIN TRANSACTION;
/*T1*/ SELECT * FROM Program ORDER BY programcode LIMIT 5;
/*T2*/ UPDATE Program SET livingExpenses = 800 WHERE programcode = 0;
/*T2*/ COMMIT TRANSACTION;
/*T1*/ SELECT * FROM Program ORDER BY programcode LIMIT 5;
/*T1*/ UPDATE Program SET livingExpenses = 800 WHERE programcode = 0;
-- ERROR

/*T1*/ COMMIT TRANSACTION;
/*T1*/ SELECT * FROM Program ORDER BY programcode LIMIT 5;

-- view
CREATE VIEW CzechUnis AS
	SELECT * FROM University
	WHERE (country = 'CZ')

CREATE VIEW WinterCoreTeachersStartingWithA AS
	SELECT * 
	FROM mandatorycourse NATURAL JOIN course
	WHERE (coreORspecialization = 'C') AND (semester = 'W') AND (teacher LIKE 'A%')

CREATE VIEW people AS
	SELECT R.fullName as Employees
	FROM Rector as R
	UNION
	SELECT C.teacher as Employees
	FROM Course as C
	ORDER BY Employees;

-- trigger

-- DROP TABLE IF EXISTS TeacherChanges CASCADE;
-- CREATE TABLE IF NOT EXISTS TeacherChanges (
-- 	courseCode INTEGER PRIMARY KEY,
-- 	courseName VARCHAR(80),
-- 	oldTeacher VARCHAR(50),
-- 	newTeacher VARCHAR(50),
-- 	changed_on TIMESTAMP(6) NOT NULL
-- );

-- CREATE OR REPLACE FUNCTION log_teacher_changes()
--   RETURNS TRIGGER 
--   LANGUAGE PLPGSQL
-- AS $$
-- BEGIN
-- 	IF NEW.teacher <> OLD.teacher THEN
-- 		 INSERT INTO TeacherChanges(courseCode,courseName,oldTeacher,newTeacher,changed_on)
-- 		 VALUES(OLD.courseCode,OLD.courseName,OLD.teacher,NEW.teacher,now());
-- 	END IF;

-- 	RETURN NEW;
-- END;
-- $$;

-- CREATE TRIGGER exam_tg_length BEFORE INSERT OR UPDATE ON exam
-- FOR EACH ROW EXECUTE PROCEDURE check_examLength()

-- trigger if you add an exam, its course credits get incremented
CREATE OR REPLACE FUNCTION new_exam_add_credits()
  RETURNS TRIGGER 
  LANGUAGE PLPGSQL
AS $$
BEGIN
	IF NEW.examLength > 30 THEN
		UPDATE Course 
		 	SET credits = credits + 1 
			WHERE courseCode = NEW.courseCode;
	END IF;
	RETURN NEW;
END;
$$;

CREATE TRIGGER course_tg_credits_examlength 
	BEFORE INSERT 
	ON Exam
	EXECUTE PROCEDURE new_exam_add_credits();



-- INDEX
-- v1
CREATE INDEX course_i_teacher ON course(teacher);
-- this is faster than LIKE %%
EXPLAIN ANALYZE SELECT teacher FROM course WHERE teacher = 'Kevin Cline';
SELECT DISTINCT * FROM people;


-- v2
CREATE INDEX uni_offers ON offer(uniCode,programCode);

SELECT uniCode, programCode FROM offer 
WHERE (uniCode = 2593) AND (programCode = 51);

-- v3
CREATE INDEX offer_i_both ON offer(uniCode, programCode);
EXPLAIN SELECT uniCode FROM OFFER 
WHERE ((programCode > 100) AND (uniCode < 20));


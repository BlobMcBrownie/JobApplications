-- velka tabulka
SELECT COUNT(*) FROM offer;

-- vnější spojení tabulek
SELECT Rector.*, Title.title
FROM Rector LEFT OUTER JOIN Title
ON Rector.fullName = Title.fullName;

-- vnitřní spojení tabulek
SELECT Course.*, Exam.examlength
FROM Course INNER JOIN Exam
ON Course.courseCode = Exam.courseCode
WHERE Exam.examLength > 60;

-- podmínku na data
SELECT programName, inLanguage, livingExpenses, tuition, studySupplies
FROM program
WHERE (startSemester = 'W') AND (livingexpenses + tuition + studySupplies < 2000)
ORDER BY livingExpenses, tuition, studySupplies ASC;

-- agregaci a podmínku na hodnotu agregační funkce
SELECT programName, inLanguage, startSemester, SUM(livingExpenses + tuition + studySupplies) as costs
FROM program
GROUP BY programCode
HAVING livingExpenses > (SELECT AVG(livingExpenses) FROM Program)
ORDER BY costs DESC;

-- řazení a stránkování
SELECT *
FROM University
ORDER BY qsranking DESC
LIMIT 5;

-- množinové operace
SELECT R.fullName as Employees
FROM Rector as R
UNION
SELECT C.teacher as Employees
FROM Course as C
ORDER BY Employees;

-- vnořený SELECT
SELECT uniName, website
FROM University
WHERE uniCode IN
(
  	SELECT uniCode 
	FROM University NATURAL JOIN Offer
	WHERE (country = 'CZ')
);

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Edit Meal</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<h2>Edit Meal</h2>
<form method="POST" action='meals'>
    <input type="hidden" readonly="readonly" name="mealId"
           value="<c:out value="${meal.getId()}" />"/> <br/>
    DateTime : <input
        type="datetime-local" name="dateTime"
        value="<c:out value="${meal.dateTime}" />"/> <br/>
    Description : <input
        type="text" name="description"
        value="<c:out value="${meal.description}" />"/> <br/>

    Calories : <input type="text" name="calories"
                      value="<c:out value="${meal.calories}" />"/> <br/>
    <p><input type="submit" value="Submit"/>
        <a href="meals"><input type="button" value="Cancel"></a></p>
</form>
</body>
</html>

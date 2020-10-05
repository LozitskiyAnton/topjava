<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="ru">
<head>
    <title>Meals</title>
</head>
<body>
<h2>Meals</h2>

<div>
    <h3><a href="index.html">Home</a></h3>
    <br>
    <h3><a href="meals?action=insert">add Meal</a></h3>
    <table border=1>
        <tr>
            <th>dateTime</th>
            <th>description</th>
            <th>calories</th>
            <th>update</th>
            <th>delete</th>
        </tr>
        <c:forEach items="${mealTo}" var="meal">
            <tr style="color:${meal.excess ? 'red' : 'green' }">
                <td><fmt:parseDate value="${ meal.dateTime }" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime"
                                   type="both"/>
                    <fmt:formatDate pattern="yyyy-MM-dd HH:mm" value="${ parsedDateTime }"/>
                </td>
                <td>${meal.description}</td>
                <td>${meal.calories}</td>
                <td><a href="meals?action=edit&mealId=${meal.id}">Update</a></td>
                <td><a href="meals?action=delete&mealId=${meal.id}">Delete</a></td>
            </tr>
        </c:forEach>
    </table>

</div>

</body>
</html>

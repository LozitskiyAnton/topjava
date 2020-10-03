<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="ru">
<head>
    <title>Meals</title>
</head>
<body>
<h2>Meals</h2>

<div>
    <table border =1>
        <tr>
            <th>dateTime</th>
            <th>description</th>
            <th>calories</th>
        </tr>
        <c:forEach items="${mealTo}" var="meal">
            <tr style="color:${meal.excess ? 'green' : 'red'}">
                <td><fmt:parseDate value="${ meal.dateTime }" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime"
                                   type="both"/>
                    <fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="${ parsedDateTime }"/>
                </td>
                <td>${meal.description}</td>
                <td>${meal.calories}</td>
            </tr>
        </c:forEach>
    </table>

</div>
<h3><a href="index.html">Home</a></h3>
</body>
</html>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="${sessionScope.language}">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Solar BIM</title>

    <jsp:include page="${contextPath}/WEB-INF/jsp/common/css.jsp"/>

</head>
<body>
<jsp:include page="${contextPath}/WEB-INF/jsp/common/navbar.jsp"/>
<div class="container" role="main">
    <div class="row">
        <jsp:include page="${contextPath}/WEB-INF/jsp/common/navigation.jsp"/>
        <div class="col-xs-10 col-md-10 col-lg-10">
            <c:if test="${empty requestScope.local_data}">
                Тип локальных данных не указан
            </c:if>
            <form action="${contextPath}/projects/local_data/info" id="form">
                <input type="hidden" name="id" value="${requestScope.power_station.id}">
                <p><select size="3" name="param">
                    <option disabled>Выберите параметр</option>
                    <option value="direct_radiation">Прямая радиация</option>
                    <option value="diffuse_radiation">Диффузная радиация</option>
                    <option value="wind_speed">Скорость ветра</option>
                    <option value="temperature">Температцра воздуха</option>
                </select></p>
                <input type="submit" value="Выбрать">
            </form>
            <c:if test="${not empty requestScope.local_data}">
                <jsp:useBean id="local_data" type="edu.spbpu.models.LocalData" scope="request"/>

                <table class="table table-bordered">
                    <thead>
                    <tr>
                        <td>Время</td>
                        <td>Величина</td>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${local_data.data}" var="entry">
                        <tr>
                            <td><c:out value="${entry.key}"/></td>
                            <td><c:out value="${entry.value}"/></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:if>
        </div>
    </div>
</div>
<jsp:include page="${contextPath}/WEB-INF/jsp/common/scripts.jsp"/>
</body>
</html>
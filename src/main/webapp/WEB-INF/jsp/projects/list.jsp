<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
            <c:set var="projectList" value="${requestScope.project_list}"/>
            <c:if test="${empty projectList}">Вы ещё не создавали проекты</c:if>
            <c:if test="${not empty projectList}">
                <table class="table table-bordered">
                    <thead>
                    <tr>
                        <th>№</th>
                        <th>Название</th>
                        <th>Расположение</th>
                        <th>Мощность</th>
                        <th>Модель панели</th>
                        <th>Модель инвертора</th>
                        <th>Часовой пояс</th>
                    </tr>
                    </thead>
                    <tbody>

                    <c:forEach items="${projectList}" var="powerStation">
                        <jsp:useBean id="powerStation" class="edu.spbpu.models.PowerStation" scope="page"/>
                        <tr>
                            <td>${powerStation.id}</td>
                            <td><a href="${contextPath}/projects/info?id=${powerStation.id}">${powerStation.name}</a></td>
                            <td>${powerStation.position}</td>
                            <td>${powerStation.nominalPower}</td>
                            <td><c:if
                                    test="${not empty powerStation.solarModule}">${powerStation.solarModule.name}</c:if><c:if
                                    test="${empty powerStation.solarModule}">Панель не выбрана</c:if></td>
                            <td><c:if
                                    test="${not empty powerStation.inverter}">${powerStation.inverter.name}</c:if><c:if
                                    test="${empty powerStation.inverter}">Инвертор не выбран</c:if></td>
                            <td>${powerStation.timeZone}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:if>
            <a href="${pageContext.request.contextPath}/projects/add">Создать новую станцию</a>
        </div>
    </div>
</div>
<jsp:include page="${contextPath}/WEB-INF/jsp/common/scripts.jsp"/>
</body>
</html>
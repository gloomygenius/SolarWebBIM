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
            <jsp:useBean id="power_station" type="edu.spbpu.models.PowerStation" scope="request"/>
            <h3>Состав электрической цепи на 1 инвертор</h3>
            <p>Количество подключений к инвертору: <c:out value="${power_station.inverter.connections}"/>
                Количество паралельных эл. цепей в одном подключении: <c:out
                        value="${power_station.string.amountString}"/>
                Количество модулей в составе одной электрической цепи: <c:out
                        value="${power_station.string.battery.amountOfPaneles}"/></p>
            <p>Длины кабелей:</p>
            <table class="table table-bordered">
                <thead>
                <tr>
                    <td>Кабель</td>
                    <td>Длина, м</td>
                    <td>Сечение, мм<sup>2</sup></td>
                </tr>
                </thead>
                <tbody>
                <c:set var="i" scope="page" value="1"/>
                <c:forEach items="${power_station.string.cables}" var="cable">
                    <tr>
                        <td>Эл. цепь №<c:out value="${i}"/></td>
                        <s:set var="i" value="${i+1}"/>
                        <td><c:out value="${cable.length}"/></td>
                        <td><c:out value="${cable.crossSection}"/> мм<sup>2</sup></td>
                    </tr>
                </c:forEach>
                <c:set var="i" value="1"/>
                <c:forEach items="${power_station.cablesFromStringToInverter}" var="cable">
                    <tr>
                        <td>Длина подключения №<c:out value="${i}"/></td>
                        <s:set var="i" value="${i+1}"/>
                        <td><c:out value="${cable.length}"/></td>
                        <td><c:out value="${cable.crossSection}"/> мм<sup>2</sup></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>
<jsp:include page="${contextPath}/WEB-INF/jsp/common/scripts.jsp"/>
</body>
</html>
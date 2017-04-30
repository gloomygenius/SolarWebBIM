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
            <jsp:useBean id="power_station" type="edu.spbpu.models.PowerStation" scope="request"/>
            <p>Количество подключений к инвертору: <c:out value="${power_station.inverter.connections}"/><br>Количество
                паралельных цепей: <c:out value="${power_station.string.amountString}"/><br>Количество модулей
                в одной цепи: <c:out value="${power_station.string.battery.amountOfPaneles}"/><br>Размеры модуля -
                ширина:<c:out value="${power_station.solarModule.length}"/> высота: <c:out
                        value="${power_station.solarModule.width}"/></p>
            <div><img src="${contextPath}/img/electro_circuit.png"></div>
            <form action="${contextPath}/projects/string/cable_edit" method="post">
                <input hidden name="id" value="${power_station.id}">
                <c:forEach var="i" begin="1" end="${power_station.string.amountString}">
                    <div>
                        <label for="circuit_${i}">Длина цепи №<c:out value="${i}"/>: </label>
                        <input type="number" value="100" name="m_string_length[]" id="circuit_${i}">
                    </div>
                </c:forEach>

                <c:forEach var="i" begin="1" end="${power_station.inverter.connections}">
                    <div>
                        <label for="is_circuit_${i}">Длина кабеля от инвертора до сборки панелей №<c:out
                                value="${i}"/>: </label>
                        <input type="number" value="200" name="is_string_length[]" id="is_circuit_${i}">
                    </div>
                </c:forEach>
                <input type="submit">
            </form>
        </div>
    </div>
</div>
<jsp:include page="${contextPath}/WEB-INF/jsp/common/scripts.jsp"/>
</body>
</html>
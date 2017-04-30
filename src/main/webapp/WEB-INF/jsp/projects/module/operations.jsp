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
            <jsp:useBean id="power_station" scope="request" type="edu.spbpu.models.PowerStation"/>
            <p>Моделирование работы панели <c:out value="${power_station.solarModule.name}"/></p>
            <table class="table table-bordered">
                <thead>
                <tr>
                    <th>Время</th>
                    <th>Iкз</th>
                    <th>Imp</th>
                    <th>Vmp</th>
                    <th>Vхх</th>
                    <th>Pmp</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>Максимальные значения</td>
                    <td><c:out value="${power_station.maxOperation.scCurrent}"/></td>
                    <td><c:out value="${power_station.maxOperation.mpCurrent}"/></td>
                    <td><c:out value="${power_station.maxOperation.mpVolatage}"/></td>
                    <td><c:out value="${power_station.maxOperation.ocVoltage}"/></td>
                    <td><c:out value="${power_station.maxOperation.maxPower}"/></td>
                </tr>
                <c:forEach items="${requestScope.operation_map}" var="entry">
                    <c:set var="operation" value="${entry.value}"/>
                    <jsp:useBean id="operation" type="edu.spbpu.models.SolarModule.Operation" scope="page"/>
                    <tr>
                        <td><c:out value="${entry.key}"/></td>
                        <td><c:out value="${operation.scCurrent}"/></td>
                        <td><c:out value="${operation.mpCurrent}"/></td>
                        <td><c:out value="${operation.mpVolatage}"/></td>
                        <td><c:out value="${operation.ocVoltage}"/></td>
                        <td><c:out value="${operation.maxPower}"/></td>
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
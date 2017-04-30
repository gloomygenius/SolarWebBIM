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
            <c:set var="moduleList" value="${requestScope.module_list}"/>
            <c:if test="${empty moduleList}">Панелей в базе нет</c:if>
            <c:if test="${not empty moduleList}">
                <table class="table table-bordered">
                    <thead>
                    <tr>
                        <th>Название</th>
                        <th>Tnoct</th>
                        <th>Площадь, м2</th>
                        <th>Iкз</th>
                        <th>Vхх</th>
                        <th>Iмм</th>
                        <th>Vмм</th>
                        <th>α</th>
                        <th>β</th>

                    </tr>
                    </thead>
                    <tbody>

                    <c:forEach items="${moduleList}" var="powerStation">
                        <jsp:useBean id="module" class="edu.spbpu.models.SolarModule" scope="page"/>
                        <tr>
                            <td>${powerStation.name}</td>
                            <td>${powerStation.tnoct}</td>
                            <td>${powerStation.area}</td>
                            <td>${powerStation.scCurrentRef}</td>
                            <td>${powerStation.ocVoltageRef}</td>
                            <td>${powerStation.i_mp_REF}</td>
                            <td>${powerStation.v_mp_REF}</td>
                            <td>${powerStation.alphaSC}</td>
                            <td>${powerStation.bettaOC}</td>
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
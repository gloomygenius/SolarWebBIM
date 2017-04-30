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
            <jsp:useBean id="module" type="edu.spbpu.models.SolarModule" scope="request"/>
            <p>Информация о модуле <c:out value="${module.name}"/>:</p>
            <table class="table table-bordered">
                <tbody>
                <tr>
                    <td>ID</td>
                    <td><c:out value="${module.id}"/></td>
                </tr>
                <tr>
                    <td>Название</td>
                    <td><c:out value="${module.name}"/></td>
                </tr>
                <tr>
                    <td>Размеры</td>
                    <td><c:out value="${module.width}"/>x<c:out value="${module.length}"/> м</td>
                </tr>
                <tr>
                    <td>Ток короткого замыкания (SRC)</td>
                    <td><c:out value="${module.scCurrentRef}"/> А</td>
                </tr>
                <tr>
                    <td>Напряжение холостого хода (SRC)</td>
                    <td><c:out value="${module.ocVoltageRef}"/> В</td>
                </tr>
                <tr>
                    <td>Ток максимальной мощности (SRC)</td>
                    <td><c:out value="${module.i_mp_REF}"/> А</td>
                </tr>
                <tr>
                    <td>Напряжение максимальной мощности (SRC)</td>
                    <td><c:out value="${module.v_mp_REF}"/> В</td>
                </tr>
                <tr>
                    <td>Коэффициент α<sub>sc</sub></td>
                    <td><c:out value="${module.alphaSC}"/></td>
                </tr>

                <tr>
                    <td>Коэффициент β<sub>oc</sub></td>
                    <td><c:out value="${module.bettaOC}"/></td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>
<jsp:include page="${contextPath}/WEB-INF/jsp/common/scripts.jsp"/>
</body>
</html>
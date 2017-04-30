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
            <jsp:useBean id="inverter" type="edu.spbpu.models.Inverter" scope="request"/>
            <p>Информация о инверторе <c:out value="${inverter.name}"/>:</p>
            <table class="table table-bordered">
                <tbody>
                <tr>
                    <td>ID</td>
                    <td><c:out value="${inverter.id}"/></td>
                </tr>
                <tr>
                    <td>Название:</td>
                    <td><c:out value="${inverter.name}"/></td>
                </tr>
                <tr>
                    <td>Номинальная мощность переменного тока:</td>
                    <td><c:out value="${inverter.p_ac0}"/> Вт</td>
                </tr>
                <tr>
                    <td>Номинальная мощность постоянного тока:</td>
                    <td><c:out value="${inverter.p_dc0}"/> Вт</td>
                </tr>
                <tr>
                    <td>Номинальное напряжение переменного тока:</td>
                    <td><c:out value="${inverter.v_ac0}"/> В</td>
                </tr>
                <tr>
                    <td>Номинальное напряжение постоянного тока:</td>
                    <td><c:out value="${inverter.v_dc0}"/> В</td>
                </tr>
                <tr>
                    <td>Максимальное напряжение постоянного тока:</td>
                    <td><c:out value="${inverter.v_dcmax}"/> В</td>
                </tr>
                <tr>
                    <td>Максимальная величина постоянного тока:</td>
                    <td><c:out value="${inverter.i_dcmax}"/> А</td>
                </tr>
                <tr>
                    <td>Потери мощности на собственные нужды (при номинальной мощности):</td>
                    <td><c:out value="${inverter.p_s0}"/> Вт</td>
                </tr>
                <tr>
                    <td>Потери мощности в ночное время:</td>
                    <td><c:out value="${inverter.p_nt}"/> Вт</td>
                </tr>
                <tr>
                    <td>Минимальное напряжение MPPT-контролера:</td>
                    <td><c:out value="${inverter.mppt_low}"/> В</td>
                </tr>
                <tr>
                    <td>Максимальное напряжение MPPT-контролера:</td>
                    <td><c:out value="${inverter.mppt_high}"/> В</td>
                </tr>
                <tr>
                    <td>Коэффициент C0:</td>
                    <td><c:out value="${inverter.c0}"/></td>
                </tr>
                <tr>
                    <td>Коэффициент C1:</td>
                    <td><c:out value="${inverter.c1}"/></td>
                </tr>
                <tr>
                    <td>Коэффициент C2:</td>
                    <td><c:out value="${inverter.c2}"/></td>
                </tr>
                <tr>
                    <td>Коэффициент C3:</td>
                    <td><c:out value="${inverter.c3}"/></td>
                </tr>
                <tr>
                    <td>Количество подключений:</td>
                    <td><c:out value="${inverter.connections}"/></td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>
<jsp:include page="${contextPath}/WEB-INF/jsp/common/scripts.jsp"/>
</body>
</html>
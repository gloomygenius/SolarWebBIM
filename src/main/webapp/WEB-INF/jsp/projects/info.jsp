<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
            <c:if test="${not empty requestScope.error_msg}">
                <div class="alert-danger"><c:out value="${requestScope.error_msg}"/></div>
            </c:if>
            <table class="table table-bordered">
                <jsp:useBean id="power_station" type="edu.spbpu.models.PowerStation" scope="request"/>
                <tr>
                    <td>ID станции:</td>
                    <td><c:out value="${power_station.id}"/></td>
                </tr>
                <tr>
                    <td>Название станции:</td>
                    <td><c:out value="${power_station.name}"/></td>
                </tr>
                <tr>
                    <td>Расположение станции:</td>
                    <td><p>Долгота: <c:out value="${power_station.position.longitude}"/>, Широта: <c:out
                            value="${power_station.position.latitude}"/> <br>Превышение над уровнем моря: <c:out
                            value="${power_station.position.altitude}"/> м</p></td>
                </tr>
                <tr>
                    <td>Номинальная мощность, Вт:</td>
                    <td><c:out value="${power_station.nominalPower}"/></td>
                </tr>
                <tr>
                    <td>Временная зона:</td>
                    <td><c:out value="${power_station.timeZone}"/></td>
                </tr>
                <tr>
                    <td>Инвертер:</td>
                    <td><c:if test="${not empty power_station.inverter}"><a
                            href="${contextPath}/inverters/info?id=${power_station.inverter.id}"><c:out
                            value="${power_station.inverter.name}"/></a></c:if><c:if
                            test="${empty power_station.inverter}">Не выбран</c:if></td>
                </tr>
                <tr>
                    <td>Модель фотоэлектрического модуля:</td>
                    <td><c:if test="${not empty power_station.solarModule}"><a
                            href="${contextPath}/modules/info?id=${power_station.solarModule.id}"><c:out
                            value="${power_station.solarModule.name}"/></a></c:if><c:if
                            test="${empty power_station.solarModule}">Не выбрана</c:if></td>
                </tr>
                <tr>
                    <td>Угол установки панели:</td>
                    <td><c:if test="${not empty power_station.betta}"><c:out
                            value="${power_station.betta}"/></c:if><c:if
                            test="${empty power_station.betta}">Угол не установлен</c:if></td>
                </tr>

                <tr>
                    <td>Электрическая цепь:</td>
                    <td><c:if test="${not empty power_station.string}"><p><a
                            href="${contextPath}/projects/cable/info?id=${power_station.id}">Количество паралельных
                        цепей:<c:out
                                value="${power_station.string.amountString}"/><br>Количество модулей в одной
                        последовательной цепи:<c:out
                                value="${power_station.string.battery.amountOfPaneles}"/></a></p></c:if><c:if
                            test="${empty power_station.string}">Параметры цепи не заданы</c:if></td>
                </tr>
                <tr>
                    <td>Количество инверторов:</td>
                    <td><c:if test="${not empty power_station.inverterAmount}"><c:out
                            value="${power_station.inverterAmount}"/></c:if><c:if
                            test="${empty power_station.inverterAmount}">неизвестно</c:if></td>
                </tr>
            </table>

            <button type="button" class="btn btn-link"><a href="${contextPath}/projects/edit?id=${power_station.id}">Изменить</a>
            </button>
            <br>
            <button type="button" class="btn btn-link"><a
                    href="${contextPath}/projects/local_data/info?id=${power_station.id}">Климатические данные</a>
            </button>
            <c:if test="${not empty power_station.solarModule}">
                <form action="${contextPath}/projects/calculate" method="post">
                    <input hidden name="parameter" value="opt_betta">
                    <input hidden name="id" value="${power_station.id}">
                    <input type="submit" value="Оптимизирвоать угол установки панели β">
                </form>
            </c:if>
            <c:if test="${not empty power_station.betta}">
                <form action="${contextPath}/projects/calculate" method="post">
                    <input hidden name="parameter" value="max_strings">
                    <input hidden name="id" value="${power_station.id}">
                    <input type="submit" value="Оптимизирвоать параметры электрической цепи">
                </form>
                <a href="${contextPath}/projects/module/operations?id=${power_station.id}">Моделирование работы панели в
                    течние года</a><br>
            </c:if>
            <c:if test="${not empty power_station.string}"><a
                    href="${contextPath}/projects/string/cable_edit?id=${power_station.id}">Задать
                длины кабелей</a><br></c:if>
            <c:if test="${not empty power_station.string.cables}"><a
                    href="${contextPath}/projects/annual_info?id=${power_station.id}">Годовое моделирование</a></c:if>
        </div>
    </div>
</div>
<jsp:include page="${contextPath}/WEB-INF/jsp/common/scripts.jsp"/>
</body>
</html>
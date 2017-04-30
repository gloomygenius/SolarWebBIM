<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
            <c:set var="operation" value="${requestScope.power_station_operation}" scope="page"/>
            <jsp:useBean id="operation" type="edu.spbpu.models.PowerStation.Operation" scope="page"/>
            <jsp:useBean id="power_station" type="edu.spbpu.models.PowerStation" scope="request"/>
            <table class="table table-bordered">
                <tbody>
                <tr>
                    <td>Годовая выработка:</td>
                    <td><fmt:formatNumber value="${operation.annualGeneration/1E6}" type="number"
                                          maxFractionDigits="3"/> МВт*ч
                    </td>
                </tr>
                <tr>
                    <td>КИУМ</td>
                    <td><fmt:formatNumber value="${operation.utilizationFactor}" type="percent"
                                          maxFractionDigits="2"/></td>
                </tr>
                <tr>
                    <td>Максимальная загрузка инвертора</td>
                    <c:if test="${operation.inverterLoad>1}"><c:set var="color" value="tomato"/></c:if>
                    <c:if test="${operation.inverterLoad<1}"><c:set var="color" value="greenyellow"/></c:if>
                    <td style="background-color: ${color}"><fmt:formatNumber value="${operation.inverterLoad}"
                                                                             type="percent" maxFractionDigits="2"/></td>
                </tr>
                <tr>
                    <td>Рабочее напряжение постоянного тока</td>
                    <td><fmt:formatNumber value="${operation.nominalVoltage}" maxFractionDigits="2"/> В</td>
                </tr>
                <tr>
                    <td><p>Максимальное напряжение холостого хода постоянного тока</p></td>
                    <td><fmt:formatNumber value="${operation.ocVoltage}" maxFractionDigits="2"/> В</td>
                </tr>
                <tr>
                    <td><p>Количество инверторов</p></td>
                    <c:set value="${power_station.inverterAmount}" var="inverterAmount"/>
                    <td id="inverter_amount"><c:out value="${inverterAmount}"/></td>
                </tr>
                <tr>
                    <td><p>Общее количество панелей</p></td>
                    <c:set var="stringAmount"
                           value="${power_station.inverterAmount*power_station.inverter.connections}"/>
                    <c:set var="moduleAmount"
                           value="${power_station.string.battery.amountOfPaneles*power_station.string.amountString}"/>
                    <td id="panel_amount"><c:out value="${stringAmount*moduleAmount}"/></td>
                </tr>
                </tbody>
            </table>
            Длины кабелей, используемых на электростанции:
            <table class="table table-bordered">
                <tr>
                    <td>Кабели от инвертора до сборки панелей</td>
                </tr>
                <c:forEach var="cross_section"
                           items="${power_station.cablesFromStringToInverter.stream().map(cable->cable.crossSection).distinct().toArray()}">
                    <tr>
                        <td>
                            <c:out value="${cross_section}"/>
                        </td>
                        <td>
                            <c:set var="is_string_cables"
                                   value="${inverterAmount*power_station.cablesFromStringToInverter.stream().filter(s->s.crossSection==cross_section).map(s->s.length).reduce(0, (x,y) -> x+y)}"/>
                            <c:out value="${is_string_cables}"/>
                        </td>
                    </tr>
                </c:forEach>
                <tr>
                    <td>Кабели сборок панелей</td>
                </tr>
                <c:forEach var="cross_section"
                           items="${power_station.string.cables.stream().map(cable->cable.crossSection).distinct().toArray()}">
                    <tr>
                        <td>
                            <c:out value="${cross_section}"/>
                        </td>
                        <td>
                            <c:set var="m_string_cables"
                                   value="${stringAmount*power_station.string.cables.stream().filter(s->s.crossSection==cross_section).map(s->s.length).reduce(0, (x,y) -> x+y)}"/>
                            <c:out value="${m_string_cables}"/>
                        </td>
                    </tr>
                </c:forEach>
            </table>
            <div>
                <p></p>
            </div>
            <form action="${contextPath}/projects/annual_info">
                <input type="hidden" value="${power_station.id}" name="id">
                <input type="radio" value="HORIZONTAL" name="orientation"> Горизонтальная ориентация панелей <br>
                <input type="radio" value="VERTICAL" name="orientation"> Вертикальная ориентация панелей <br>
                <%--<c:forEach var="cross_section"--%>
                <%--items="${power_station.cablesFromStringToInverter.stream().map(cable->cable.crossSection).distinct().toArray()}">--%>
                <%--<label>--%>
                <%--Цена 1 м кабеля сечением ${cross_section}:--%>
                <%--<input type="text" name="cable_price">--%>
                <%--</label>--%>
                <%--<br>--%>
                <%--</c:forEach>--%>
                <c:forEach var="cross_section" items="${requestScope.cross_sections}">
                    <label>
                        Цена 1 м кабеля сечением ${cross_section}:
                        <input type="text" value="0" name="cable_price">
                    </label>
                    <br>
                </c:forEach>
                <label>
                    Цена одного инвертора:
                    <input type="text" name="inverter_price">
                </label>
                <br>
                <label>
                    Цена установки одной солнечной панели:
                    <input type="text" name="module_price">
                </label>
                <br>
                <input type="submit" value="Рассчитать">
            </form>
            <c:if test="${not empty requestScope.capex_map}">
                <p> Итоговая стоимость:
                    кабели - <fmt:formatNumber value="${requestScope.capex_map.cable_cost}" minFractionDigits="2"/> руб.<br>
                    инвертора - <fmt:formatNumber value="${requestScope.capex_map.inverter_cost}"
                                                  minFractionDigits="2"/> руб.<br>
                    модули - <fmt:formatNumber value="${requestScope.capex_map.module_cost}" minFractionDigits="2"/>
                    руб.</p>
            </c:if>
        </div>
    </div>
</div>
<jsp:include page="${contextPath}/WEB-INF/jsp/common/scripts.jsp"/>
</body>
</html>
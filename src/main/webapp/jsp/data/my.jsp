<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<c:set var="dataSeriesList" value="${sessionScope.get('arrayOfDataSet')}" scope="page"/>
<h2>Data Sets</h2>
<div class="row">
    <div class="col-xs-12">
        <table class="table table-bordered">
            <thead>
            <tr>
                <th>№</th>
                <th>DataSet</th>
                <th>Параметр</th>
                <th>Широта</th>
                <th>Долгота</th>
                <th>Начало</th>
                <th>Конец</th>
                <th>Статус</th>
                <th>Ссылка</th>
            </tr>
            </thead>
            <tbody>

            <c:forEach items="${dataSeriesList}" var="dataSeries">

                <%--<jsp:useBean id="dataSeries" class="models.DataSeries" scope="page"/>--%>
                <tr>
                    <td></td>
                    <td>${dataSeries.dataSet.dataSetName}</td>
                    <td>${dataSeries.parameter}</td>
                    <td>${dataSeries.latitude}</td>
                    <td>${dataSeries.longitude}</td>
                    <td>${dataSeries.timeStart}</td>
                    <td>${dataSeries.timeEnd}</td>
                    <td><c:if test="${empty dataSeries.dataMap}">Идёт загрузка</c:if>
                        <c:if test="${not empty dataSeries.dataMap}">Данные загружены</c:if></td>
                    <td><input maxlength="25" size="40" value="${dataSeries.getLink()}"></td>
                </tr>
            </c:forEach>

            </tbody>
        </table>
    </div>
</div>
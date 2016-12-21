<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<jsp:useBean id="dataSetMap" class="java.util.HashMap" scope="application"/>
<h2>Data Sets</h2>
<div class="row">
    <div class="col-xs-12">
        <c:set var="dataSet" value="${dataSetMap.get(param.dataSetName)}" scope="page"/>
        <%--<jsp:useBean id="dataSet" class="models.DataSet" scope="page"/>--%>
        Сервер: ${dataSet.dataSource}
        Массив данных: ${dataSet.dataSetName}
        Время наблюдений: c ${dataSet.defaultTime}
        Географические ограничения <p>Широта: ${dataSet.minLatitude} .. ${dataSet.maxLatitude}<br>
        Долгота: ${dataSet.minLongitude} .. ${dataSet.maxLongitude}</p>
        <form action="/data/download" method="post">
        Параметры: <br>
        <c:forEach items="${dataSet.parameters}" var="parameter">
                   <input type="radio" name="parameter" value="${parameter}"> ${parameter} <br>
        </c:forEach>
            Широта: <input type="text" name="latitude" value=""><br>
            Долгота: <input type="text" name="longitude" value=""><br>
            Начало: <input type="datetime" name="timeStart"><br>
            Конец: <input type="datetime" name="timeEnd"><br>
            <input type="hidden" name="dataSetInfo" value="${param.dataSetName}">
        <button type="submit" class="btn btn-default">Загрузить</button>
        </form>
    </div>
</div>
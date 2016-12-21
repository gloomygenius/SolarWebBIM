<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<jsp:useBean id="dataSetMap" class="java.util.HashMap" scope="application"/>
<h2>Data Sets</h2>
<div class="row">
    <div class="col-xs-12">
        <form action="/data/get" method="get">
            <table class="table table-bordered">
                <thead>
                <tr>
                    <th>Выбрать</th>
                    <th>Название</th>
                    <th>Сервер</th>
                    <th>Массив данных</th>
                    <th>Время наблюдений</th>
                    <th>Географические ограничения</th>
                    <th>Параметры</th>
                </tr>
                </thead>
                <tbody>

                <c:forEach items="${dataSetMap}" var="entry">
                    <c:set var="dataSet" value="${entry.value}" scope="page"/>
                    <%--<jsp:useBean id="dataSet" class="models.DataSet" scope="page"/>--%>
                    <tr>
                        <td><input type="radio" name="dataSetName" value="${entry.key}"></td>
                        <td>${entry.key}</td>
                        <td>${dataSet.dataSource}</td>
                        <td>${dataSet.dataSetName}</td>
                        <td>Начиная с ${dataSet.defaultTime}</td>
                        <td><p>Широта: ${dataSet.minLatitude} .. ${dataSet.maxLatitude}<br>
                            Долгота: ${dataSet.minLongitude} .. ${dataSet.maxLongitude}</p></td>
                        <td>
                            <p><select size="1">
                                <option>Список параметров</option>
                                <c:forEach items="${dataSet.parameters}" var="parameter">
                                    <option>${parameter}</option>
                                </c:forEach>
                            </select></p>
                        </td>
                    </tr>
                </c:forEach>

                </tbody>
            </table>
            <button type="submit" class="btn btn-default">Загрузить</button>
        </form>
    </div>
</div>
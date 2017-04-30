<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="col-xs-2 col-md-2 col-lg-2">
    <ul class="nav nav-list">
        <li class="nav-header">Навигация</li>
        <li><a href="/data/get">Загрузка данных</a></li>
        <li><a href="/data/my">Мои данные</a></li>
        <li><a href="${pageContext.request.contextPath}/projects/list">Мои проекты</a></li>
        <li><a href="${pageContext.request.contextPath}/modules/list">Солнечные модули</a></li>
    </ul>
</div>
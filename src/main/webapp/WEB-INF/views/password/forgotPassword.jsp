<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:choose>
    <c:when test="${not empty success && success == true}">
        Reset password link was sent to your email
    </c:when>
    <c:when test="${not empty error}">
        ${error}
    </c:when>
    <c:otherwise>
        <form action="${pageContext.request.contextPath}/forgotPassword" method="post">
            <label for="email">Input email here</label><input type="text" name="email" id="email"/>
            <input type="submit"/>
        </form>
    </c:otherwise>
</c:choose>
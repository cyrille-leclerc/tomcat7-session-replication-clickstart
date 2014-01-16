<%@ page session="false" %>
<html>
<body>
<h2>Tomcat 7 Session Replication Demo</h2>
<%
    HttpSession httpSession = request.getSession(false);
    if(httpSession != null) {
        httpSession.invalidate();
        %>Session invalidated<br/><%
    }
%>
<ul>
    <li><a href="${pageContext.request.contextPath}/init-session">START SESSION TEST: Init Session</a></li>
    <li><a href="${pageContext.request.contextPath}/invalidate-session">Invalidate Session</a></li>
</ul>

<h2>Troubleshooting pages</h2>
<ul>
    <li><a href="parameters.jsp">Request and container parameters</a></li>
</ul>
</body>
</html>

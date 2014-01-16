/*
 * Copyright 2010-2013, CloudBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package localdomain.localhost;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:cleclerc@cloudbees.com">Cyrille Le Clerc</a>
 */
@WebServlet(value = "/init-session", loadOnStartup = 1)
public class SessionInitializerServlet extends HttpServlet {
    protected final Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        logger.info("Init");
        System.out.println("raw stdout call" + new Timestamp(System.currentTimeMillis()) + " - " + getClass().getName() + ".init");
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String clientSessionId = request.getParameter("client-session-id");

        HttpSession session = request.getSession(false);
        if (session != null) {
            sendError(response, clientSessionId, "session-already-exists");
            return;
        }

        session = request.getSession();

        Object creationTime = session.getAttribute("creation-time");
        if (creationTime != null) {
            sendError(response, clientSessionId, "creation-time-already-exists");
            return;
        }
        creationTime = new Date();
        session.setAttribute("creation-time", creationTime);

        String nextStepUrl = request.getContextPath() + "/test-session?client-session-id=" + clientSessionId;

        response.setHeader("x-next-step-url", nextStepUrl);
        PrintWriter writer = response.getWriter();

        writer.println("<html>");
        writer.println("<head><title>init-session</title></head>");
        writer.println("<body><h1>init-session</h1>");
        writer.println("SESSION IS CONSISTENT: <a href='" + nextStepUrl + "'>Next step</a><br/>");
        writer.println("</body></html>");

    }

    private void sendError(HttpServletResponse response, String clientSessionId, String msg) throws IOException {
        System.out.println("Error " + msg + " client-session-id=" + clientSessionId);
        response.setHeader("x-error", msg);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}

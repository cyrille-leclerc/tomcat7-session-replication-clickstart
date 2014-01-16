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
import java.util.logging.Logger;

/**
 * @author <a href="mailto:cleclerc@cloudbees.com">Cyrille Le Clerc</a>
 */
@WebServlet(value = "/test-session", loadOnStartup = 2)
public class SessionTesterServlet extends HttpServlet {
    protected final Logger logger = Logger.getLogger(getClass().getName());
    protected final int STEP_COUNT = 10;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        logger.info("Init");
        System.out.println("raw stdout call" + new Timestamp(System.currentTimeMillis()) + " - " + getClass().getName() + ".init");
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String clientSessionId = request.getParameter("client-session-id");

        HttpSession session = request.getSession(false);
        if (session == null) {
            sendError(response, clientSessionId, "missing-http-session");
            return;
        }
        Object creationTime = session.getAttribute("creation-time");
        if (creationTime == null) {
            sendError(response, clientSessionId, "missing-creation-time");
            return;
        }

        Integer counter = (Integer) session.getAttribute("counter");
        if (counter == null) {
            counter = 0;
        }

        counter = counter + 1;
        session.setAttribute("counter", counter);
        String nextStepUrl;
        String nextStepMessage;

        if (counter > STEP_COUNT) {
            nextStepUrl = request.getContextPath() + "/invalidate-session?client-session-id=" + clientSessionId;
            nextStepMessage = "LAST step";
        } else {
            nextStepUrl = request.getContextPath() + "/test-session?client-session-id=" + clientSessionId;
            nextStepMessage = "NEXT step";
        }

        response.setHeader("x-next-step-url", nextStepUrl);
        PrintWriter writer = response.getWriter();

        writer.println("<html>");
        writer.println("<head><title>test-session</title></head>");
        writer.println("<body><h1>test-session</h1>");
        writer.println("creationTime:" + creationTime + "<br/>");
        writer.println("counter:" + counter + "<br/>");
        writer.println("SESSION IS CONSISTENT: <a href='" + nextStepUrl + "'>" + nextStepMessage + "</a><br/>");
        writer.println("</body></html>");

    }

    private void sendError(HttpServletResponse response, String clientSessionId, String msg) throws IOException {
        System.out.println("Error " + msg + " client-session-id=" + clientSessionId);
        response.setHeader("x-error", msg);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}

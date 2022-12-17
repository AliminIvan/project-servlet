package com.tictactoe;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        config.getServletContext().setAttribute("Signs", Sign.values());
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        Field field = extractedField(session);

        int index = getSelectedIndex(request, response, session, field);
        if (index < 0) {
            return;
        }

        Sign sign = field.getField().get(index);

        if (sign != Sign.EMPTY) {
            RequestDispatcher requestDispatcher = this.getServletContext().getRequestDispatcher("/index.jsp");
            requestDispatcher.forward(request, response);
            return;
        }

        field.getField().put(index, Sign.CROSS);

        if (this.checkWin(response, session, field)) {
            return;
        }

        int emptyFieldIndex = field.getEmptyFieldIndex();
        if (emptyFieldIndex >= 0) {
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);
            if (this.checkWin(response, session, field)) {
                return;
            }
        } else {
            session.setAttribute("draw", true);

            List<Sign> data = field.getFieldData();

            session.setAttribute("data", data);

            response.sendRedirect("/index.jsp");
            return;
        }

        List<Sign> data = field.getFieldData();

        session.setAttribute("data", data);
        session.setAttribute("field", field);

        response.sendRedirect("/index.jsp");
    }

    private boolean checkWin(HttpServletResponse response, HttpSession session, Field field) throws IOException {
        Sign winner = field.checkWin();
        if (winner == Sign.CROSS || winner == Sign.NOUGHT) {

            session.setAttribute("winner", winner);

            List<Sign> data = field.getFieldData();

            session.setAttribute("data", data);

            response.sendRedirect("/index.jsp");

            return true;
        }
        return false;
    }

    private Field extractedField(HttpSession session) {
        Object field = session.getAttribute("field");
        if (Field.class != field.getClass()) {
            session.invalidate();
            throw new RuntimeException("Session is broken, try one more time");
        }
        return (Field) field;
    }

    private int getSelectedIndex(HttpServletRequest request, HttpServletResponse response,
                                 HttpSession session, Field field) throws IOException {
        String click = request.getParameter("click");
        boolean isNumeric = click.chars().allMatch(Character::isDigit);
        boolean isWin = checkWin(response, session, field);
        if (isWin) {
            return -1;
        }
        return isNumeric? Integer.parseInt(click) : 0;
    }
}

package com.tictactoe;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "InitServlet", value = "/start")
public class InitServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(true);

        Field field = new Field();

        List<Sign> data = field.getFieldData();


        session.setAttribute("field", field);
        session.setAttribute("data", data);

        RequestDispatcher requestDispatcher = this.getServletContext().getRequestDispatcher("/index.jsp");
        requestDispatcher.forward(request, response);
    }

}

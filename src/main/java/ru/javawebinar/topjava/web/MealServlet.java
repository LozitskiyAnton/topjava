package ru.javawebinar.topjava.web;

import ru.javawebinar.topjava.dao.CrudDao;
import ru.javawebinar.topjava.dao.InMemoryMealDao;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class MealServlet extends HttpServlet {
    public static final Integer CALORIES_PER_DAY = 2000;
    private CrudDao<Meal> dao;

    @Override
    public void init() throws ServletException {
        this.dao = new InMemoryMealDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String view = "";
        String action = request.getParameter("action");
        action = (action == null) ? "" : action ;

        switch (action) {
            case "delete": {
                dao.delete(parseId(request));
                response.sendRedirect("meals");
                return;
            }
            case "edit": {
                Meal meal = dao.find(parseId(request));
                request.setAttribute("meal", meal);
                view = "/addMeal.jsp";
                break;
            }
            case "insert": {
                view = "/addMeal.jsp";
                break;
            }
            default: {
                List<MealTo> mealTo = MealsUtil.filteredByStreams(dao.findAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY);
                request.setAttribute("mealTo", mealTo);
                view = "/meals.jsp";
            }
        }
        request.getRequestDispatcher(view).forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String mealId = request.getParameter("mealId");
        Meal meal = new Meal(LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")), parseId(request));
        if (mealId == null || mealId.isEmpty()) {
            dao.create(meal);
        } else {
            dao.update(meal);
        }
        response.sendRedirect("meals");
    }

    private static int parseId(HttpServletRequest request) {
        String mealId = request.getParameter("mealId");
        if (mealId.isEmpty())  {
            return 0;
        }
        else {
            return Integer.parseInt(mealId);
        }
    }
}

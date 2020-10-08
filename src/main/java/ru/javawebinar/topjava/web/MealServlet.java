package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
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

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    public static final int CALORIES_PER_DAY = 2000;
    private CrudDao<Meal> dao;

    @Override
    public void init() throws ServletException {
        this.dao = new InMemoryMealDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String view = "";
        String action = request.getParameter("action");
        action = (action == null) ? "" : action;

        switch (action) {
            case "delete": {
                dao.delete(parseId(request));
                log.debug("redirect to meals");
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
        log.debug("forward to {}", view);
        request.getRequestDispatcher(view).forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        int mealId = parseId(request);
        Meal meal = new Meal(mealId, LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));
        if (mealId == 0) {
            log.debug("create new meal");
            dao.create(meal);
        } else {
            log.debug("update meal with id: {}", mealId);
            dao.update(meal);
        }
        log.debug("redirect to meals");
        response.sendRedirect("meals");
    }

    private static int parseId(HttpServletRequest request) {
        String mealId = request.getParameter("mealId");
        return (mealId == null || mealId.isEmpty() ? 0 : Integer.parseInt(mealId));
    }
}

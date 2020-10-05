package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.CrudDao;
import ru.javawebinar.topjava.dao.MealDaoInMemory;
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
    private static final Integer CALORIES_PER_DAY = 2000;
    private CrudDao<Meal> dao;

    private static Integer parseMealId(String id) {
        return Integer.parseInt(id);
    }

    @Override
    public void init() throws ServletException {
        this.dao = new MealDaoInMemory();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String view = "";
        String action = request.getParameter("action");
        if (action == null) action = "";

        switch (action) {
            case "delete": {
                dao.delete(parseMealId(request.getParameter("mealId")));
                log.debug("redirect to meals");
                response.sendRedirect("meals");
                return;
            }
            case "edit": {
                Meal meal = dao.find(parseMealId(request.getParameter("mealId")));
                request.setAttribute("meal", meal);
                log.debug("redirect to addMeal");
                view = "/addMeal.jsp";
                break;
            }
            case "insert": {
                log.debug("redirect to addMeal");
                view = "/addMeal.jsp";
                break;
            }
            default: {
                List<MealTo> mealTo = MealsUtil.filteredByStreams(dao.findAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY);
                request.setAttribute("mealTo", mealTo);
                log.debug("redirect to meals");
                view = "/meals.jsp";
            }

        }
        request.getRequestDispatcher(view).forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String mealId = request.getParameter("mealId");
        if (mealId == null || mealId.isEmpty()) {
            Meal meal = new Meal(LocalDateTime.parse(request.getParameter("dateTime")),
                    request.getParameter("description"),
                    Integer.parseInt(request.getParameter("calories")), MealDaoInMemory.nextId());
            dao.create(meal);
        } else {
            Meal meal = new Meal(LocalDateTime.parse(request.getParameter("dateTime")),
                    request.getParameter("description"),
                    Integer.parseInt(request.getParameter("calories")), parseMealId(mealId));
            dao.update(meal);
        }

        List<MealTo> mealTo = MealsUtil.filteredByStreams(dao.findAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY);
        request.setAttribute("mealTo", mealTo);
        request.getRequestDispatcher("/meals.jsp").forward(request, response);
    }
}

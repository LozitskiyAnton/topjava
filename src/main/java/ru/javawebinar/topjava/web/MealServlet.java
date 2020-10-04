package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.MealDaoInMemoryImpl;
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
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private final MealDaoInMemoryImpl dao;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public MealServlet() {
        this.dao = new MealDaoInMemoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String view;
        String action = request.getParameter("action");

        if (action == null) {
            List<MealTo> mealTo = MealsUtil.filteredByStreams(dao.findAll(), LocalTime.MIN, LocalTime.MAX, 2000);
            request.setAttribute("mealTo", mealTo);
            view = "/meals.jsp";

        } else if (action.equalsIgnoreCase("delete")) {
            int mealId = Integer.parseInt(request.getParameter("mealId"));
            dao.delete(mealId);
            List<MealTo> mealTo = MealsUtil.filteredByStreams(dao.findAll(), LocalTime.MIN, LocalTime.MAX, 2000);
            request.setAttribute("mealTo", mealTo);
            view = "/meals.jsp";

        } else if (action.equalsIgnoreCase("edit")) {
            view = "/addMeal.jsp";
            int mealId = Integer.parseInt(request.getParameter("mealId"));
            Meal meal = dao.find(mealId);
            request.setAttribute("meal", meal);

        } else {
            view = "/addMeal.jsp";
        }

        request.getRequestDispatcher(view).forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Meal meal = new Meal(LocalDateTime.parse(request.getParameter("dateTime"), formatter),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));

        String mealId = request.getParameter("mealId");
        if (mealId != null && !mealId.isEmpty()) {
            dao.delete(Integer.parseInt(mealId));
        }
        dao.save(meal);

        List<MealTo> mealTo = MealsUtil.filteredByStreams(dao.findAll(), LocalTime.MIN, LocalTime.MAX, 2000);
        mealTo.sort(Comparator.comparing(MealTo::getDateTime));
        request.setAttribute("mealTo", mealTo);
        request.getRequestDispatcher("/meals.jsp").forward(request, response);
    }
}

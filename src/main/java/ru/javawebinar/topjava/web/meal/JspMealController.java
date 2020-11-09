package ru.javawebinar.topjava.web.meal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;
import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

@Controller
@RequestMapping(value = "/meals")
public class JspMealController {

    public final MealService service;

    public JspMealController(MealService service) {
        this.service = service;
    }

    @GetMapping
    public String getMeals(Model model) {
        model.addAttribute("meals", MealsUtil.getTos(service.getAll(SecurityUtil.authUserId()), SecurityUtil.authUserCaloriesPerDay()));
        return "meals";
    }

    @GetMapping("/filter")
    public String getBetween(HttpServletRequest request) {
        List<Meal> mealsDateFiltered = service.getBetweenInclusive(
                parseLocalDate(request.getParameter("startDate")),
                parseLocalDate(request.getParameter("endDate")),
                SecurityUtil.authUserId());
        request.setAttribute("meals", MealsUtil.getFilteredTos(mealsDateFiltered, SecurityUtil.authUserCaloriesPerDay(),
                parseLocalTime(request.getParameter("startTime")),
                parseLocalTime(request.getParameter("endTime"))));
        return "meals";
    }

    @GetMapping("/delete")
    public String delete(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        service.delete(id, SecurityUtil.authUserId());
        return "redirect:/meals";
    }

    @GetMapping("/update")
    public String update(HttpServletRequest request) {
        request.setAttribute("meal", StringUtils.hasText(request.getParameter("id")) ?
                service.get(getId(request), SecurityUtil.authUserId()) :
                new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000));

        return "mealForm";
    }

    @PostMapping("/update")
    public String updateMeal(HttpServletRequest request) {
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));
        if (!StringUtils.hasText(request.getParameter("id"))) {
            int userId = SecurityUtil.authUserId();
            checkNew(meal);
            service.create(meal, userId);
        } else {
            assureIdConsistent(meal, getId(request));
            service.update(meal, SecurityUtil.authUserId());
        }
        return "redirect:/meals";
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }

}

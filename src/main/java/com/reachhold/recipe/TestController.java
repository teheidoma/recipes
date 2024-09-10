package com.reachhold.recipe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@RestController
public class TestController {
    List<Recipe> recipes = new ArrayList<>();
    List<User> users = new ArrayList<>();


    TestController() throws IOException {
        load();
        loadUsers();
    }

    void save() throws IOException {
        FileOutputStream fos = new FileOutputStream("recipes.txt", false);
        JsonMapper mapper = new JsonMapper();
        mapper.writeValue(fos, recipes);
    }

    void save(User user) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(new FileOutputStream("users.txt", true));
        out.println(user.username + ";;" + user.password);
        out.close();
    }

    void load() throws IOException {
        recipes = new ArrayList<>(List.of(new JsonMapper().readValue(new File("recipes.txt"), Recipe[].class)));
    }

    void loadUsers() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("users.txt"));

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] split = line.split(";;");

            User user = new User();
            user.username = split[0];
            user.password = split[1];

            users.add(user);
        }
    }

    @RequestMapping
    public ModelAndView home(Authentication authentication) throws FileNotFoundException {
        ModelAndView mvc = new ModelAndView("index.html");
        mvc.addObject("recipes", recipes);

        if (authentication != null) {
            mvc.addObject("user", authentication.getName());
        } else {
            mvc.addObject("user", "guest");
        }
        return mvc;
    }

    @RequestMapping("/recipe/add")
    ModelAndView addRecipe(String name,
                           String description,
                           String image,
                           float rating,
                           @RequestParam("test-editor-html-code") String instruction) throws IOException {
        Recipe recipe = new Recipe();
        recipe.name = name;
        recipe.description = description;
        recipe.image = image;
        recipe.rating = rating;
        recipe.instruction = instruction;
        recipes.add(recipe);
        save();
        return new ModelAndView("redirect:/");
    }

    @RequestMapping("/recipe/show")
    List<Recipe> showRecipe() throws FileNotFoundException {
        return recipes;
    }

    @RequestMapping("/recipe/create")
    ModelAndView createRecipe() throws FileNotFoundException {
        return new ModelAndView("create");
    }

    @RequestMapping("/recipe")
    ModelAndView recipe(Integer number) {
        Recipe recipe = recipes.get(number - 1);
        ModelAndView mvc = new ModelAndView("recipe");
        mvc.addObject("recipe", recipe);
        return mvc;
    }

    @RequestMapping("/recipe/show_one")
    Recipe showOneRecipe(int number) {
        return recipes.get(number - 1);
    }

    @RequestMapping("/registration")
    ModelAndView registration() throws FileNotFoundException {
        return new ModelAndView("registration");
    }

    @RequestMapping("/registration2")
    ModelAndView registration2(String login, String pass1, String pass2) throws FileNotFoundException {
        if (pass1.equals(pass2)) {
            boolean userExists = false;
            for (User user : users) {
                if (user.username.equals(login)) {
                    userExists = true;
                    break;
                }
            }
            if (userExists) {
                throw new IllegalArgumentException("User already exists");
            }


            User user = new User();
            user.username = login;
            user.password = pass1;
            save(user);
            users.add(user);
            return new ModelAndView("redirect:/");
        } else {
            throw new IllegalArgumentException("Wrong password");
        }
    }

    @GetMapping("/login")
    ModelAndView login() {
        return new ModelAndView("login");
    }
}

package com.reachhold.recipe;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@RestController
public class TestController {
    List<Recipe> recipes = new ArrayList<>();
    List<User> users = new ArrayList<>();


    TestController() throws FileNotFoundException {
        load();
        loadUsers();
    }

    void save(Recipe recipe) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(new FileOutputStream("recipes.txt", true));
        out.println(recipe.name + ";;" + recipe.description + ";;" + recipe.image + ";;" + recipe.rating);
        out.close();
    }

    void save(User user) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(new FileOutputStream("users.txt", true));
        out.println(user.username + ";;" + user.password);
        out.close();
    }

    void load() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("recipes.txt"));

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] split = line.split(";;");

            Recipe recipe = new Recipe();
            recipe.name = split[0];
            recipe.description = split[1];
            recipe.image = split[2];
            recipe.rating = Float.parseFloat(split[3]);

            recipes.add(recipe);
        }
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
                           float rating) throws FileNotFoundException {
        Recipe recipe = new Recipe();
        recipe.name = name;
        recipe.description = description;
        recipe.image = image;
        recipe.rating = rating;
        recipes.add(recipe);
        save(recipe);
        return new ModelAndView("redirect:/");
    }

    @RequestMapping("/recipe/show")
    List<Recipe> showRecipe() throws FileNotFoundException {
        return recipes;
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

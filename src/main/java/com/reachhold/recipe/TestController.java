package com.reachhold.recipe;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

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
            user.avatar = split[2];

            users.add(user);
        }
    }

    @RequestMapping
    public ModelAndView home() throws FileNotFoundException {
        ModelAndView mvc = new ModelAndView("index.html");
        mvc.addObject("recipes", recipes);
        return mvc;
    }

    @RequestMapping("/recipe/add")
    ModelAndView addRecipe(String name,
                           String description,
                           String image,
                           float rating,
                           String username,
                           String password) throws FileNotFoundException {
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (username.equals(user.username)) {
                if (password.equals(user.password)) {
                    break;
                } else {
                    return null;
                }
            }
        }
        return null;
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

    //localhost/login?username=test1&password=test
    @RequestMapping("/login")
    boolean login(String username,
                  String password) {
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (username.equals(user.username)) {
                if (password.equals(user.password)) {
                    return true;
                }
            }
        }
        return false;
    }
}

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
        try(FileOutputStream fos = new FileOutputStream("recipes.txt", false)) {
            JsonMapper mapper = new JsonMapper();
            mapper.writeValue(fos, recipes);
        }
    }

    void saveUsers() throws IOException {
        try(FileOutputStream fos = new FileOutputStream("users.txt", false)) {
            JsonMapper mapper = new JsonMapper();
            mapper.writeValue(fos, users);
        }
    }

    void load() throws IOException {
        File source = new File("recipes.txt");
        if (!source.exists()) {
            source.createNewFile();
            FileOutputStream fos = new FileOutputStream(source);
            fos.write("[]".getBytes());
            fos.close();
        }
        recipes = new ArrayList<>(List.of(new JsonMapper().readValue(source, Recipe[].class)));
    }

    void loadUsers() throws IOException {
        File source = new File("users.txt");
        if (!source.exists()) {
            source.createNewFile();
            FileOutputStream fos = new FileOutputStream(source);
            fos.write("[]".getBytes());
            fos.close();
        }
        users = new ArrayList<>(List.of(new JsonMapper().readValue(source, User[].class)));
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
                           String ingridients,
                           float rating,
                           @RequestParam("test-editor-html-code") String instruction,
                           Authentication authentication) throws IOException {
        Recipe recipe = new Recipe();
        recipe.name = name;
        recipe.ingridients = ingridients.split(",");
        recipe.description = description;
        recipe.image = image;
        recipe.rating = rating;
        recipe.author = authentication.getName();
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
    ModelAndView registration2(String login, String pass1, String pass2) throws IOException {
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
            users.add(user);
            saveUsers();
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

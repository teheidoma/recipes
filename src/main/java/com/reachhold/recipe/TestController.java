package com.reachhold.recipe;

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

    @RequestMapping
    public ModelAndView home() throws FileNotFoundException {
        ModelAndView mvc = new ModelAndView("index.html");
        if (alreadyLoaded == false) {
            load();
            alreadyLoaded = true;
        }
        mvc.addObject("recipes", recipes);
        return mvc;
    }

    @RequestMapping("/recipe/add")
    String addRecipe(String name, String description, String image, float rating) throws FileNotFoundException {
        Recipe recipe = new Recipe();
        recipe.name = name;
        recipe.description = description;
        recipe.image = image;
        recipe.rating = rating;
        recipes.add(recipe);
        save(recipe);
        return "OK";
    }

    boolean alreadyLoaded = false;

    @RequestMapping("/recipe/show")
    List<Recipe> showRecipe() throws FileNotFoundException {
        if (alreadyLoaded == false) {
            load();
            alreadyLoaded = true;
        }
        return recipes;
    }

    @RequestMapping("/recipe/show_one")
    Recipe showOneRecipe(int number) {
        return recipes.get(number - 1);
    }
}

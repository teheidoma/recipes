package com.reachhold.recipe;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

@RestController
public class TestController {
    Recipe[] recipes = new Recipe[1000];

    int counter = 0;

    void save(Recipe recipe) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(new FileOutputStream("recipes.txt", true));
        out.println(recipe.name + "," + recipe.description + "," + recipe.image + "," + recipe.rating);
        out.close();
    }

    void load() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("recipes.txt"));

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            //хліб,борошно,123 => ['хліб', 'борошно', '123']
            String[] split = line.split(",");

            Recipe recipe = new Recipe();
            recipe.name = split[0];
            recipe.description = split[1];
            recipe.image = split[2];
            recipe.rating = Float.parseFloat(split[3]);

            recipes[counter] = recipe;
            counter++;
        }
    }

    @RequestMapping("/recipe/add")
    String addRecipe(String name, String description, String image, float rating) throws FileNotFoundException {
        Recipe recipe = new Recipe();
        recipe.name = name;
        recipe.description = description;
        recipe.image = image;
        recipe.rating = rating;
        recipes[counter] = recipe;
        counter++;
        save(recipe);
        return "OK";
    }

    boolean alreadyLoaded = false;

    @RequestMapping("/recipe/show")
    Recipe[] showRecipe() throws FileNotFoundException {
        if (alreadyLoaded == false) {
            load();
            alreadyLoaded = true;
        }
        return recipes;
    }

    @RequestMapping("/recipe/show_one")
    Recipe showOneRecipe(int number) {
        return recipes[number - 1];
    }
}

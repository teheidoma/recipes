package com.reachhold.recipe;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    Recipe[] recipes = new Recipe[1000];
    //public class Recipe {
    //    String name;
    //    String description;
    //    String image;
    //}

    int counter = 0;

    @RequestMapping("/recipe/add")
    void addRecipe(String name, String description, String image) {
        Recipe recipe = new Recipe();
        recipe.name = name;
        recipe.description = description;
        recipe.image = image;
        recipes[counter] = recipe;
        counter++;
    }

    @RequestMapping("/recipe/show")
    Recipe[] test(String name, String age) {
        return recipes;
    }

    @RequestMapping("/recipe/show_one")
    Recipe test(int number) {
        return recipes[number - 1];
    }
}

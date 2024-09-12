package com.reachhold.recipe;

import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
public class UserPerehodnik implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            ArrayList<UserDetails> users = loadUsers();
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getUsername().equals(username)) {
                    return users.get(i);
                }
            }
            throw new UsernameNotFoundException(username);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    ArrayList<UserDetails> loadUsers() throws IOException {
        com.reachhold.recipe.User[] users = new JsonMapper().readValue(new File("users.txt"), com.reachhold.recipe.User[].class);
        ArrayList<UserDetails> list = new ArrayList<>();
        for (com.reachhold.recipe.User u : users) {

            UserDetails user = new User(u.username, u.password, List.of());

            list.add(user);
        }
        return list;
    }
}

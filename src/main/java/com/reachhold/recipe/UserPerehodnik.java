package com.reachhold.recipe;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
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
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    ArrayList<UserDetails> loadUsers() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("users.txt"));
        ArrayList<UserDetails> list = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] split = line.split(";;");

//            User user = new User();
//            user.username = split[0];
//            user.password = split[1];
            UserDetails user = new User(split[0], split[1], List.of());

            list.add(user);
        }
        return list;
    }
}

package com.github.danpfe.lookielookie.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;

public class ExampleUserLoader {
  public static ExampleUserLoader INSTANCE = new ExampleUserLoader();

  private final List<ExampleUser> users;

  private ExampleUserLoader() {
    final var mapper = new ObjectMapper();
    try {
      users = mapper.readValue(getClass().getResource("/users.json"), ExampleUsers.class).getUsers();
    } catch (IOException e) {
      throw new RuntimeException("unable to fetch users JSON", e);
    }
  }

  public ExampleUser getUserByAnyUserName(String userName) {
    return users
        .stream()
        .filter(user -> {
          boolean match = user.getUserNames().stream().anyMatch(name -> name.equalsIgnoreCase(userName));
          if (!match) {
            match = user.getId().equalsIgnoreCase(userName); // check the regular ID if it's the second round when Keycloak uses the username we told.
          }
          return match;
        })
        .findFirst()
        .orElse(null);
  }

  public ExampleUser getUserByAnyEmailAddress(String emailAddress) {
    return users
        .stream()
        .filter(user -> user.getEmailAddresses().stream().anyMatch(address -> address.equalsIgnoreCase(emailAddress)))
        .findFirst()
        .orElse(null);
  }

  public ExampleUser getUserById(String id) {
    return users.stream().filter(user -> user.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
  }
}

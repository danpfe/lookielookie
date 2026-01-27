package com.github.danpfe.lookielookie;

import org.keycloak.testframework.server.KeycloakServerConfig;
import org.keycloak.testframework.server.KeycloakServerConfigBuilder;

public class CustomServerConfig implements KeycloakServerConfig {
  @Override
  public KeycloakServerConfigBuilder configure(KeycloakServerConfigBuilder keycloakServerConfigBuilder) {
    return keycloakServerConfigBuilder.dependency("com.github.danpfe", "lookielookie");
  }
}

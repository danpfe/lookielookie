package com.github.danpfe.lookielookie;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class ExampleUserStorageProviderFactory implements UserStorageProviderFactory<ExampleUserStorageProvider> {
  public static final String ID = "example-provider";

  @Override
  public ExampleUserStorageProvider create(final KeycloakSession session, final ComponentModel model) {
    return new ExampleUserStorageProvider(session, model);
  }

  @Override
  public String getId() {
    return ID;
  }
}

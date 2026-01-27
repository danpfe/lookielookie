package com.github.danpfe.lookielookie;

import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.testframework.annotations.InjectAdminClient;
import org.keycloak.testframework.annotations.InjectRealm;
import org.keycloak.testframework.annotations.KeycloakIntegrationTest;
import org.keycloak.testframework.oauth.OAuthClient;
import org.keycloak.testframework.oauth.annotations.InjectOAuthClient;
import org.keycloak.testframework.realm.ManagedRealm;

import static org.junit.jupiter.api.Assertions.assertEquals;

@KeycloakIntegrationTest(config = CustomServerConfig.class)
public class ExampleUserStorageProviderTest {
  @InjectRealm
  private ManagedRealm realm;
  @InjectAdminClient
  private Keycloak adminClient;
  @InjectOAuthClient
  private OAuthClient oAuthClient;

  @Test
  public void testDifferentUsernamesDoNotResultInDuplicates() {
    final var component = new ComponentRepresentation();
    component.setName("lookielookie");
    component.setProviderId(ExampleUserStorageProviderFactory.ID);
    component.setProviderType(UserStorageProvider.class.getName());
    component.setParentId(realm.getId());
    try (final var addResponse = adminClient.realm(realm.getName()).components().add(component)) {
      assertEquals(201, addResponse.getStatus());
    }

    oAuthClient.doLogin("1029384", "test"); // we don't care about the accessToken
    oAuthClient.doLogin("5748392", "test"); // we don't care about the accessToken

    assertEquals(1, adminClient.realm(realm.getName()).users().count());

    oAuthClient.doLogin("9988776", "test"); // we don't care about the accessToken

    assertEquals(2, adminClient.realm(realm.getName()).users().count());
  }

}

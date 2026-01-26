package com.github.danpfe.lookielookie;

import com.github.danpfe.lookielookie.loader.ExampleUserLoader;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserProvider;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.models.utils.UserModelDelegate;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.ImportedUserValidation;
import org.keycloak.storage.user.UserLookupProvider;

public class ExampleUserStorageProvider implements UserStorageProvider /* this is just a marker interface */,
    UserLookupProvider /* adds methods that allow to look up a user and make them login-able */,
    ImportedUserValidation /* called to check if a previously imported user has been updated (or deleted) in the remote storage */,
    CredentialInputValidator /* adds methods to verify user credentials */ {

  public static final String USER_MODEL_EXTERNAL_USER_NAMES = "externalUserNames";
  public static final String USER_MODEL_EXTRA_EMAIL_ADDRESSES = "allEmailAddresses";

  private final KeycloakSession session;
  private final ComponentModel model;
  private final ExampleUserLoader loader;

  public ExampleUserStorageProvider(final KeycloakSession session, final ComponentModel model) {
    this.session = session;
    this.model = model;
    this.loader = ExampleUserLoader.INSTANCE;
  }

  @Override
  public void close() {
    // intentionally left empty
  }

  @Override
  public UserModel getUserById(final RealmModel realm, final String id) {
    final var storageId = new StorageId(id);
    final var externalId = storageId.getExternalId();
    return getUserByUsername(realm, externalId);
  }

  @Override
  public UserModel getUserByUsername(final RealmModel realm, final String username) {
    return createAdapter(realm, username);
  }

  @Override
  public UserModel getUserByEmail(final RealmModel realm, final String email) {
    return null; // skip this for now
  }

  @Override
  public UserModel validate(final RealmModel realm, final UserModel user) {
    // TODO Instead of simply returning the same thing we have to make a diff!
    return getUserByUsername(realm, user.getUsername());
  }

  private UserModel createAdapter(final RealmModel realm, final String username) {
    final var remoteUser = loader.getUserByAnyUserName(username);
    if (remoteUser == null) {
      return null;
    }
    final var userProvider = session.getProvider(UserProvider.class);
    var localUser = userProvider.getUserByUsername(realm, remoteUser.getId().toLowerCase());
    if (localUser == null) {
      localUser = userProvider.addUser(realm, remoteUser.getId().toLowerCase());
      localUser.setFirstName(remoteUser.getFirstName());
      localUser.setLastName(remoteUser.getLastName());
      localUser.setEmail(remoteUser.getEmailAddresses().getFirst());
      localUser.setEmailVerified(true);
      localUser.setEnabled(true);
      localUser.setAttribute(USER_MODEL_EXTERNAL_USER_NAMES, remoteUser.getUserNames());
      localUser.setAttribute(USER_MODEL_EXTRA_EMAIL_ADDRESSES, remoteUser.getEmailAddresses());
      localUser.setFederationLink(model.getId());
    }
    return new UserModelDelegate(localUser);
  }

  @Override
  public boolean supportsCredentialType(String credentialType) {
    return PasswordCredentialModel.TYPE.equals(credentialType);
  }

  @Override
  public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
    return true;
  }

  @Override
  public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
    return true; // we take any password, it's fiiiiiine.
  }
}

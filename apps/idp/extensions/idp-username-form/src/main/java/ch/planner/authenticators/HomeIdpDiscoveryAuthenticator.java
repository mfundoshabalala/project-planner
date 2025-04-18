package ch.planner.authenticators;

import static org.keycloak.services.validation.Validation.FIELD_USERNAME;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.authenticators.browser.AbstractUsernameFormAuthenticator;
import org.keycloak.events.Details;
import org.keycloak.events.Errors;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.AuthenticationManager;

/**
 * This is a fork from https://github.com/sventorben/keycloak-home-idp-discovery
 *
 * The class has been slighly modified to make the class non-final and some methods protected.
 */
class HomeIdpDiscoveryAuthenticator extends AbstractUsernameFormAuthenticator {

  private static final Logger LOG = Logger.getLogger(HomeIdpDiscoveryAuthenticator.class);

  HomeIdpDiscoveryAuthenticator() {}

  @Override
  public void authenticate(AuthenticationFlowContext authenticationFlowContext) {
    HomeIdpAuthenticationFlowContext context = new HomeIdpAuthenticationFlowContext(authenticationFlowContext);

    if (context.loginPage().shouldByPass()) {
      String loginHint = trimToNull(context.loginHint().getFromSession());
      if (loginHint == null) {
        loginHint = trimToNull(authenticationFlowContext.getAuthenticationSession().getAuthNote(ATTEMPTED_USERNAME));
      }
      if (loginHint != null) {
        String username = setUserInContext(authenticationFlowContext, loginHint);
        final List<IdentityProviderModel> homeIdps = context.discoverer().discoverForUser(username);
        if (!homeIdps.isEmpty()) {
          context.rememberMe().remember(username);
          redirectOrChallenge(context, username, homeIdps);
          return;
        }
      }
    }
    context.authenticationChallenge().forceChallenge();
  }

  protected void redirectOrChallenge(
    HomeIdpAuthenticationFlowContext context,
    String username,
    List<IdentityProviderModel> homeIdps
  ) {
    if (homeIdps.size() == 1 || context.config().forwardToFirstMatch()) {
      IdentityProviderModel homeIdp = homeIdps.get(0);
      context.loginHint().setInAuthSession(homeIdp, username);
      context.redirector().redirectTo(homeIdp);
    } else {
      context.authenticationChallenge().forceChallenge(homeIdps);
    }
  }

  @Override
  public void action(AuthenticationFlowContext authenticationFlowContext) {
    MultivaluedMap<String, String> formData = authenticationFlowContext.getHttpRequest().getDecodedFormParameters();
    if (formData.containsKey("cancel")) {
      LOG.debugf("Login canceled");
      authenticationFlowContext.cancelLogin();
      return;
    }

    String username = setUserInContext(
      authenticationFlowContext,
      formData.getFirst(AuthenticationManager.FORM_USERNAME)
    );
    if (username == null) {
      LOG.debugf("No username in request");
      return;
    }

    HomeIdpAuthenticationFlowContext context = new HomeIdpAuthenticationFlowContext(authenticationFlowContext);

    final List<IdentityProviderModel> homeIdps = context.discoverer().discoverForUser(username);
    if (homeIdps.isEmpty()) {
      authenticationFlowContext.attempted();
    } else {
      RememberMe rememberMe = context.rememberMe();
      rememberMe.handleAction(formData);
      rememberMe.remember(username);
      redirectOrChallenge(context, username, homeIdps);
    }
  }

  protected String setUserInContext(AuthenticationFlowContext context, String username) {
    username = trimToNull(username);

    if (username == null) {
      LOG.warn("No or empty username found in request");
      context.getEvent().error(Errors.USER_NOT_FOUND);
      Response challengeResponse = challenge(context, getDefaultChallengeMessage(context), FIELD_USERNAME);
      context.failureChallenge(AuthenticationFlowError.INVALID_USER, challengeResponse);
      return null;
    }

    LOG.debugf("Found username '%s' in request", username);
    context.getEvent().detail(Details.USERNAME, username);
    context.getAuthenticationSession().setAuthNote(ATTEMPTED_USERNAME, username);

    return username;
  }

  private String trimToNull(String username) {
    if (username != null) {
      username = username.trim();
      if ("".equalsIgnoreCase(username)) username = null;
    }
    return username;
  }

  @Override
  protected Response createLoginForm(LoginFormsProvider form) {
    return form.createLoginUsername();
  }

  @Override
  protected String getDefaultChallengeMessage(AuthenticationFlowContext context) {
    return context.getRealm().isLoginWithEmailAllowed() ? "invalidUsernameOrEmailMessage" : "invalidUsernameMessage";
  }

  @Override
  public boolean requiresUser() {
    return false;
  }

  @Override
  public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
    return true;
  }

  @Override
  public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {}
}

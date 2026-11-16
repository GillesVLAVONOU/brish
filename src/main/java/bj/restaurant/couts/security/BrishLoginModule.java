package bj.restaurant.couts.security;

import bj.restaurant.couts.model.Role;
import bj.restaurant.couts.util.PasswordUtil;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class BrishLoginModule implements LoginModule {
    private Subject subject;
    private CallbackHandler callbackHandler;
    private boolean authenticated;
    private boolean committed;
    private BrishPrincipal userPrincipal;
    private BrishRolePrincipal rolePrincipal;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler,
            Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
    }

    @Override
    public boolean login() throws LoginException {
        NameCallback nameCallback = new NameCallback("Adresse e-mail");
        PasswordCallback passwordCallback = new PasswordCallback("Mot de passe", false);
        try {
            callbackHandler.handle(new Callback[] {nameCallback, passwordCallback});
        } catch (IOException | UnsupportedCallbackException exception) {
            throw loginException("Impossible de lire les identifiants.", exception);
        }

        String email = nameCallback.getName() == null ? "" : nameCallback.getName().trim().toLowerCase();
        char[] password = passwordCallback.getPassword();
        passwordCallback.clearPassword();
        if (email.isBlank() || password == null || password.length == 0) {
            if (password != null) Arrays.fill(password, '\0');
            throw new FailedLoginException("Identifiants invalides.");
        }

        try {
            authenticate(email, password);
            authenticated = true;
            return true;
        } catch (FailedLoginException exception) {
            throw exception;
        } catch (NamingException | SQLException | GeneralSecurityException exception) {
            throw loginException("Le service d'authentification est indisponible.", exception);
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    private void authenticate(String email, char[] password)
            throws NamingException, SQLException, GeneralSecurityException, FailedLoginException {
        DataSource dataSource = (DataSource) new InitialContext().lookup("java:/jdbc/BrishDS");
        String sql = "SELECT id_utilisateur, nom, email, role, mot_de_passe_hash "
                + "FROM utilisateurs WHERE LOWER(email) = ? AND actif = TRUE";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next() || !PasswordUtil.verifier(password, result.getString("mot_de_passe_hash"))) {
                    throw new FailedLoginException("Identifiants invalides.");
                }
                String role = Role.valueOf(result.getString("role")).name();
                userPrincipal = new BrishPrincipal(
                        result.getLong("id_utilisateur"), result.getString("nom"), result.getString("email"), role);
                rolePrincipal = new BrishRolePrincipal(role);
            }
        }
    }

    @Override
    public boolean commit() throws LoginException {
        if (!authenticated) return false;
        if (subject.isReadOnly()) throw new LoginException("Le sujet JAAS est en lecture seule.");
        subject.getPrincipals().add(userPrincipal);
        subject.getPrincipals().add(rolePrincipal);
        committed = true;
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        if (!authenticated) return false;
        if (committed) return logout();
        clearState();
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        if (subject.isReadOnly()) throw new LoginException("Le sujet JAAS est en lecture seule.");
        subject.getPrincipals().remove(userPrincipal);
        subject.getPrincipals().remove(rolePrincipal);
        clearState();
        return true;
    }

    private void clearState() {
        authenticated = false;
        committed = false;
        userPrincipal = null;
        rolePrincipal = null;
    }

    private LoginException loginException(String message, Exception cause) {
        LoginException exception = new LoginException(message);
        exception.initCause(cause);
        return exception;
    }
}

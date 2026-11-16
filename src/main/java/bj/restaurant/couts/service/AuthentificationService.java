package bj.restaurant.couts.service;

import bj.restaurant.couts.security.BrishPrincipal;
import bj.restaurant.couts.security.BrishJaasConfiguration;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.Optional;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

@ApplicationScoped
public class AuthentificationService {
	public Optional<BrishPrincipal> authentifier(String email, char[] motDePasse) throws LoginException {
		Subject subject = new Subject();
		CallbackHandler callbackHandler = callbacks -> renseignerCallbacks(callbacks, email, motDePasse);
		LoginContext loginContext = new LoginContext("Brish", subject, callbackHandler, new BrishJaasConfiguration());

		loginContext.login();
		try {
			return subject.getPrincipals(BrishPrincipal.class).stream().findFirst();
		} finally {
			loginContext.logout();
		}
	}

	private void renseignerCallbacks(Callback[] callbacks, String email, char[] motDePasse)
			throws IOException, UnsupportedCallbackException {
		for (Callback callback : callbacks) {
			if (callback instanceof NameCallback nameCallback) {
				nameCallback.setName(email);
			} else if (callback instanceof PasswordCallback passwordCallback) {
				passwordCallback.setPassword(motDePasse);
			} else {
				throw new UnsupportedCallbackException(callback);
			}
		}
	}
}
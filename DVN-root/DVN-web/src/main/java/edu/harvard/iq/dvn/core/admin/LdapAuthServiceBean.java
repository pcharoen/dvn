/**
 * University of Alberta Libraries
 * Information Technology Services
 */
package edu.harvard.iq.dvn.core.admin;

import java.util.Hashtable;

import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;

/**
 * The LdapAuthServiceBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 1181 $ $Date: 2012-07-11 15:46:27 -0600 (Wed, 11 Jul 2012) $
 */
@Stateless
public class LdapAuthServiceBean implements LdapAuthServiceLocal {
	private final String providerUrl = "ldaps://directory.srv.ualberta.ca:636";
	private final String authentication = "simple";
	private final String principles = "ou=people,dc=ualberta,dc=ca";

	public LdapAuthServiceBean() {
	}

	@Override
	public boolean authenticate(String uid, String pwd) {
		try {
			Hashtable<String, String> env = getEnv();
			env.put(Context.SECURITY_PRINCIPAL, "uid=" + uid + ", " + principles);
			env.put(Context.SECURITY_CREDENTIALS, pwd);
			DirContext ctx = new InitialDirContext(env);
			ctx.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public Attributes getAttributes(String uid) {
		try {
			Hashtable<String, String> env = getEnv();
			SearchResult result = searchByUid(env, uid);
			return result.getAttributes();
		} catch (Exception e) {
			return null;
		}
	}

	private Hashtable<String, String> getEnv() {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put("com.sun.jndi.ldap.connect.pool", "true");
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, providerUrl);
		env.put(Context.SECURITY_AUTHENTICATION, authentication);
		return env;
	}

	private SearchResult searchByUid(Hashtable<String, String> env, String uid) {
		try {
			DirContext ctx = new InitialDirContext(env);
			Attributes matchAttrs = new BasicAttributes(true); // ignore case
			matchAttrs.put(new BasicAttribute("uid", uid));
			NamingEnumeration<SearchResult> results = ctx.search(principles, matchAttrs);
			ctx.close();
			return results.hasMore() ? results.next() : null;
		} catch (Exception e) {
			return null;
		}
	}
}
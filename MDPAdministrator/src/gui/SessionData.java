package gui;

public class SessionData {
	String login,logout,session;
	public SessionData(String login,String logout,String session)
	{
		this.login=login;
		this.logout=logout;
		this.session=session;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getLogout() {
		return logout;
	}
	public void setLogout(String logout) {
		this.logout = logout;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
}

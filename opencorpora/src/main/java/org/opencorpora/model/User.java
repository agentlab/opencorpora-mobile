package org.opencorpora.model;

/**
 *
 * Instance of this class represents user. User has username, login and answers count.
 *
 */
public class User {

    private String username;
    private String login;
    private int answers;

    /**
     * Default constructor
     */
    public User() {
        // Do nothing
    }

    /**
     * @return username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return login
     */
    public String getLogin() {
        return login;
    }

    /**
     * @return answers count
     */
    public int getAnswers() {
        return answers;
    }

    @Override
    public String toString() {
        return "User: " + username + " (" + answers  + ')';
    }
}

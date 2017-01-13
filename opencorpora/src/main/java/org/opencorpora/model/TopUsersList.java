package org.opencorpora.model;

import java.util.Collection;

/**
 *
 * Instance of this class represents collection for top users.
 *
 */
public class TopUsersList {
    private Collection<User> users;

    /**
     * Default constructor
     */
    public TopUsersList() {
        // Do nothing
    }

    /**
     * @return top users.
     */
    public  Collection<User> getUsers() {
        return users;
    }
}

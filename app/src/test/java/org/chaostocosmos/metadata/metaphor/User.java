package org.chaostocosmos.metadata.metaphor;

/**
 * User
 * 
 * @author 9ins
 */
public class User {
    
    @MetaField(expr = "hosts[0].users[i].username")
    String username;

    @MetaField(expr = "hosts[0].users[i].password")
    String password;

    @MetaField(expr = "hosts[0].port")
    int port;

    @Override
    public String toString() {
        return "{" +
            " username='" + username + "'" +
            " password='" + password + "'" +
            " port='" + port + "'" +
            "}";
    }    
}

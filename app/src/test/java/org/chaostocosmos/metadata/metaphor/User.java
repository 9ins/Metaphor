package org.chaostocosmos.metadata.metaphor;

import java.util.List;

import org.chaostocosmos.metadata.metaphor.annotation.MetaWired;

/**
 * User
 * 
 * @author 9ins
 */
@MetaWired(expr = "hosts[0].users[0]")
public class User {
    
    String username;

    String password;

    double doubleValue;

    List<String> name;

    String grant;

    String host;

    int port;
    
    public void setUser(@MetaWired(expr = "hosts[1].users") List<String> name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    @Override
    public String toString() {
        return "{" +
            " username='" + username + "'" +
            ", password='" + password + "'" +
            ", port='" + port + "'" +
            ", doubleValue='" + doubleValue + "'" +
            ", name='" + name + "'" +
            ", grant='" + grant + "'" +
            ", host='" + host + "'" +
            "}";
    }
}

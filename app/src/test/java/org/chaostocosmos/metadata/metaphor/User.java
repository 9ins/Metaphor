package org.chaostocosmos.metadata.metaphor;

import java.util.List;

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

    double doubleValue;

    List<String> name;

    long grant;

    String pass;

    long port;
    
    public void setUser(@MetaParameter(expr = "hosts[0].users") List<String> name, String pass, long port, @MetaParameter(expr = "hosts[0].port") long grant) {
        this.name = name;
        this.grant = grant;
        this.pass = pass;
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
            ", pass='" + pass + "'" +
            "}";
    }
}

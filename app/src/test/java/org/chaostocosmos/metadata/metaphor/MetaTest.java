package org.chaostocosmos.metadata.metaphor;

import java.util.List;
import java.util.Map;

import org.chaostocosmos.metadata.metaphor.annotation.MetaWired;

public class MetaTest {

    @MetaWired(expr = "hosts[0].logs")
    String logs;

    @MetaWired(expr = "hosts[0].users[0].username")
    String username;

    @MetaWired(expr = "hosts[0].port")
    Integer port;

    @MetaWired(expr = "hosts[0].users")
    List<User> users;

    @MetaWired(expr = "hosts[0].resources")
    Map<String, String> resources;

    public List<User> getUsers() {
        return this.users;
    }

    @Override
    public String toString() {
        return "{" +
            " logs='" + logs + "'" +
            ", username='" + username + "'" +
            ", port='" + port + "'" +
            ", users='" + users.toString() + "'" +
            ", resources='" + resources.toString() + "'" +
            "}";
    }

}

package org.chaostocosmos.metadata.metaphor;

import java.util.List;
import java.util.Map;

public class MetaTest {

    @MetaField(expr = "hosts[0].logs")
    String logs;

    @MetaField(expr = "hosts[0].users[0].username")
    String username;

    @MetaField(expr = "hosts[0].port")
    Integer port;

    @MetaField(expr = "hosts[0].users")
    List<User> users;

    @MetaField(expr = "hosts[0].resources")
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

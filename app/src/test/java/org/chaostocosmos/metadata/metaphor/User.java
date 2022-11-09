package org.chaostocosmos.metadata.metaphor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    Map<String, String> map;

    Address address = new Address();

    public User() {
        this.name = new ArrayList<>();
        this.name.add("aaa");
        this.name.add("bbb");
        this.name.add("ccc");
        this.map = new HashMap<>();
        this.map.put("aaa", "111");
        this.map.put("bbb", "222");
        this.map.put("ccc", "333");
    }


    public User(String username, String password, double doubleValue, List<String> name, String grant, String host, int port) {
        this.username = username;
        this.password = password;
        this.doubleValue = doubleValue;
        this.grant = grant;
        this.host = host;
        this.port = port;
    }

    public Map getMap() {
        return this.map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getDoubleValue() {
        return this.doubleValue;
    }

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public List<String> getName() {
        return this.name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public String getGrant() {
        return this.grant;
    }

    public void setGrant(String grant) {
        this.grant = grant;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "{" +
            " username='" + getUsername() + "'" +
            ", password='" + getPassword() + "'" +
            ", doubleValue='" + getDoubleValue() + "'" +
            ", name='" + getName() + "'" +
            ", grant='" + getGrant() + "'" +
            ", host='" + getHost() + "'" +
            ", port='" + getPort() + "'" +
            ", map='" + getMap() + "'" +
            ", address='" + getAddress() + "'" +
            "}";
    }

}

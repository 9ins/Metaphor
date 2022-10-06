package org.chaostocosmos.metadata.metaphor;

import org.chaostocosmos.metadata.metaphor.annotation.MetaWired;

public class Address {

    @MetaWired(expr = "hosts[0].resources.streaming-buffer-size")
    int port;

    @Override
    public String toString() {
        return "{" +
            " port='" + port + "'" +
            "}";
    }
}

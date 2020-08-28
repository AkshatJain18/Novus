package com.org.novus;

import java.io.Serializable;

public class SeenBy implements Serializable {
    public String name,Uid;

    public SeenBy() {
    }
    public SeenBy(String name, String uid) {
        this.name = name;
        this.Uid = uid;
    }


}

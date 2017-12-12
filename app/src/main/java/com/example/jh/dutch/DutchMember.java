package com.example.jh.dutch;

import java.util.ArrayList;


class DutchMember {
    String id;
    ArrayList<String> info = new ArrayList<>();
    public DutchMember(String id){ this.id = id; }
    public String toString(){ return id; }
}

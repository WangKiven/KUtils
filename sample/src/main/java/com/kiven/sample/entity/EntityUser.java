package com.kiven.sample.entity;

/**
 * Created by wangk on 2017/12/14.
 */

public class EntityUser {
    private int id;
    public String name;
    public int age;

    public EntityUser(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "EntityUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}

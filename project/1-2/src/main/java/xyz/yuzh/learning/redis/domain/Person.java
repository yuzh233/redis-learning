package xyz.yuzh.learning.redis.domain;

import java.util.List;

/**
 * @author Harry Zhang
 * @since 2020/2/2 19:41
 */
public class Person {

    private String name;
    private Integer age;
    private List<String> hobby;


    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", hobby=" + hobby +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public List<String> getHobby() {
        return hobby;
    }

    public void setHobby(List<String> hobby) {
        this.hobby = hobby;
    }
}

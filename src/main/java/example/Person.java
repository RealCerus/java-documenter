package example;

//DOC
public class Person {

    private String name,
            surName;
    private int age;

    public Person(String name, String surName, int age) {
        this.name = name;
        this.surName = surName;
        this.age = age;
    }

    //DOC
    public void printFullName() {
        System.out.println(name + " " + surName);
    }

    //DOC
    public int getAgeDifference(Person otherPerson) {
        return Math.max(otherPerson.age, age) - Math.min(otherPerson.age, age);
    }

    /**
     * Returns a string which contains fields and their values.
     * @since 1.0
     * @return a string with fields and values
     */
    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", surName='" + surName + '\'' +
                ", age=" + age +
                '}';
    }

    public String getName() {
        return name;
    }

    public String getSurName() {
        return surName;
    }

    public int getAge() {
        return age;
    }
}

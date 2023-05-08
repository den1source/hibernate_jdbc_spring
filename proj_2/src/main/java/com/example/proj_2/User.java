package com.example.proj_2;

import javax.persistence.*;

@Entity
@Table(name = "table_of_user")
public class User {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "num_year")
    private int numYear;

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id=id;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getLastname(){
        return lastname;
    }
    public void setLastname(String Lastname){
        this.name=Lastname;
    }
    public int getNumYear()
    {
        return numYear;
    }
    public void setNumYear(int num_y){
        this.numYear=num_y;
    }
    public void table(){}

}

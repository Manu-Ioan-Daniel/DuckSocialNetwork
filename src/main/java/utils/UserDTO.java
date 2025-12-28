package utils;

import enums.DuckType;

import java.time.LocalDate;

public class UserDTO {
    private  String username;
    private  String password;
    private  String email;
    private  String type;
    private  DuckType duckType;
    private  Double speed;
    private  Double resistance;
    private  String name;
    private  String surname;
    private  LocalDate dateOfBirth;
    private  String occupation;
    private  int empathyLevel;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }

    public DuckType getDuckType() {
        return duckType;
    }

    public Double getSpeed() {
        return speed;
    }

    public Double getResistance() {
        return resistance;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getOccupation() {
        return occupation;
    }

    public int getEmpathyLevel() {
        return empathyLevel;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDuckType(DuckType duckType) {
        this.duckType = duckType;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public void setResistance(Double resistance) {
        this.resistance = resistance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public void setEmpathyLevel(int empathyLevel) {
        this.empathyLevel = empathyLevel;
    }
}

package org.example.housebackend.entity;

import lombok.Data;

@Data  // 自动生成 getter、setter、toString
public class User {
    private Integer id;
    private String name;
    private String role;
    private String phone;
    private String email;

    // Getter & Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
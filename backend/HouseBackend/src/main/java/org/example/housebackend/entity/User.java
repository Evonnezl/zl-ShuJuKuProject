package org.example.housebackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data  // 自动生成 getter、setter、toString
public class User {
    private Integer id;
    private String name;
    private String role;
    private String phone;
    private String email;
    private String password;

    // 哈希列（用于密文查找，不暴露给前端）
    @JsonIgnore
    private String emailHash;
    @JsonIgnore
    private String phoneHash;

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
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmailHash() { return emailHash; }
    public void setEmailHash(String emailHash) { this.emailHash = emailHash; }
    public String getPhoneHash() { return phoneHash; }
    public void setPhoneHash(String phoneHash) { this.phoneHash = phoneHash; }
}
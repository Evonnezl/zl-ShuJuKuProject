package org.example.housebackend.entity;

import java.time.LocalDateTime;

public class VerificationCode {
    private Integer id;
    private String email;
    private String code;
    private LocalDateTime expiresAt;
    private Integer used;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public Integer getUsed() { return used; }
    public void setUsed(Integer used) { this.used = used; }
}

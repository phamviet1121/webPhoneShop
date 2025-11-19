package com.example.phone_shop.models;

import jakarta.persistence.*;


@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;

    @Column(nullable = false)
    private String nameUser;

    @Column(nullable = false, unique = true)
    private String gmailUser;

    @Column(nullable = false)
    private String passUser;

    @Column(nullable = false, length = 15)
    private String phoneNumber;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(nullable = false)
    private String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //private List<Order> orders;

    @PrePersist
    protected void prePersist() {
        if (this.role == null || this.role.isEmpty()) {
            this.role = "user";
        }
    }

    // Getter và Setter thủ công
    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getGmailUser() {
        return gmailUser;
    }

    public void setGmailUser(String gmailUser) {
        this.gmailUser = gmailUser;
    }

    public String getPassUser() {
        return passUser;
    }

    public void setPassUser(String passUser) {
        this.passUser = passUser;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}

// package com.example.phone_shop.models;

// import jakarta.persistence.*;
// import lombok.Getter;
// import lombok.Setter;
// @Getter
// @Setter
// @Entity
// @Table(name = "users")
// public class User {
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long idUser;

//     private String nameUser;
//     private String gmailUser;
//     private String passUser;
//     private String phoneNumber;  // ✅ Kiểm tra xem có field này không
//     private String address;      // ✅ Kiểm tra xem có field này không
//     private String role;
// }


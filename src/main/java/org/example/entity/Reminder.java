package org.example.telegrambot.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "reminders")
public class Reminder {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column (name = "chat_id")
    @NotBlank
    private String chatId;

    @Column(name = "action_date")
    @NotNull
    private LocalDateTime actionDate;

    @Column(name = "create_date")
    @NotNull
    private LocalDateTime dateCreate;

    @Column(name = "message")
    @NotBlank
    private String message;

    @Column(name = "actual")
    @NotNull
    private boolean actual;

    @Override
    public String toString() {
        return "Reminder{" +
                "id=" + id +
                ", user=" + user +
                ", chatId='" + chatId + '\'' +
                ", dateAction=" + actionDate +
                ", dateCreate=" + dateCreate +
                ", message='" + message + '\'' +
                ", actual='" + actual + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public LocalDateTime getActionDate() {
        return actionDate;
    }

    public void setActionDate(LocalDateTime dateAction) {
        this.actionDate = dateAction;
    }

    public LocalDateTime getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(LocalDateTime dateCreate) {
        this.dateCreate = dateCreate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isActual() {
        return actual;
    }

    public void setActual(boolean actual) {
        this.actual = actual;
    }
}

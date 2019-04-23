package com.t3h.filternews.model;

public class User {
    private String name;
    private String username;
    private String password;
    private String email;
    private String keyword;

    public User(){

    }

    public User(UserBuilder builder) {
        this.name = builder.name;
        this.username = builder.username;
        this.password = builder.password;
        this.email = builder.email;
        this.keyword = builder.keyword;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }




    public static class UserBuilder{
        private final String username;
        private final String password;
        private String name;
        private String email;
        private String keyword;

        public UserBuilder(String username, String password){
            this.username = username;
            this.password = password;
        }

        public UserBuilder name(String name){
            this.name = name;
            return this;
        }

        public UserBuilder email(String email){
            this.email = email;
            return this;
        }

        public UserBuilder keyword(String keyword){
            this.keyword = keyword;
            return this;
        }

        public User build(){
            User user = new User(this);
            return user;
        }
    }
}

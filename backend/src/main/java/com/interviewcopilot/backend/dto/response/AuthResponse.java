package com.interviewcopilot.backend.dto.response;

public class AuthResponse {
    private String token;
    private String name;
    private String email;
    private String role;
    private String message;

    public AuthResponse() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String token;
        private String name;
        private String email;
        private String role;
        private String message;

        public Builder token(String v) { this.token = v; return this; }
        public Builder name(String v) { this.name = v; return this; }
        public Builder email(String v) { this.email = v; return this; }
        public Builder role(String v) { this.role = v; return this; }
        public Builder message(String v) { this.message = v; return this; }

        public AuthResponse build() {
            AuthResponse r = new AuthResponse();
            r.token = this.token;
            r.name = this.name;
            r.email = this.email;
            r.role = this.role;
            r.message = this.message;
            return r;
        }
    }

    public String getToken() { return token; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getMessage() { return message; }
}
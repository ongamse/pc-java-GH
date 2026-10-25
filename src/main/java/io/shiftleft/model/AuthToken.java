package io.shiftleft.model;

public class AuthToken {

  // yes there are only 2 roles so
  // having them in this class should be fine
  public static int ADMIN = 0;
  public static int USER = 1;

  private int role;

  // No-arg constructor required for JSON deserialization
  public AuthToken() {
    this.role = USER;
  }

  public AuthToken(int role) {
    this.role = role;
  }

  public boolean isAdmin() {
    return this.role == ADMIN;
  }

  public int getRole() {
    if(this.role == ADMIN) {
      return ADMIN;
    } else {
      return USER;
    }
  }

  public void setRole(int role) {
    this.role = role;
  }
}

package com.MeetingRoom.RoomM.dto;

import com.MeetingRoom.RoomM.Enums.Department;
import com.MeetingRoom.RoomM.Enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SignupRequestDTO {

 private String name;
 private String email;
 private String phone;
 private Department department;
 private Role role;
 private String password;
private String secretCode;
 public String getPhoneNumber() {
  return phone;
 }

 public void setPhoneNumber(String phone) {
  this.phone = phone;
 }
 public void setPhone(String phone) {
  this.phone = phone;
 }
}
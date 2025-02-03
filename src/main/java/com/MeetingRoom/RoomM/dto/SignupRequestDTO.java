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


        private  String email;


        private String phoneNumber;


        private Department department;
        private Role role;
        private  String password;

 public String getName() {
  return name;
 }

 public void setName(String name) {
  this.name = name;
 }

 public String getEmail() {
  return email;
 }

 public void setEmail(String email) {
  this.email = email;
 }

 public String getPhoneNumber() {
  return phoneNumber;
 }

 public void setPhoneNumber(String phoneNumber) {
  this.phoneNumber = phoneNumber;
 }

 public Department getDepartment() {
  return department;
 }

 public void setDepartment(Department department) {
  this.department = department;
 }

 public Role getRole() {
  return role;
 }

 public void setRole(Role role) {
  this.role = role;
 }

 public String getPassword() {
  return password;
 }

 public void setPassword(String password) {
  this.password = password;
 }
}

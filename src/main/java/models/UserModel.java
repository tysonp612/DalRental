package models;

 public class UserModel {
    private String username;
    private String password;
    private String email;
    //Should have array of thing rented => should have a model for things to be rented
     //Should also have an  array of things user are renting;

    public UserModel(String username, String password, String email){
        this.username = username;
        this.password = password;
        this.email = email;
    }

     public String getEmail() {
         return email;
     }

     public String getPassword() {
         return password;
     }

     public String getUsername() {
         return username;
     }

     @Override
     public String toString() {
         return this.username+" "+this.password+ " "+this.email;
     }
     //Save to database method,
    //public saveUserDataToDB

     public void check(){}
}

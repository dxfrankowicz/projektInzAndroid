package frankowicz.damian.projektinz.Model;

public class Employee {
    final String id;
    final String employeeName;
    final String employeeSalary;
    final String employeeAge;
    final String profileImage;

    public Employee(String id, String employeeName, String employeeSalary, String employeeAge, String profileImage) {
        this.id = id;
        this.employeeName = employeeName;
        this.employeeSalary = employeeSalary;
        this.employeeAge = employeeAge;
        this.profileImage = profileImage;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id='" + id + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", employeeSalary='" + employeeSalary + '\'' +
                ", employeeAge='" + employeeAge + '\'' +
                ", profileImage='" + profileImage + '\'' +
                '}';
    }
}

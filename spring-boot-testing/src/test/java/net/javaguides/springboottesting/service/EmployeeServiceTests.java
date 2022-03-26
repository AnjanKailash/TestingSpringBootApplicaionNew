package net.javaguides.springboottesting.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.javaguides.springboottesting.exception.ResourceNotFoundException;
import net.javaguides.springboottesting.model.Employee;
import net.javaguides.springboottesting.repository.EmployeeRepository;
import net.javaguides.springboottesting.service.impl.EmployeeServiceImpl;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTests {

	@Mock
	private EmployeeRepository employeeRepository;
	@InjectMocks
	private EmployeeServiceImpl employeeService;
	
	private Employee employee;
	
	@BeforeEach
	public void setup() {
		//instead of below code we are using @Mock and @InjectMock annotations
		//employeeRepository = Mockito.mock(EmployeeRepository.class);
		//employeeService = new EmployeeServiceImpl(employeeRepository);
		employee = new Employee("Ramesh", "Fadatare", "ramesh@gmail.com");
	}
	
	@DisplayName("JUnit test case for save employee method")
	@Test
	public void givenEmployeeObject_whenSaveEmployee_thenEmployeeObject() {

		//given - precondition or setup
		//Employee employee = new Employee("Ramesh", "Fadatare", "ramesh@gmail.com");
		
		given(employeeRepository.findByEmail(employee.getEmail()))
		.willReturn(Optional.empty());
		
		given(employeeRepository.save(employee)).willReturn(employee);
		
		//when - action or behavior that we are going to test
		Employee savedEmployee = employeeService.saveEmployee(employee);
		//given(employeeRepository.findAll()).willReturn(List.of(employee));
		System.out.println(savedEmployee.toString()+" -- "+employeeService.getAllEmployees().toString());
		
		//then - verify the result
		assertThat(savedEmployee).isNotNull();
	}
	
	@DisplayName("JUnit test case for save employee method which throws exception")
	@Test
	public void givenEmployeeWithExistingEmail_whenSaveEmployee_thenThrowsException() {

		//given - precondition or setup
		given(employeeRepository.findByEmail(employee.getEmail()))
		.willReturn(Optional.of(employee));
		
//		given(employeeRepository.save(employee)).willReturn(employee);
		
		org.junit.jupiter.api.Assertions.assertThrows(ResourceNotFoundException.class, () -> 
		employeeService.saveEmployee(employee));
		
		//then
		verify(employeeRepository, never()).save(any(Employee.class));
	}
	
	@DisplayName("JUnit test for getting all the employees")
	@Test
	public void givenEmployeesList_whenGetAllEmployees_thenReturnEmployeesList() {

		Employee employee1 = new Employee("Tony", "Stark", "tony@gmail.com");
		Employee employee2 = new Employee("Bony", "Bark", "bony@gmail.com");
		
		//given - precondition or setup
		given(employeeRepository.findAll()).willReturn(List.of(employee, employee1, employee2));
		
		//when - action or behavior that we are going to test
		List<Employee> employeeList = employeeService.getAllEmployees();
//		System.out.println(employeeList.toString());
		//then - verify the result
		assertThat(employeeList).isNotNull();
		assertThat(employeeList.size()).isEqualTo(3);
	}
	
	@DisplayName("JUnit test for getting all the employees negative scenario")
	@Test
	public void givenEmptyEmployeesList_whenGetAllEmployees_thenReturnEmployeesList() {

		//given - precondition or setup
		given(employeeRepository.findAll()).willReturn(Collections.emptyList());
		
		//when - action or behavior that we are going to test
		List<Employee> employeeList = employeeService.getAllEmployees();

		//then - verify the result
		assertThat(employeeList).isEmpty();
		assertThat(employeeList.size()).isEqualTo(0);
	}
	
	@DisplayName("JUnit test for getting the employee by id")
	@Test
	public void givenEmployeeObject_whenGetById_thenReturnEmployee() {
		//employeeService.saveEmployee(employee);
		//given - precondition or setup
		given(employeeRepository.findById(employee.getId())).willReturn(Optional.of(employee));
		System.out.println(employee.getId());
		//when - action or behavior that we are going to test
		Employee savedEmployee = employeeService.getEmployeeById(employee.getId()).get();
		System.out.println(savedEmployee.toString());
		//then - verify the result
		assertThat(savedEmployee.getId()).isEqualTo(employee.getId());
	}
	
	@DisplayName("JUnit test for updating employee")
	@Test
	public void givenEmployeeObject_whenUpdateEmployee_thenReturnUpdatedEmployee() {

		//given - precondition or setup
		given(employeeRepository.save(employee)).willReturn(employee);
		employee.setEmail("ram@gmail.com");
		employee.setFirstName("Ram");
		
		//when - action or behavior that we are going to test
		Employee updatedEmployee = employeeService.updateEmployee(employee);
		
		//then - verify the result
		assertThat(updatedEmployee.getEmail()).isEqualTo("ram@gmail.com");
		assertThat(updatedEmployee.getFirstName()).isEqualTo("Ram");
	}
	
	@DisplayName("JUnit test for deleting employee")
	@Test
	public void givenEmployeeId_whenDeleteEmployee_then() {

		//given - precondition or setup --> willDoNothing is used when return type is void
		willDoNothing().given(employeeRepository).deleteById(employee.getId());
		
		//when - action or behavior that we are going to test
		employeeService.deleteEmployee(employee.getId());
		
		//then - verify the result -->below method should be called once, we need to check this as 
		//method returns void
		verify(employeeRepository, times(1)).deleteById(employee.getId());

	}
}

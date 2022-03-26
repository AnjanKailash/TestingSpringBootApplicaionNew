package net.javaguides.springboottesting.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.javaguides.springboottesting.model.Employee;
import net.javaguides.springboottesting.service.EmployeeService;

import static org.mockito.BDDMockito.willDoNothing;

@WebMvcTest
public class EmployeeControllerTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private EmployeeService employeeService;
	
	@Autowired
	private ObjectMapper objectMapper; //for json
	
	@Test
	public void givenEmployeeObject_whenCreateEmployee_theReturnSavedEmployee() throws JsonProcessingException, Exception {
		
		//given - precondition or setup
		Employee employee = new Employee("Ramesh", "Fadatare", "ramesh@gmail.com");
		given(employeeService.saveEmployee(any(Employee.class)))
		.willAnswer(invocation->invocation.getArgument(0));
		
		//when - action or behavior that we are going test
		ResultActions response = mockMvc.perform(post("/api/employees")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(employee)));
		
		//then - verify the result or output using assert statements
		response.andExpect(status().isCreated())
				.andExpect(jsonPath("$.firstName", is(employee.getFirstName())))
				.andExpect(jsonPath("$.lastName", is(employee.getLastName())))
				.andExpect(jsonPath("$.email", is(employee.getEmail())));
	}
	
	@DisplayName("Given list of employees given all employees get all employees")
	@Test
	public void givenEmployeeList_whenGetAllEmployees_thenListOfEmployees() throws Exception {

		//given - precondition or setup
		List<Employee> listOfEmployees = new ArrayList<>();
		listOfEmployees.add(new Employee("Rmesh", "Fatadare", "ramesh@gmail.com"));
		listOfEmployees.add(new Employee("Tony", "Start", "tony@gmail.com"));
		given(employeeService.getAllEmployees()).willReturn(listOfEmployees);
		
		//when - action or behavior that we are going to test
		ResultActions response = mockMvc.perform(get("/api/employees"));
		
		//then - verify the result
		response.andExpect(status().isOk())
		.andDo(print())
		.andExpect(jsonPath("$.size()", is(listOfEmployees.size())));
	}
	
	@DisplayName("valid employee id should return employee")
	@Test
	public void givenValidEmployeeId_whenGetEmployeeById_thenReturnEmployee() throws Exception {

		//given - precondition or setup
		Employee employee = Employee.builder()
							.id(1L)
							.firstName("Ramesh")
							.lastName("Fadatare")
							.email("ramesh@gmail.com")
							.build();
		given(employeeService.getEmployeeById(employee.getId())).willReturn(Optional.of(employee));
		
		//when - action or behavior that we are going to test
		ResultActions response = mockMvc.perform(get("/api/employees/{id}", employee.getId()));
		
		//then - verify the result
		response.andExpect(status().isOk())
			.andDo(print())
			.andExpect(jsonPath("$.firstName", is(employee.getFirstName())))
			.andExpect(jsonPath("$.lastName", is(employee.getLastName())))
			.andExpect(jsonPath("$.email", is(employee.getEmail())));

	}
	
	@DisplayName("In valid employee id should return employee")
	@Test
	public void givenInValidEmployeeId_whenGetEmployeeById_thenReturnEmpty() throws Exception {

		//given - precondition or setup
		Employee employee = Employee.builder()
							.id(1L)
							.firstName("Ramesh")
							.lastName("Fadatare")
							.email("ramesh@gmail.com")
							.build();
		given(employeeService.getEmployeeById(employee.getId())).willReturn(Optional.empty());
		
		//when - action or behavior that we are going to test
		ResultActions response = mockMvc.perform(get("/api/employees/{id}", employee.getId()));
		
		//then - verify the result
		response.andExpect(status().isNotFound()).andDo(print());
	}
	
	
	@DisplayName("JUnit test for udate employee REST API")
	@Test
	public void givenUpdatedEmployee_whenUpdateEmployee_thenReturnUpdateEmployeeObject() throws Exception {

		//given - precondition or setup
		long employeeId = 1L;
		Employee savedEmployee = Employee.builder()
							.id(employeeId)
							.firstName("Ramesh")
							.lastName("Fadatare")
							.email("ramesh@gmail.com")
							.build();
		
		Employee updatedEmployee = Employee.builder()
							.id(employeeId)
							.firstName("Ram")
							.lastName("Jadav")
							.email("ram@gmail.com")
							.build();
		given(employeeService.getEmployeeById(employeeId)).willReturn(Optional.of(savedEmployee));
		given(employeeService.updateEmployee(any(Employee.class)))
							.willAnswer(invocation -> invocation.getArgument(0));
		
		//when - action or behavior that we are going to test
		ResultActions response = mockMvc.perform(put("/api/employees/{id}", employeeId)
										.contentType(MediaType.APPLICATION_JSON)
										.content(objectMapper.writeValueAsString(updatedEmployee)));
		
		//then - verify the result
		response.andExpect(status().isOk()).andDo(print()).andExpect(jsonPath("$.firstName", is(updatedEmployee.getFirstName())))
				.andExpect(jsonPath("$.lastName", is(updatedEmployee.getLastName()))).andExpect(jsonPath("$.email", is(updatedEmployee.getEmail())));
	}
	
	@DisplayName("JUnit test for udate employee REST API negative scenario")
	@Test
	public void givenUpdatedEmployee_whenUpdateEmployee_thenReturn404NotFound() throws Exception {

		//given - precondition or setup
		long employeeId = 1L;
		Employee savedEmployee = Employee.builder()
							.id(employeeId)
							.firstName("Ramesh")
							.lastName("Fadatare")
							.email("ramesh@gmail.com")
							.build();
		
		Employee updatedEmployee = Employee.builder()
							.id(employeeId)
							.firstName("Ram")
							.lastName("Jadav")
							.email("ram@gmail.com")
							.build();
		given(employeeService.getEmployeeById(employeeId)).willReturn(Optional.empty());
		given(employeeService.updateEmployee(any(Employee.class)))
							.willAnswer(invocation -> invocation.getArgument(0));
		
		//when - action or behavior that we are going to test
		ResultActions response = mockMvc.perform(put("/api/employees/{id}", employeeId)
										.contentType(MediaType.APPLICATION_JSON)
										.content(objectMapper.writeValueAsString(updatedEmployee)));
		
		//then - verify the result
		response.andExpect(status().isNotFound()).andDo(print());
	}
	
	
	@DisplayName("Junit for delete employee REST api")
	@Test
	public void givenEmployeeId_whenDeleteEmployee_thenReturn200() throws Exception {

		//given - precondition or setup
		long employeeId = 1L;
		willDoNothing().given(employeeService).deleteEmployee(employeeId);
		
		//when - action or behavior that we are going to test
		ResultActions response = mockMvc.perform(delete("/api/employees/{id}", employeeId));
		
		//then - verify the result
		response.andExpect(status().isOk())
				.andDo(print());
	}
	
}

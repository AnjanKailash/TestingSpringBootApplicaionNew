package net.javaguides.springboottesting.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.javaguides.springboottesting.model.Employee;
import net.javaguides.springboottesting.repository.EmployeeRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc //need this in Spring Boot test
public class EmployeeControllerITests {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@BeforeEach
	void setup() {
		employeeRepository.deleteAll();
	}
	
	@Test
	public void givenEmployeeObject_whenCreateEmployee_theReturnSavedEmployee() throws JsonProcessingException, Exception {
		
		//given - precondition or setup
		Employee employee = new Employee("Ramesh", "Fadatare", "ramesh@gmail.com");
		
		/**
		 * simply remove this mocking step from controller tests
		 */
//		given(employeeService.saveEmployee(any(Employee.class)))
//		.willAnswer(invocation->invocation.getArgument(0));
		
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
		employeeRepository.saveAll(listOfEmployees);
//		given(employeeService.getAllEmployees()).willReturn(listOfEmployees); --> no need to mock
		
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
							.firstName("Ramesh")
							.lastName("Fadatare")
							.email("ramesh@gmail.com")
							.build();
		employeeRepository.save(employee);
		
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
							.firstName("Ramesh")
							.lastName("Fadatare")
							.email("ramesh@gmail.com")
							.build();
		employeeRepository.save(employee);
		
		//when - action or behavior that we are going to test
		ResultActions response = mockMvc.perform(get("/api/employees/{id}", 1l));//send any invalid id
		
		//then - verify the result
		response.andExpect(status().isNotFound()).andDo(print());
	}
}













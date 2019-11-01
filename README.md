# exception-handling
exception handling in spring boot

# Exception Handling In SpringBoot

In this lession we will learn how to handle the exception in spring boot.

1 . Create a Patint POJO. Inorder to avoid writting getters and setters we are using **Lombok** library.

```aidl
import lombok.Data;

import java.util.Date;

@Data
public class Patient {
    private String firstName; 
    private String middleName;
    private String lastName;
    private int age;
    private Date dob;
}
```

2 . Now create a PatientService class.
Make firstName as mandatory field to create.
If firstName has no value then throw an Exception.

```aidl
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    public Patient createPatient(Patient patient) throws Exception {
        if(patient.getFirstName() == null || patient.getFirstName().isEmpty()){
            throw new Exception("required first name");
        }
        return patient;
    }
}
```

3 . Create a controller class and expose an api to create a Patient record.

```aidl
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) throws Exception {
        return new ResponseEntity<>(patientService.createPatient(patient), HttpStatus.OK);
    }
}
```

Notice the createPatient method, Exception thrown by patientService.createPatient is not handled by our controller.
We have not handled because i want to show you the exception handling behaviour of spring boot.

Now lets test our service.
SpringBoot Tomcat server runs on 8080 port by default.

Send a request with URL: 
    POST :                  http://localhost:8080/Patient
    with Request body :     { "firstName" : "" }
    
    Response 
    
    {
        "timestamp": "2019-06-07T14:05:21.993+0000",
        "status": 500,
        "error": "Internal Server Error",
        "message": "required first name",
        "path": "/Patient"
    }
    
Now lets understand why the response is returned in this format.
Since we haven't handled the exception which we thrown, SpringBoot handles the exception thrown by us. 
the response includes following things:
 
    a. the timestamp at which the exception is thrown.
    b. response status. SpringBoot doesnot know the cause of error to give the valid response status.
    c. response status error description.
    d. message thrown by the exception.
    e. the request path for which the exception is thrown.
    
I want to show you one more scenario to demonstate that springboot handles the exception.

send a request with URL : GET http://localhost:8080/Student
    
response : 

{
    "timestamp": "2019-06-07T14:16:33.981+0000",
    "status": 404,
    "error": "Not Found",
    "message": "No message available",
    "path": "/Student"
}

we haven't exposed any api for accessing the Student resource.
When we trigger this request springboot checks whether the api has been exposed or not.
if not it understands that the resource is not available.
So it throws 404 Not Found error(REST Guidelines). 

4. Now our requirement is to send Proper Response status to client.
Inorder to do it we have to modify our code. Lets handle exception in our code rather than it handled by springboot.

```aidl
    @PostMapping
    public ResponseEntity<?> createPatient(@RequestBody Patient patient)  {
        Patient patientResponse = null;
        try {
            patientResponse = patientService.createPatient(patient);
            return new ResponseEntity<>(patientResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.PRECONDITION_REQUIRED);
        }
    }
```

Observe the changes which we had done.

a. Return type of createPatient is changed from ResponseEntity<Patient> to ResponseEntity<?>.
   Now there are two different kinds of response we are returning. i.e Patient if everything goes fine and "String" when exception occures.
   So the response Entity is changed to ?.
   
b. Handling exception thrown by patientService.createPatient method.
    On success return Patient Record with Response status as 200.
    On exception rturn Exception message as Response (String , but we can send custom response of perticular type) and status as PRECONDITION_REQUIRED(Based on our business requirement we can change this).

Now lets test.

Send a request with URL: 
    POST :                  http://localhost:8080/Patient
    with Request body :     { "firstName" : "" }
    Response : required first name 
    Response Status : 428 Precondition Required.
    
5 . Now we will learn how to create a custom exception.
    Create a class and extends RuntimeException class to create a custom Exception class.
    
```aidl
public class FirstNameShouldNotBeEmptyException extends RuntimeException {
    public FirstNameShouldNotBeEmptyException(String message){
        super(message);
    }
}
```

6. Now instead of throwing the Exception we will throw Custom FirstNameShouldNotBeEmptyException 
    
```aidl
import com.tutorial.springboot.exceptionhandler.FirstNameShouldNotBeEmptyException;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    public Patient createPatient(Patient patient) {
        if(patient.getFirstName() == null || patient.getFirstName().isEmpty()){
            throw new FirstNameShouldNotBeEmptyException("required first name");
        }
        return patient;
    }
}
```

```aidl
import com.tutorial.springboot.exceptionhandler.FirstNameShouldNotBeEmptyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping
    public ResponseEntity<?> createPatient(@RequestBody Patient patient)  {
        Patient patientResponse = null;
        try {
            patientResponse = patientService.createPatient(patient);
            return new ResponseEntity<>(patientResponse, HttpStatus.OK);
        } catch (FirstNameShouldNotBeEmptyException e) {
            return new ResponseEntity<>(e, HttpStatus.PRECONDITION_REQUIRED);
        }
    }

}
```
    

Now lets test.

Send a request with URL: 
    POST :                  http://localhost:8080/Patient
    with Request body :     { "firstName" : "" }
    Response : 
    {
        "cause": null,
        "stackTrace": [],
        "localizedMessage": "required first name",
        "message": "required first name",
        "suppressed": []
    }
    Response Status : 428 Precondition Required.
    
Since from our controller logic we are returning FirstNameShouldNotBeEmptyException object.
it includes the stacktrace and other information. 

6 . In real time development custom exceptions will be used in many places.
For example we can use FirstNameShouldNotBeEmptyException while creating and upating the Patient record.
Both time we have to send the response status explicitly.

So to avoid this limitation we can add @ResponseStatus annotation.

```aidl
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PRECONDITION_REQUIRED)
public class FirstNameShouldNotBeEmptyException extends RuntimeException {
    public FirstNameShouldNotBeEmptyException(String message){
        super(message);
    }
}
```

```aidl
@RestController
@RequestMapping("/Patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        return new ResponseEntity<>(patientService.createPatient(patient), HttpStatus.OK);
    }

}
```

Now lets test.

Send a request with URL: 
    POST :                  http://localhost:8080/Patient
    with Request body :     { "firstName" : "" }
    Response : 
    {
        "timestamp": "2019-06-07T16:58:58.575+0000",
        "status": 428,
        "error": "Precondition Required",
        "message": "required first name",
        "path": "/Patient"
    }
    Response Status : 428 Precondition Required.

Even through the spring boot handles the exception it is sending the proper response status.
Now lets see how to customize the response thrown.

7 . create a custom error response class 

```aidl
import lombok.Data;

@Data
public class CustomExceptionResponse {
    private String message;
    private String timeStamp;
}
```

now whenever the FirstNameShouldNotBeEmptyException thrown the response should be in CustomExceptionResponse format.

Inorder to do it we need to have handling logic at one place.

SpringBoot gives an annotations i.e @RestControllrAdvice and @ExceptionHandler to handle perticular type of exceptions at one place.

```aidl
public class FirstNameShouldNotBeEmptyException extends RuntimeException {
    public FirstNameShouldNotBeEmptyException(String message){
        super(message);
    }
}
``` 

```aidl

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
public class ApplicationExceptionHandler {
    
    @ExceptionHandler(FirstNameShouldNotBeEmptyException.class)
    public ResponseEntity<CustomExceptionResponse> handleFirstNameShouldNotBeEmptyException(
            FirstNameShouldNotBeEmptyException ex, WebRequest web){
        final CustomExceptionResponse response = new CustomExceptionResponse();
        response.setMessage(ex.getMessage());
        response.setTimeStamp(new Date().toString());
        return new ResponseEntity<>(response, HttpStatus.PRECONDITION_REQUIRED);
    }    
}

```
 
 Now lets test.
 
 Send a request with URL: 
     POST :                  http://localhost:8080/Patient
     with Request body :     { "firstName" : "" }
     Response : 
     {
         "message": "required first name",
         "timeStamp": "Fri Jun 07 22:47:34 IST 2019"
     }
     Response Status : 428 Precondition Required.
     
     
 Note: We can handle multiple exception handling in a same method.
 i.e
 ```aidl
@ExceptionHandler({ CustomException1.class, CustomException2.class })
    public void handleException() {
        //
    }
```
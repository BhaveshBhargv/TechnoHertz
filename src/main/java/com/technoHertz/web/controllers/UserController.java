package com.technoHertz.web.controllers;

import com.technoHertz.web.models.DataResponse;
import com.technoHertz.web.models.ResponseMessage;
import com.technoHertz.web.models.UserPojo;
import com.technoHertz.web.models.UserData;
import com.technoHertz.web.repositories.UserRepository;
import com.technoHertz.web.security.AuthenticationRequest;
import com.technoHertz.web.security.AuthenticationResponse;
import com.technoHertz.web.security.JwtUtil;
import com.technoHertz.web.services.EmailService;
import com.technoHertz.web.services.MyUserDetailsService;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
public class UserController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    MyUserDetailsService userDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/auth")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()));
        }
        catch (BadCredentialsException e) {
            throw new Exception("Incorrect UserName or Password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @Transactional
    @PostMapping("/user/register")
    public ResponseEntity<?> registerUser(@RequestPart("user") UserPojo userPojo, @RequestPart("file") MultipartFile file) {

        try {

            boolean valid = EmailValidator.getInstance().isValid(userPojo.getEmail());
            if(!valid) {
                    throw new Exception(String.format("Email address invalid, Email ID: %s", userPojo.getEmail()));
            }

            Optional<UserData> ExistingUser = userRepository.findByEmail(userPojo.getEmail());
            if (ExistingUser.isPresent()) {
                throw new Exception(String.format("User already exists with email id: %s", userPojo.getEmail()));
            }

            UserData user = new UserData();

            user.setFirstName(userPojo.getFirstName());
            user.setLastName(userPojo.getLastName());
            user.setCompany(userPojo.getCompany());
            user.setEmail(userPojo.getEmail());
            user.setPassword(generatePassword());
            user.setTitle(userPojo.getTitle());
            user.setPhoneNumber(userPojo.getPhoneNumber());
            user.setDepartment(userPojo.getDepartment());
            user.setJobType(userPojo.getJobType());
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            user.setFileName(fileName);
            user.setType(file.getContentType());
            user.setData(file.getBytes());

            user = userRepository.save(user);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject("Welcome to ABC corporation");
            mailMessage.setFrom("your mail address"); //Add your email address from which the email would be sent
            mailMessage.setText("Hi Newbie,\nWelcome to ABC corporation. We are glad that you choose to join us. Your credentials are mentioned below: \n\n" +
                    "Username : " + user.getEmail() + "\nPassword: " + user.getPassword());

            emailService.sendEmail(mailMessage);

            ResponseMessage message = new ResponseMessage();
            message.setMessage("User registered successfully, Please check registered email for credentials");
            return ResponseEntity.ok(message);
        }
        catch (Exception e) {
            String error = e.getMessage();
            ResponseMessage message = new ResponseMessage();
            message.setMessage(error);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(message);
        }
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<?> getUser(@PathVariable("email") String email) {
        try {

            Optional<UserData> userDetailsOptional = userRepository.findByEmail(email);

            if(userDetailsOptional.isEmpty()) {
                throw new Exception(String.format("No user found with email: %s", email));
            }

            UserData userData = userDetailsOptional.get();
            UserPojo userPojo = UserPojo.convetTOUserPojo(userData);
            Map<String, Object> responseData = new HashMap<String, Object>();
            responseData.put("user", userPojo);
            DataResponse response = new DataResponse();
            response.setData(responseData);
            return ResponseEntity.ok(response);

        }
        catch (Exception e) {
            String error = e.getMessage();
            ResponseMessage message = new ResponseMessage();
            message.setMessage(error);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(message);
        }
    }

    @GetMapping("/user/{email}/file")
    public ResponseEntity<?> getAttachedFile(@PathVariable("email") String email) {
        try {

            Optional<UserData> userDetailsOptional = userRepository.findByEmail(email);

            if(userDetailsOptional.isEmpty()) {
                throw new Exception(String.format("No user found with email: %s", email));
            }

            UserData userData = userDetailsOptional.get();

            String filePath = userData.getFileName();
            MediaType mediaType;
            if(filePath.contains(".pdf")) {
                mediaType = MediaType.APPLICATION_PDF;
            } else {
                mediaType = MediaType.IMAGE_JPEG;
            }
            return ResponseEntity.ok().contentType(mediaType).body(userData.getData());

        }
        catch (Exception e) {
            String error = e.getMessage();
            ResponseMessage message = new ResponseMessage();
            message.setMessage(error);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(message);
        }
    }

    @Transactional
    @PostMapping("/user/{email}")
    public ResponseEntity<?> updateUser(@PathVariable("email") String email, @RequestPart("user") UserPojo userPojo, @RequestPart("file") MultipartFile file) {
        try {

            Optional<UserData> userDetailsOptional = userRepository.findByEmail(email);

            if (userDetailsOptional.isEmpty()) {
                throw new Exception(String.format("No user found with email: %s", email));
            }

            UserData user = userDetailsOptional.get();

            user.setFirstName(userPojo.getFirstName());
            user.setLastName(userPojo.getLastName());
            user.setCompany(userPojo.getCompany());
            user.setEmail(userPojo.getEmail());
            user.setTitle(userPojo.getTitle());
            user.setPhoneNumber(userPojo.getPhoneNumber());
            user.setDepartment(userPojo.getDepartment());
            user.setJobType(userPojo.getJobType());
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            user.setFileName(fileName);
            user.setType(file.getContentType());
            user.setData(file.getBytes());

            user = userRepository.save(user);

            ResponseMessage message = new ResponseMessage();
            message.setMessage("User updated successfully.");
            return ResponseEntity.ok(message);

        }
        catch (Exception e) {
            String error = e.getMessage();
            ResponseMessage message = new ResponseMessage();
            message.setMessage(error);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(message);
        }
    }

    @Transactional
    @DeleteMapping("/user/{email}")
    public ResponseEntity<?> deleteUser(@PathVariable("email") String email) {
        try {

            Optional<UserData> userDetailsOptional = userRepository.findByEmail(email);

            if (userDetailsOptional.isEmpty()) {
                throw new Exception(String.format("No user found with email: %s", email));
            }

            userRepository.deleteByEmail(email);

            ResponseMessage message = new ResponseMessage();
            message.setMessage("User deleted successfully.");
            return ResponseEntity.ok(message);
        }
        catch (Exception e) {
            String error = e.getMessage();
            ResponseMessage message = new ResponseMessage();
            message.setMessage(error);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(message);
        }
    }


    private static String generatePassword() {
        String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String specialCharacters = "!@#$";
        String numbers = "1234567890";
        String combinedChars = capitalCaseLetters + lowerCaseLetters + specialCharacters + numbers;
        Random random = new Random();
        StringBuilder password = new StringBuilder();

        password.append(lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length())));
        password.append(capitalCaseLetters.charAt(random.nextInt(capitalCaseLetters.length())));
        password.append(specialCharacters.charAt(random.nextInt(specialCharacters.length())));
        password.append(numbers.charAt(random.nextInt(numbers.length())));

        for(int i = 4; i< 10 ; i++) {
            password.append(combinedChars.charAt(random.nextInt(combinedChars.length())));
        }
        return password.toString();
    }
}

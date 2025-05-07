package org.ai.server.serviceimpl;

import lombok.AllArgsConstructor;
import org.ai.server.Repository.UserRepository;
import org.ai.server.configuration.JwtTokenProvider;
import org.ai.server.dto.Response;
import org.ai.server.dto.UserDto;
import org.ai.server.enumPackage.Role;
import org.ai.server.mapper.DtoConverter;
import org.ai.server.model.UserEntity;
import org.ai.server.request.LoginRequest;
import org.ai.server.service.AuthService;
import org.ai.server.service.CloudinaryService;
import org.ai.server.service.EmailService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Service
@AllArgsConstructor
public class AuthServiceHandler implements AuthService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final CloudinaryService cloudinaryService;
    @Override
    public Response LoginUser(LoginRequest loginRequest) {

        try {
            // Authenticate the user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), loginRequest.getPassword()
                    )
            );

            // Fetch user details
            UserEntity userEntity = userRepository.findByEmail(loginRequest.getEmail());
            if (userEntity == null) {
                return Response.error("The user is not registered",401);
            }

            // Generate JWT token
            String token = jwtTokenProvider.generateToken(userEntity);
            Date expirationDate = jwtTokenProvider.extractExpiration(token);

            // Convert UserEntity to UserDto
            UserDto userDto = DtoConverter.convertUsertoUserDto(userEntity);

            return Response.success("The account has been logged in successfully.")
                    .withUser(DtoConverter.convertUsertoUserDto(userEntity))
                    .withTokenAndExpirationTime(token,expirationDate.toString());



        } catch (Exception e) {
            return Response.error("Login failed: " + e.getMessage(), 500);
        }


    }

    @Override
    public Response RegisterUser(UserEntity user, MultipartFile imageFile) {



        try{
            if (userRepository.existsByEmail(user.getEmail())) {
                return Response.error("Email already in use", 400);
            }
            // 1. Validate input
            if (imageFile == null || imageFile.isEmpty()) {
                return Response.error("Profile image is required", 400);
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            if(user.getRole()== Role.SELLER){
                user.setRole(Role.SELLER);
            }
            else{
                user.setRole(Role.USER);
            }
            // 3. Upload thumbnail to Cloudinary
            String thumbnailUrl = cloudinaryService.uploadFile(imageFile);
            if (thumbnailUrl == null) {
                return Response.error("Failed to upload profile image", 500);
            }
            user.setImage(thumbnailUrl);

            UserEntity savedUser=userRepository.save(user);
            return Response.success("User registered successfully")
                    .withUser(DtoConverter.convertUsertoUserDto(savedUser));


        }catch(Exception e){
            return Response.error("Registration failed please try again " , 500);
        }
    }


}

package org.ai.server.serviceimpl;

import lombok.AllArgsConstructor;
import org.ai.server.Repository.UserRepository;
import org.ai.server.configuration.JwtTokenProvider;
import org.ai.server.dto.Response;
import org.ai.server.mapper.DtoConverter;
import org.ai.server.model.UserEntity;
import org.ai.server.service.UserService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceHandler implements UserService {

    private final UserRepository userRepository;


    @Override
    public Response getUserByToken(String token) {
       try{
           token=token.substring(7);
           String email=JwtTokenProvider.extractUsername(token);
           UserEntity user=userRepository.findByEmail(email);
           if(user==null){
               throw new RuntimeException("User not found");
           }
           UserEntity userEntity=new UserEntity();
           userEntity.setEmail(email);
           userEntity.setImage(user.getImage());
           userEntity.setName(user.getName());
           userEntity.setId(user.getId());
           userEntity.setRole(user.getRole());
           userEntity.setPassword(user.getPassword());
           userEntity.setCartItems(user.getCartItems());




           return Response.success("The user get successfully").withUser(DtoConverter.convertUsertoUserDto(userEntity));

       }catch(RuntimeException e){
           return Response.error("The user not found",400);
       } catch (Exception e) {
           return Response.error("An error occurred",500);
       }
    }
}

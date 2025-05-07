package org.ai.server.serviceimpl;

import lombok.AllArgsConstructor;
import org.ai.server.Repository.UserRepository;
import org.ai.server.dto.Response;
import org.ai.server.mapper.DtoConverter;
import org.ai.server.model.UserEntity;
import org.ai.server.request.UpdateCartRequest;
import org.ai.server.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CartServiceHandler implements CartService {

    private final UserRepository userRepository;

    @Override
    public Response updateCartItems(UpdateCartRequest updateCartRequest, Long id) {

        try{
            Optional<UserEntity> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                UserEntity user = userOptional.get();

                // Create a new map to ensure proper persistence
                Map<String, Integer> newCartItems = new HashMap<>(updateCartRequest.getCartItems());
                user.setCartItems(newCartItems);

               user= userRepository.save(user);

                return Response.success("Successfully updated cart items").withUser(DtoConverter.convertUsertoUserDto(user));
            } else {
        return Response.error("The user is not available in database",403);
            }


        } catch (Exception e) {
           return Response.error("CartItems cannot be updated try again",403);
        }

    }
}

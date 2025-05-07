package org.ai.server.serviceimpl;

import lombok.AllArgsConstructor;
import org.ai.server.Repository.AddressRepository;
import org.ai.server.dto.Response;
import org.ai.server.mapper.DtoConverter;
import org.ai.server.model.AddressEntity;
import org.ai.server.service.AddressService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AddressServiceHandler implements AddressService {

    private final AddressRepository addressRepository;

    @Override
    public Response addAddress(AddressEntity address) {

        try{
            AddressEntity addressEntity = addressRepository.save(address);

            return Response.success("The address has been added successfully").withAddress(DtoConverter.convertAddresstoAddressDto(addressEntity));



        } catch (Exception e) {
            return Response.error("The address has not been added successfully",500);

        }

    }

    @Override
    public Response getAddressUser(Long userId) {
        try{
            List<AddressEntity> addressEntity=addressRepository.findByUserId(userId);
            if(addressEntity!=null){
                return Response.success("User address can be fetched successfully").withAddress(DtoConverter.convertAddressListToAddressDtoList(addressEntity));

            }
            else{
                return Response.error("User address can not be fetched successfully",500);

            }




        } catch (Exception e) {
            return Response.error("The address has not been fetched successfully",500);

        }
    }
}

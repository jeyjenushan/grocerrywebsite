
package org.ai.server.serviceimpl;

import lombok.AllArgsConstructor;
import org.ai.server.Repository.ProductRepository;
import org.ai.server.dto.Response;
import org.ai.server.mapper.DtoConverter;
import org.ai.server.model.ProductEntity;
import org.ai.server.service.CloudinaryService;
import org.ai.server.service.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductServiceHandler implements ProductService {
    private final ProductRepository productRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public Response AddProduct(ProductEntity productEntity, List<MultipartFile>images) {
        try {
            // Validate required fields (optional but recommended)
            if (productEntity.getName() == null || productEntity.getDescription() == null ||
                    productEntity.getPrice() == null || productEntity.getOfferPrice() == null ||
                    productEntity.getCategory() == null || images == null || images.isEmpty()) {
                return Response.error("Missing required product fields", 400);
            }
            if (productEntity.getImage() == null) {
                productEntity.setImage(new ArrayList<>());
            }
          for(MultipartFile image: images) {
              String thumbnailUrl = cloudinaryService.uploadFile(image);
              productEntity.getImage().add(thumbnailUrl);
          }


            // Save the product to the database
            ProductEntity savedProduct = productRepository.save(productEntity);

            return Response.success("Product added successfully").withProduct(DtoConverter.convertProducttoProductDto(savedProduct));

        } catch (Exception e) {
            return Response.error("Error adding product: " + e.getMessage(), 500);
        }
    }

    @Override
    public Response productList() {
       try{
           List<ProductEntity> products = productRepository.findAll();
           if (products.isEmpty()) {
               return Response.error("No products found", 400);
           }
           return  Response.success("Products can be fetched successfully").withProducts(DtoConverter.convertProductListToProductDto(products));


       } catch (Exception e) {
           return Response.error("Products are not fetched successfully", 400);

       }
    }

    @Override
    public Response productById(Long id) {
        try{
           ProductEntity productEntity = productRepository.findById(id).orElseThrow(()-> new RuntimeException("Product not found"));
            if (productEntity == null) {
                return Response.error("Product cannot be found with this id", 400);
            }
            return  Response.success("Products can be fetched successfully").withProduct(DtoConverter.convertProducttoProductDto(productEntity));


        } catch (Exception e) {
            return Response.error("Product cannot be found with this id", 400);

        }
    }

    @Override
    public Response changeStock(Long id, boolean stock) {
        try{

            ProductEntity productEntity = productRepository.findById(id).orElseThrow(()-> new RuntimeException("Product not found"));
            if (productEntity == null) {
                return Response.error("Product cannot be found with this id", 400);
            }
            productEntity.setInStock(stock);
           productEntity= productRepository.save(productEntity);
            return  Response.success("Products can be updated successfully").withProduct(DtoConverter.convertProducttoProductDto(productEntity));


        } catch (Exception e) {
            return Response.error("Product cannot be found with this id", 400);

        }
    }
}

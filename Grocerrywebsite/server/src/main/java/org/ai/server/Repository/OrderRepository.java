package org.ai.server.Repository;

import org.ai.server.enumPackage.PaymentType;
import org.ai.server.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrderRepository extends JpaRepository<OrderEntity,Long> {
    OrderEntity findByUserId(Long userId);

    List<OrderEntity> findByUserIdAndPaymentTypeOrIsPaid(Long userId, PaymentType paymentType, boolean isPaid);
    List<OrderEntity> findByPaymentTypeAndIsPaid(PaymentType paymentType, boolean isPaid);

    List<OrderEntity> findByUserIdAndIsPaid(Long userId,  boolean b);

    List<OrderEntity> findByUserIdOrIsPaid(Long userId, boolean b);

    List<OrderEntity> findByIsPaid(boolean b);
}

package com.djccnt15.northwind.db.projection;

import com.djccnt15.northwind.db.entity.AppUserEntity;
import com.djccnt15.northwind.db.entity.EmployeeEntity;

/**
 * Spring Data JPA의 "closed projection"이 아니다. {@link OrderTotalProjection}처럼 getter가
 * 스칼라 타입을 반환하고 필요한 컬럼만 SELECT하여 비관리(non-managed) 상태로 값을 담아오는 방식과 달리,
 * 이 인터페이스의 getter는 {@link AppUserEntity}/{@link EmployeeEntity} 엔티티 타입 자체를 반환한다.
 *
 * <p>{@code AppUserRepo.findFullByIdInOrderById}의 {@code JOIN FETCH} 쿼리에서 {@code SELECT u as appUser, e as employee}로
 * 조회된, 영속성 컨텍스트에 관리되는(managed) 엔티티를 튜플처럼 묶어 반환하는 wrapper일 뿐이며,
 * 연관 엔티티까지 함께 fetch하므로 오히려 일반 엔티티 조회보다 무겁다.
 */
public interface UserEmployeeProjection {
    
    AppUserEntity getAppUser();
    
    EmployeeEntity getEmployee();
}

package com.SmartPackageSurveillance.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.SmartPackageSurveillance.entity.Container;

import java.util.Optional;

public interface ContainerRepository extends JpaRepository<Container, Long> {

    Optional<Container> findByContainerCode(String containerCode);}
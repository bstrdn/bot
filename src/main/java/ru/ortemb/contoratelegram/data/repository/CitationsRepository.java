package ru.ortemb.contoratelegram.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ortemb.contoratelegram.data.entity.Citations;

public interface CitationsRepository extends JpaRepository<Citations, String> {

}
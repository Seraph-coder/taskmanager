package ru.naujava.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения Task Manager.
 *
 * @author Seraph-coder
 * @since 01.11.2025
 */
@SpringBootApplication
public class TaskManagerApplication {
    /**
     * Точка входа в приложение.
     */
	public static void main(String[] args) {

        SpringApplication.run(TaskManagerApplication.class, args);
	}

}

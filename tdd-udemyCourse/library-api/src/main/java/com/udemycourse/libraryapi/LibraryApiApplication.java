package com.udemycourse.libraryapi;

import com.udemycourse.libraryapi.api.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class LibraryApiApplication {

//    @Autowired
//    private EmailService emailService;

//    @Bean   // executa direto assim que roda o código - testando envio de email mailtrap.io
//    public CommandLineRunner runner() {
//        return args -> {
//            List<String> emails = Arrays.asList("c9f40e4621-618dee@inbox.mailtrap.io");
//            emailService.sendMails("Testado serviço de emails.", emails);
//            System.out.println("EMAIL ENVIADO COM SUCESSO!");
//        };
//    }

    public static void main(String[] args) {
        SpringApplication.run(LibraryApiApplication.class, args);
    }

//    @Scheduled(cron = "0 50 13 ? * *") // só utiliza 6 parâmetros - todos os dias as 13h50
//    public void testeAgendamentoTarefas() {
//        System.out.println("Agendamento de tarefas funcionando com sucesso!");
//    }
}

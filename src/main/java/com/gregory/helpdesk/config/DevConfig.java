package com.gregory.helpdesk.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.gregory.helpdesk.services.DBService;

@Configuration
@Profile("dev")
public class DevConfig {
	
	@Autowired
	private DBService dbService;
	
	@Value("${spring.jpa.hibernate.ddl-auto}")
	private String value;
	
	@Bean //sempre que o perfil estiver ativo, este metodo vai subir automatico
	public boolean instanciaDB() {
		if (value.equals("create")) {
			this.dbService.instanciaDB();	
		}
		
		return false;		
	}
}

package com.gregory.helpdesk.domain.enums;

public enum Prioridade {
	
	BAIXA(0, "ROLE_BAIXA"),
	MEDIA(1, "ROLE_MEDIA"),
	ALTA(2, "ROLE_ALTA");
	
	private Integer codigo;
	private String descricao;
	
	private Prioridade(Integer codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	
	public static Prioridade toEnum(Integer cod) {
		if(cod == null) {
			return null;
		}
		
		for (Prioridade priori : Prioridade.values()) {
			if (cod == priori.getCodigo()) {
				return priori;
			}
		}
		
		throw new IllegalArgumentException("Prioridade invalido");
	}
	
}

package com.gregory.helpdesk.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gregory.helpdesk.domain.Chamado;
import com.gregory.helpdesk.domain.Cliente;
import com.gregory.helpdesk.domain.Tecnico;
import com.gregory.helpdesk.domain.dtos.ChamadoDTO;
import com.gregory.helpdesk.domain.enums.Prioridade;
import com.gregory.helpdesk.domain.enums.Status;
import com.gregory.helpdesk.repositories.ChamadoRepository;
import com.gregory.helpdesk.services.exceptions.ObjectNotFoundException;

@Service
public class ChamadoService {
	
	@Autowired
	private ChamadoRepository repository;
	@Autowired
	private TecnicoService tecnicoService;
	@Autowired
	private ClienteService clienteService;
	
	public Chamado findById(Integer id) {
		
		Optional<Chamado> obj = repository.findById(id);
		
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado id: " + id));
	}

	public List<Chamado> findAll() {
		return repository.findAll();
	}

	public Chamado create(@Valid ChamadoDTO objDTO) {
		return repository.save(newChamado(objDTO));
	}
	
	public Chamado update(Integer id, @Valid ChamadoDTO objDTO) {
		objDTO.setId(id); // tratando para que não seja passado dados invalidos
		Chamado oldObj = findById(id);//valida se exite o id informado, se não, ja lança a exceção personalizada
		oldObj = newChamado(objDTO);
		return repository.save(oldObj);
	}
	
	private Chamado newChamado(ChamadoDTO obj) {
		
		Tecnico tecnico = tecnicoService.findById(obj.getTecnico()); 
		
		Cliente cliente = clienteService.findById(obj.getCliente());
		
		Chamado chamado = new Chamado();
		
		if (obj.getId() != null) {
			chamado.setId(obj.getId());
		}
		if (obj.getStatus().equals(2)) {//verifica se no update o status está com encerrado
			chamado.setDataFechamento(LocalDate.now());//se sim, atualiza a data de encerramento
		}
		
		chamado.setTecnico(tecnico);
		chamado.setCliente(cliente);
		chamado.setPrioridade(Prioridade.toEnum(obj.getPrioridade()));
		chamado.setStatus(Status.toEnum(obj.getStatus()));
		chamado.setTitulo(obj.getTitulo());
		chamado.setObservacoes(obj.getObservacoes());
		
		return chamado;
		
	}

	
}









package com.gregory.helpdesk.services;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.gregory.helpdesk.domain.Pessoa;
import com.gregory.helpdesk.domain.Tecnico;
import com.gregory.helpdesk.domain.dtos.TecnicoDTO;
import com.gregory.helpdesk.repositories.PessoaRepository;
import com.gregory.helpdesk.repositories.TecnicoRepository;
import com.gregory.helpdesk.services.exceptions.DataIntegrityViolationException;
import com.gregory.helpdesk.services.exceptions.ObjectNotFoundException;

@Service
public class TecnicoService {
	
	@Autowired
	private TecnicoRepository repository;
	@Autowired
	private PessoaRepository pessoaRepository;
	@Autowired
	private BCryptPasswordEncoder enconder;
	
	public Tecnico findById(Integer id) {
		Optional<Tecnico> obj = repository.findById(id);
		
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! id: " + id));
		
	}

	public List<Tecnico> findAll() {
		return repository.findAll();
	}

	public Tecnico create(TecnicoDTO objDTO) {
		objDTO.setId(null);
		objDTO.setSenha(enconder.encode(objDTO.getSenha()));
		validaPorCpfEEmail(objDTO);
		Tecnico newObj = new Tecnico(objDTO);
		
		return repository.save(newObj);
	}
	
	public Tecnico update(Integer id, @Valid TecnicoDTO objDTO) {
		objDTO.setId(id);
		Tecnico oldObj = findById(id);
		
		if (!objDTO.getSenha().equals(oldObj.getSenha())) {
			objDTO.setSenha(enconder.encode(objDTO.getSenha()));
		}
		
		validaPorCpfEEmail(objDTO);
		oldObj = new Tecnico(objDTO);
		return repository.save(oldObj);
	}
	
	public void delete(Integer id) {
		
		Tecnico obj = findById(id);
		if (obj.getChamados().size() > 0) {
			throw new DataIntegrityViolationException("Tecnico possui orderns de servicço e não pode ser deletado!");
		}
		
		repository.deleteById(id);		
	}
	
	private void validaPorCpfEEmail(TecnicoDTO objDTO) {
		
		Optional<Pessoa> obj = pessoaRepository.findByCpf(objDTO.getCpf());
		if (obj.isPresent() && obj.get().getId() != objDTO.getId()) {
			throw new DataIntegrityViolationException("CPF já cadastrado no sistema!");
		}
		
		obj = pessoaRepository.findByEmail(objDTO.getEmail());
		if (obj.isPresent() && obj.get().getId() != objDTO.getId()) {
			throw new DataIntegrityViolationException("E-mail já cadastrado no sistema!");
		}
		
	}

	

	
	
}

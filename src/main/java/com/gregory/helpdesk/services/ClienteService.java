package com.gregory.helpdesk.services;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.gregory.helpdesk.domain.Cliente;
import com.gregory.helpdesk.domain.Pessoa;
import com.gregory.helpdesk.domain.dtos.ClienteDTO;
import com.gregory.helpdesk.repositories.ClienteRepository;
import com.gregory.helpdesk.repositories.PessoaRepository;
import com.gregory.helpdesk.services.exceptions.DataIntegrityViolationException;
import com.gregory.helpdesk.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository repository;
	@Autowired
	private PessoaRepository pessoaRepository;
	@Autowired
	private BCryptPasswordEncoder enconder;
	
	public Cliente findById(Integer id) {
		Optional<Cliente> obj = repository.findById(id);
		
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! id: " + id));
		
	}

	public List<Cliente> findAll() {
		return repository.findAll();
	}

	public Cliente create(ClienteDTO objDTO) {
		objDTO.setId(null);
		//pega a senha que está sendo passada no create, encoda ela e seta no objeto que vai ser salvo no banco com uma senha encodade
		objDTO.setSenha(enconder.encode(objDTO.getSenha()));
		validaPorCpfEEmail(objDTO);
		Cliente newObj = new Cliente(objDTO);
		
		return repository.save(newObj);
	}
	
	public Cliente update(Integer id, @Valid ClienteDTO objDTO) {
		objDTO.setId(id);
		Cliente oldObj = findById(id);
		
		if (!objDTO.getSenha().equals(oldObj.getSenha())) {
			objDTO.setSenha(enconder.encode(objDTO.getSenha()));
		}
		
		validaPorCpfEEmail(objDTO);
		oldObj = new Cliente(objDTO);
		return repository.save(oldObj);
	}
	
	public void delete(Integer id) {
		
		Cliente obj = findById(id);
		if (obj.getChamados().size() > 0) {
			throw new DataIntegrityViolationException("Cliente possui orderns de servicço e não pode ser deletado!");
		}
		
		repository.deleteById(id);		
	}
	
	private void validaPorCpfEEmail(ClienteDTO objDTO) {
		
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

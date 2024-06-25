package com.example.buensaborback.service;

import com.example.buensaborback.domain.entities.Cliente;
import com.example.buensaborback.repositories.ClienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ClienteServiceImpl extends BaseServiceImpl<Cliente, Long> implements ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        super(clienteRepository);
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public List<Cliente> buscarXNombreYEliminado(String busqueda, boolean mostrarEliminados) throws Exception {
        try {
            return clienteRepository.buscarXNombreYEliminado(busqueda, mostrarEliminados);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public Cliente buscarXUsuarioAuth0(String busqueda) throws Exception {
        try {
            return clienteRepository.buscarXUsuarioAuth0(busqueda);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}

package com.example.buensaborback.service;

import com.example.buensaborback.domain.entities.Categoria;
import com.example.buensaborback.repositories.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class CategoriaServiceImpl extends BaseServiceImpl<Categoria, Long> implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        super(categoriaRepository);
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public List<Categoria> buscarXSucursal(Long sucursalId) throws Exception {
        try {
            return categoriaRepository.buscarXSucursal(sucursalId);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}

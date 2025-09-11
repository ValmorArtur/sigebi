package com.biblioteca.controller.api;

import com.biblioteca.model.Autor;
import com.biblioteca.repository.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/autores")
public class AutorApiController {

    @Autowired
    private AutorRepository autorRepository;

    // GET /api/autores?q=tolk -> [{"id": 1, "nome": "J. R. R. Tolkien"}, ...]
    @GetMapping
    public List<Map<String, Object>> search(@RequestParam(required = false) String q) {
        List<Autor> lista = (q == null || q.isBlank())
                ? autorRepository.findTop20ByOrderByNomeAutorAsc()
                : autorRepository.findTop20ByNomeAutorContainingIgnoreCaseOrderByNomeAutorAsc(q.trim());

        List<Map<String, Object>> out = new ArrayList<>(lista.size());
        for (Autor a : lista) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", a.getIdAutor());      // <- Tom Select espera "id"
            m.put("nome", a.getNomeAutor());  // <- e "nome"
            out.add(m);
        }
        return out;
    }
}

package com.pcbuilder.ms_componentes.config;

import com.pcbuilder.ms_componentes.entity.Categoria;
import com.pcbuilder.ms_componentes.entity.Componente;
import com.pcbuilder.ms_componentes.repository.CategoriaRepository;
import com.pcbuilder.ms_componentes.repository.ComponenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Carga el 30% restante de los datos de prueba (el 70% lo hace Liquibase en
 * db.changelog-master.xml). Solo inserta si las tablas están vacías para no
 * duplicar datos en cada arranque.
 */
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final ComponenteRepository componenteRepo;
    private final CategoriaRepository categoriaRepo;

    @Override
    public void run(String... args) {
        if (componenteRepo.count() >= 10) {
            return;
        }

        Categoria cpu = categoriaRepo.findById(2L).orElseThrow();

        componenteRepo.save(crearComponente("Ryzen 7 7800X3D", "AMD", 450000.0, 5, cpu));
        componenteRepo.save(crearComponente("Core i7 14700K", "Intel", 480000.0, 7, cpu));
        componenteRepo.save(crearComponente("Ryzen 5 5600G", "AMD", 150000.0, 30, cpu));
    }

    private Componente crearComponente(String nombre, String marca, Double precio, Integer stock, Categoria categoria) {
        Componente c = new Componente();
        c.setNombre(nombre);
        c.setMarca(marca);
        c.setPrecio(precio);
        c.setStock(stock);
        c.setCategoria(categoria);
        return c;
    }
}
